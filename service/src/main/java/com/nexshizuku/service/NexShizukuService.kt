package com.nexshizuku.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Process
import android.util.Log
import com.nexshizuku.core.common.Logger
import com.nexshizuku.core.model.ServiceCapabilities
import com.nexshizuku.core.model.ServiceInfo
import com.nexshizuku.core.model.CommandRequest
import com.nexshizuku.core.model.CommandResult
import com.nexshizuku.core.model.BinderStatus
import com.nexshizuku.ipc.BinderStatus as AidlBinderStatus
import com.nexshizuku.ipc.CommandRequest as AidlCommandRequest
import com.nexshizuku.ipc.CommandResult as AidlCommandResult
import com.nexshizuku.ipc.ICommandCallback
import com.nexshizuku.ipc.INexShizukuService
import com.nexshizuku.ipc.IServiceCallback
import com.nexshizuku.ipc.ServiceCapabilities as AidlServiceCapabilities
import com.nexshizuku.ipc.ServiceInfo as AidlServiceInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 * NexShizuku Privileged Service
 * 
 * This service runs with elevated privileges (via root/ADB/Shizuku)
 * and executes commands on behalf of the client app.
 * 
 * IMPORTANT: This service must be started with appropriate privileges.
 * On standard Android, it runs with the app's UID and has limited capabilities.
 * For full functionality, start via: adb shell am start-foreground-service ...
 * or with root: su -c "am start-foreground-service ..."
 */
class NexShizukuService : Service() {
    
    private val TAG = "NexShizukuService"
    
    // Service state
    private val _isRunning = AtomicBoolean(false)
    private val _startTime = AtomicLong(System.currentTimeMillis())
    private val _commandCount = AtomicLong(0)
    private val _callbacks = ConcurrentHashMap<IBinder, IServiceCallback>()
    private val _scope = CoroutineScope(Dispatchers.IO + Job())
    
    // Service info
    private val serviceInfo = AidlServiceInfo().apply {
        version = "1.0.0"
        apiVersion = 1
        pid = Process.myPid()
        uid = Process.myUid()
        processName = "com.nexshizuku:service"
        startTime = _startTime.get()
        capabilities = createCapabilities()
        isPrivileged = checkPrivileged()
        selinuxContext = getSelinuxContext()
    }
    
