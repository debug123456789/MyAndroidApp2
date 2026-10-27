package com.nexshizuku.ipc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.nexshizuku.core.common.Logger
import com.nexshizuku.core.model.Result
import com.nexshizuku.core.model.ServiceInfo
import com.nexshizuku.core.model.ServiceCapabilities
import com.nexshizuku.core.model.CommandRequest
import com.nexshizuku.core.model.CommandResult
import com.nexshizuku.core.model.BinderStatus
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * NexShizuku IPC Client
 * Manages connection to the privileged service via AIDL/Binder
 */
class NexShizukuClient private constructor(
    private val context: Context,
    private val scope: CoroutineScope
) {
    
    private val TAG = "NexShizukuClient"
    
    // Service connection state
    private val _service = AtomicReference<INexShizukuService?>(null)
    private val _isConnected = AtomicBoolean(false)
    private val _connectionState = AtomicReference<ConnectionState>(ConnectionState.DISCONNECTED)
    private val _binderStatus = AtomicReference<BinderStatus>(BinderStatus())
    
    // Pending async requests
    private val pendingCallbacks = ConcurrentHashMap<String, CompletableDeferred<CommandResult>>()
    
    // Service connection
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Logger.i(TAG, "Service connected: ${name.flattenToShortString()}")
            val binder = INexShizukuService.Stub.asInterface(service)
            _service.set(binder)
            _isConnected.set(true)
            _connectionState.set(ConnectionState.CONNECTED)
            
            // Update binder status
            updateBinderStatus()
            
            // Notify listeners
            connectionListeners.forEach { it.onConnected() }
        }
        
        override fun onServiceDisconnected(name: ComponentName) {
            Logger.w(TAG, "Service disconnected: ${name.flattenToShortString()}")
            _service.set(null)
            _isConnected.set(false)
            _connectionState.set(ConnectionState.DISCONNECTED)
            _binderStatus.set(BinderStatus(isConnected = false))
            
            // Fail pending requests
            pendingCallbacks.values.forEach { it.completeExceptionally(IllegalStateException("Service disconnected")) }
            pendingCallbacks.clear()
            
            // Notify listeners
            connectionListeners.forEach { it.onDisconnected() }
        }
        
        override fun onBindingDied(name: ComponentName) {
            Logger.e(TAG, "Binding died: ${name.flattenToShortString()}")
            onServiceDisconnected(name)
        }
    }
    
    // Connection listeners
    private val connectionListeners = mutableListOf<ConnectionListener>()
    
    // Service interface
    private val service: INexShizukuService?
        get() = _service.get()
    
    val isConnected: Boolean
        get() = _isConnected.get()
    
    val connectionState: ConnectionState
        get() = _connectionState.get()
    
    val binderStatus: BinderStatus
        get() = _binderStatus.get()
    
    /**
     * Connect to the service
     */
    suspend fun connect(): Result<Unit> {
        if (isConnected) return Result.success(Unit)
        
        _connectionState.set(ConnectionState.CONNECTING)
        connectionListeners.forEach { it.onConnecting() }
        
        return try {
            val intent = Intent("com.nexshizuku.SERVICE").apply {
                setPackage("com.nexshizuku")
            }
            
            val bound = context.bindService(
                intent,
                serviceConnection,
                Context.BIND_AUTO_CREATE | Context.BIND_FOREGROUND_SERVICE
            )
            
            if (!bound) {
                _connectionState.set(ConnectionState.ERROR)
                return Result.failure("Failed to bind to service")
            }
            
            // Wait for connection with timeout
            val connected = withTimeoutOrNull(10_000) {
                while (!isConnected) {
                    kotlinx.coroutines.delay(100)
                }
            }
            
            if (connected == null) {
                disconnect()
                return Result.failure("Connection timeout")
            }
            
            // Verify API version
            val apiVersion = service?.getApiVersion() ?: 0
            if (apiVersion < 1) {
                disconnect()
                return Result.failure("Incompatible API version: $apiVersion")
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            _connectionState.set(ConnectionState.ERROR)
            Logger.e(TAG, "Connection failed", e)
            Result.failure("Connection failed: ${e.message}", e)
        }
    }
    
    /**
     * Disconnect from the service
     */
    fun disconnect() {
        if (!isConnected) return
        
        context.unbindService(serviceConnection)
        _service.set(null)
        _isConnected.set(false)
        _connectionState.set(ConnectionState.DISCONNECTED)
        _binderStatus.set(BinderStatus(isConnected = false))
        
        pendingCallbacks.values.forEach { it.completeExceptionally(IllegalStateException("Disconnected")) }
        pendingCallbacks.clear()
        
        connectionListeners.forEach { it.onDisconnected() }
    }
    
    /**
     * Add a connection state listener
     */
    fun addConnectionListener(listener: ConnectionListener) {
        connectionListeners.add(listener)
    }
    
    /**
     * Remove a connection state listener
     */
    fun removeConnectionListener(listener: ConnectionListener) {
        connectionListeners.remove(listener)
    }
    
    /**
     * Get API version
     */
    suspend fun getApiVersion(): Result<Int> = safeExecuteSuspend {
        service?.getApiVersion() ?: throw IllegalStateException("Service not connected")
    }
    
    /**
     * Get service info
     */
    suspend fun getServiceInfo(): Result<ServiceInfo> = safeExecuteSuspend {
        val aidlInfo = service?.getServiceInfo() ?: throw IllegalStateException("Service not connected")
        convertServiceInfo(aidlInfo)
    }
    
    /**
     * Get service capabilities
     */
    suspend fun getCapabilities(): Result<ServiceCapabilities> = safeExecuteSuspend {
        val aidlCaps = service?.getCapabilities() ?: throw IllegalStateException("Service not connected")
        convertCapabilities(aidlCaps)
    }
    
    /**
     * Check if service is ready
     */
    suspend fun isReady(): Result<Boolean> = safeExecuteSuspend {
        service?.isReady() ?: false
    }
    
    /**
     * Execute a command synchronously
     */
    suspend fun executeCommand(request: CommandRequest): Result<CommandResult> = safeExecuteSuspend {
        val aidlRequest = convertCommandRequest(request)
        val aidlResult = service?.executeCommand(aidlRequest) 
            ?: throw IllegalStateException("Service not connected")
        convertCommandResult(aidlResult)
    }
    
    /**
     * Execute a command asynchronously
     */
    suspend fun executeCommandAsync(request: CommandRequest): Result<CommandResult> = safeExecuteSuspend {
        val aidlRequest = convertCommandRequest(request)
        val deferred = CompletableDeferred<CommandResult>()
        
        val requestId = request.requestId
        pendingCallbacks[requestId] = deferred
        
        try {
            val callback = object : ICommandCallback.Stub() {
                override fun onResult(result: CommandResult) {
                    pendingCallbacks.remove(requestId)?.complete(convertCommandResult(result))
                }
                
                override fun onError(error: String) {
                    pendingCallbacks.remove(requestId)?.completeExceptionally(
                        IllegalStateException(error)
                    )
                }
            }
            
            service?.executeCommandAsync(aidlRequest, callback)
            
            // Wait for result with timeout
            withTimeoutOrNull(request.timeoutMs) {
                deferred.await()
            } ?: throw IllegalStateException("Command timeout")
        } finally {
            pendingCallbacks.remove(requestId)
        }
    }
    
    /**
     * Ping the service
     */
    suspend fun ping(): Result<Long> = safeExecuteSuspend {
        service?.ping() ?: throw IllegalStateException("Service not connected")
    }
    
    /**
     * Register service callback
     */
    suspend fun registerCallback(callback: IServiceCallback): Result<Unit> = safeExecuteSuspend {
        service?.registerCallback(callback)
    }
    
    /**
     * Unregister service callback
     */
    suspend fun unregisterCallback(callback: IServiceCallback): Result<Unit> = safeExecuteSuspend {
        service?.unregisterCallback(callback)
    }
    
    /**
     * Get binder status
     */
    suspend fun getBinderStatus(): Result<BinderStatus> = safeExecuteSuspend {
        val aidlStatus = service?.getBinderStatus() ?: BinderStatus()
        convertBinderStatus(aidlStatus)
    }
    
    /**
     * Request service restart
     */
    suspend fun requestRestart(): Result<Boolean> = safeExecuteSuspend {
        service?.requestRestart() ?: false
    }
    
    /**
     * Shutdown service
     */
    suspend fun shutdown(): Result<Unit> = safeExecuteSuspend {
        service?.shutdown()
    }
    
    /**
     * Update binder status from service
     */
    private fun updateBinderStatus() {
        scope.launch {
            getBinderStatus().onSuccess { status ->
                _binderStatus.set(status)
            }.onFailure { error ->
                Logger.w(TAG, "Failed to update binder status: $error")
            }
        }
    }
    
    // Conversion functions
    private fun convertServiceInfo(aidl: com.nexshizuku.ipc.ServiceInfo): ServiceInfo {
        return ServiceInfo(
            version = aidl.version,
            apiVersion = aidl.apiVersion,
            pid = aidl.pid,
            uid = aidl.uid,
            processName = aidl.processName,
            startTime = aidl.startTime,
            capabilities = convertCapabilities(aidl.capabilities),
            isPrivileged = aidl.isPrivileged,
            selinuxContext = aidl.selinuxContext
        )
    }
    
    private fun convertCapabilities(aidl: com.nexshizuku.ipc.ServiceCapabilities): ServiceCapabilities {
        return ServiceCapabilities(
            apiVersion = aidl.apiVersion,
            supportsShell = aidl.supportsShell,
            supportsFileOps = aidl.supportsFileOps,
            supportsProcessManagement = aidl.supportsProcessManagement,
            supportsBinderCalls = aidl.supportsBinderCalls,
            maxCommandLength = aidl.maxCommandLength,
            maxOutputLength = aidl.maxOutputLength,
            supportedAbis = aidl.supportedAbis.toList(),
            features = aidl.features.toSet()
        )
    }
    
    private fun convertCommandRequest(model: CommandRequest): com.nexshizuku.ipc.CommandRequest {
        val aidl = com.nexshizuku.ipc.CommandRequest()
        aidl.command = model.command
        aidl.args = model.args.toTypedArray()
        aidl.workingDirectory = model.workingDirectory
        aidl.envKeys = model.environment.keys.toTypedArray()
        aidl.envValues = model.environment.values.toTypedArray()
        aidl.timeoutMs = model.timeoutMs
        aidl.requireRoot = model.requireRoot
        aidl.requestId = model.requestId
        return aidl
    }
    
    private fun convertCommandResult(aidl: com.nexshizuku.ipc.CommandResult): CommandResult {
        return CommandResult(
            requestId = aidl.requestId,
            exitCode = aidl.exitCode,
            stdout = aidl.stdout,
            stderr = aidl.stderr,
            executionTimeMs = aidl.executionTimeMs,
            timestamp = aidl.timestamp,
            isSuccess = aidl.isSuccess
        )
    }
    
    private fun convertBinderStatus(aidl: com.nexshizuku.ipc.BinderStatus): BinderStatus {
        return BinderStatus(
            isConnected = aidl.isConnected,
            servicePid = aidl.servicePid,
            serviceUid = aidl.serviceUid,
            apiVersion = aidl.apiVersion,
            lastPingMs = aidl.lastPingMs,
            error = aidl.error
        )
    }
    
    companion object {
        @Volatile private var INSTANCE: NexShizukuClient? = null
        
        fun getInstance(context: Context, scope: CoroutineScope): NexShizukuClient {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NexShizukuClient(context.applicationContext, scope).also { INSTANCE = it }
            }
        }
        
        fun resetInstance() {
            INSTANCE?.disconnect()
            INSTANCE = null
        }
    }
}

/**
 * Connection state enum
 */
enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}

/**
 * Connection state listener
 */
interface ConnectionListener {
    fun onConnecting()
    fun onConnected()
    fun onDisconnected()
}