    // Binder implementation
    private val binder = object : INexShizukuService.Stub() {
        
        override fun getApiVersion(): Int = 1
        
        override fun getServiceInfo(): AidlServiceInfo = serviceInfo
        
        override fun getCapabilities(): AidlServiceCapabilities = serviceInfo.capabilities
        
        override fun isReady(): Boolean = _isRunning.get()
        
        override fun executeCommand(request: AidlCommandRequest): AidlCommandResult {
            val startTime = System.currentTimeMillis()
            _commandCount.incrementAndGet()
            
            return try {
                val modelRequest = convertRequest(request)
                val result = executeCommandSync(modelRequest)
                convertResult(result, startTime)
            } catch (e: Exception) {
                Logger.e(TAG, "Command execution failed", e)
                AidlCommandResult().apply {
                    requestId = request.requestId
                    exitCode = -1
                    stdout = ""
                    stderr = "Execution error: ${e.message}"
                    executionTimeMs = System.currentTimeMillis() - startTime
                    timestamp = System.currentTimeMillis()
                    isSuccess = false
                }
            }
        }
        
        override fun executeCommandAsync(request: AidlCommandRequest, callback: ICommandCallback) {
            _scope.launch {
                val result = executeCommand(request)
                try {
                    callback.onResult(result)
                } catch (e: Exception) {
                    Logger.e(TAG, "Async callback failed", e)
                    try {
                        callback.onError(e.message ?: "Callback error")
                    } catch (e2: Exception) {
                        // Ignore
                    }
                }
            }
        }
        
        override fun ping(): Long = System.currentTimeMillis()
        
        override fun registerCallback(callback: IServiceCallback) {
            val binder = callback.asBinder()
            _callbacks[binder] = callback
            binder.linkToDeath(DeathRecipient { _callbacks.remove(binder) }, 0)
            notifyServiceConnected(callback)
        }
        
        override fun unregisterCallback(callback: IServiceCallback) {
            _callbacks.remove(callback.asBinder())
        }
        
        override fun getBinderStatus(): AidlBinderStatus {
            return AidlBinderStatus().apply {
                isConnected = _isRunning.get()
                servicePid = Process.myPid()
                serviceUid = Process.myUid()
                apiVersion = 1
                lastPingMs = System.currentTimeMillis()
                error = null
            }
        }
        
        override fun requestRestart(): Boolean {
            // In a real implementation, this would trigger a restart
            // For now, just notify
            notifyServiceDisconnected()
            stopSelf()
            return true
        }
        
        override fun shutdown() {
            notifyServiceDisconnected()
            stopSelf()
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        Logger.i(TAG, "Service created")
        _isRunning.set(true)
        _startTime.set(System.currentTimeMillis())
        serviceInfo.startTime = _startTime.get()
        serviceInfo.pid = Process.myPid()
        serviceInfo.uid = Process.myUid()
    }
    
    override fun onBind(intent: Intent?): IBinder {
        Logger.i(TAG, "Client binding")
        return binder
    }
    
    override fun onUnbind(intent: Intent?): Boolean {
        Logger.i(TAG, "Client unbinding")
        return true
    }
    
    override fun onDestroy() {
        Logger.i(TAG, "Service destroyed")
        _isRunning.set(false)
        _scope.cancel()
        _callbacks.clear()
        super.onDestroy()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger.i(TAG, "Service started")
        return START_STICKY
    }
    
    // Command execution
    private fun executeCommandSync(request: CommandRequest): CommandResult {
        val startTime = System.currentTimeMillis()
        
        // Validate command
        if (!request.isValidCommand()) {
            return CommandResult(
                requestId = request.requestId,
                exitCode = -1,
                stdout = "",
                stderr = "Invalid command",
                executionTimeMs = System.currentTimeMillis() - startTime,
                isSuccess = false
            )
        }
        
        // Check if command requires root but we don't have it
        if (request.requireRoot && !isRootAvailable()) {
            return CommandResult(
                requestId = request.requestId,
                exitCode = -1,
                stdout = "",
                stderr = "Root access required but not available",
                executionTimeMs = System.currentTimeMillis() - startTime,
                isSuccess = false
            )
        }
        
        // Build process
        val processBuilder = ProcessBuilder().apply {
            command(request.command, *request.args.toTypedArray())
            directory(java.io.File(request.workingDirectory))
            redirectErrorStream(false)
            request.environment.forEach { (k, v) -> environment()[k] = v }
        }
        
        return try {
            val process = processBuilder.start()
            
            // Read output with timeout
            val stdout = withTimeoutOrNull(request.timeoutMs) {
                process.inputStream.readBytes().decodeToString()
            } ?: ""
            
            val stderr = withTimeoutOrNull(request.timeoutMs) {
                process.errorStream.readBytes().decodeToString()
            } ?: ""
            
            val exitCode = withTimeoutOrNull(request.timeoutMs) {
                process.waitFor()
            } ?: -1
            
            val executionTime = System.currentTimeMillis() - startTime
            
            CommandResult(
                requestId = request.requestId,
                exitCode = exitCode,
                stdout = stdout.take(1024 * 1024), // Limit output
                stderr = stderr.take(1024 * 1024),
                executionTimeMs = executionTime,
                isSuccess = exitCode == 0
            )
        } catch (e: Exception) {
            Logger.e(TAG, "Command execution error", e)
            CommandResult(
                requestId = request.requestId,
                exitCode = -1,
                stdout = "",
                stderr = "Execution error: ${e.message}",
                executionTimeMs = System.currentTimeMillis() - startTime,
                isSuccess = false
            )
        }
    }
    
    // Conversion functions
    private fun convertRequest(aidl: AidlCommandRequest): CommandRequest {
        val env = mutableMapOf<String, String>()
        for (i in aidl.envKeys.indices) {
            env[aidl.envKeys[i]] = aidl.envValues[i]
        }
        return CommandRequest(
            command = aidl.command,
            args = aidl.args.toList(),
            workingDirectory = aidl.workingDirectory,
            environment = env,
            timeoutMs = aidl.timeoutMs,
            requireRoot = aidl.requireRoot,
            requestId = aidl.requestId
        )
    }
    
    private fun convertResult(result: CommandResult, startTime: Long): AidlCommandResult {
        return AidlCommandResult().apply {
            requestId = result.requestId
            exitCode = result.exitCode
            stdout = result.stdout
            stderr = result.stderr
            executionTimeMs = result.executionTimeMs
            timestamp = result.timestamp
            isSuccess = result.isSuccess
        }
    }
    
    // Capabilities
    private fun createCapabilities(): AidlServiceCapabilities {
        return AidlServiceCapabilities().apply {
            apiVersion = 1
            supportsShell = true
            supportsFileOps = true
            supportsProcessManagement = isRootAvailable()
            supportsBinderCalls = true
            maxCommandLength = 4096
            maxOutputLength = 1024 * 1024
            supportedAbis = arrayOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86")
            features = arrayOf("shell", "file_ops")
        }
    }
    
    // Privilege checks
    private fun checkPrivileged(): Boolean {
        return isRootAvailable() || isShizukuContext()
    }
    
    private fun isRootAvailable(): Boolean {
        return try {
            Runtime.getRuntime().exec("su -c id").waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }
    
    private fun isShizukuContext(): Boolean {
        // Check if running under Shizuku
        return Build.TYPE == "userdebug" || Build.TYPE == "eng"
    }
    
    private fun getSelinuxContext(): String {
        return try {
            Runtime.getRuntime().exec("id -Z").inputStream.readBytes().decodeToString().trim()
        } catch (e: Exception) {
            "unknown"
        }
    }
    
    // Callback notifications
    private fun notifyServiceConnected(callback: IServiceCallback) {
        try {
            callback.onServiceConnected(serviceInfo)
        } catch (e: Exception) {
            Logger.e(TAG, "Callback error", e)
        }
    }
    
    private fun notifyServiceDisconnected() {
        _callbacks.values.forEach { callback ->
            try {
                callback.onServiceDisconnected()
            } catch (e: Exception) {
                Logger.e(TAG, "Disconnect callback error", e)
            }
        }
    }
    
    // Death recipient for callback cleanup
    private class DeathRecipient(private val onDeath: () -> Unit) : IBinder.DeathRecipient {
        override fun binderDied() {
            onDeath()
        }
    }
}
