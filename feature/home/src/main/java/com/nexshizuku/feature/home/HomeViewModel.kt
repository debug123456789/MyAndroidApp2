package com.nexshizuku.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexshizuku.core.common.Result
import com.nexshizuku.core.common.safeExecuteSuspend
import com.nexshizuku.core.ipc.NexShizukuClient
import com.nexshizuku.core.model.ServiceInfo
import com.nexshizuku.core.model.ServiceState
import com.nexshizuku.core.model.BinderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Home Screen ViewModel
 * Manages service connection state and UI data
 */
class HomeViewModel(
    private val ipcClient: NexShizukuClient
) : ViewModel() {
    
    // Connection state
    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.UNKNOWN)
    val serviceState = _serviceState.asStateFlow()
    
    // Service info
    private val _serviceInfo = MutableStateFlow<ServiceInfo?>(null)
    val serviceInfo = _serviceInfo.asStateFlow()
    
    // Binder status
    private val _binderStatus = MutableStateFlow<BinderStatus>(BinderStatus())
    val binderStatus = _binderStatus.asStateFlow()
    
    // Last check timestamp
    private val _lastCheck = MutableStateFlow<Long>(0)
    val lastCheck = _lastCheck.asStateFlow()
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()
    
    init {
        observeConnection()
        refreshData()
    }
    
    /**
     * Observe IPC client connection state
     */
    private fun observeConnection() {
        viewModelScope.launch {
            ipcClient.addConnectionListener(object : NexShizukuClient.ConnectionListener {
                override fun onConnecting() {
                    _serviceState.value = ServiceState.CONNECTING
                }
                
                override fun onConnected() {
                    _serviceState.value = ServiceState.CONNECTED
                    refreshData()
                }
                
                override fun onDisconnected() {
                    _serviceState.value = ServiceState.DISCONNECTED
                    _serviceInfo.value = null
                    _binderStatus.value = BinderStatus(isConnected = false)
                }
            })
            
            // Initial state
            _serviceState.value = when {
                ipcClient.isConnected -> ServiceState.CONNECTED
                else -> ServiceState.DISCONNECTED
            }
        }
    }
    
    /**
     * Refresh all data from service
     */
    fun refreshData() {
        if (!ipcClient.isConnected) return
        
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            // Fetch service info
            val infoResult = ipcClient.getServiceInfo()
            infoResult.onSuccess { info ->
                _serviceInfo.value = info
                _lastCheck.value = System.currentTimeMillis()
            }.onFailure { error ->
                _error.value = error
            }
            
            // Fetch binder status
            val binderResult = ipcClient.getBinderStatus()
            binderResult.onSuccess { status ->
                _binderStatus.value = status
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Connect to service
     */
    fun connect() {
        if (ipcClient.isConnected) return
        
        _serviceState.value = ServiceState.CONNECTING
        _error.value = null
        
        viewModelScope.launch {
            val result = ipcClient.connect()
            result.onFailure { error ->
                _serviceState.value = ServiceState.ERROR
                _error.value = error
            }.onSuccess {
                _serviceState.value = ServiceState.CONNECTED
                refreshData()
            }
        }
    }
    
    /**
     * Disconnect from service
     */
    fun disconnect() {
        ipcClient.disconnect()
        _serviceState.value = ServiceState.DISCONNECTED
        _serviceInfo.value = null
        _binderStatus.value = BinderStatus(isConnected = false)
    }
    
    /**
     * Request service restart
     */
    fun requestRestart() {
        viewModelScope.launch {
            val result = ipcClient.requestRestart()
            result.onSuccess {
                // Service will disconnect, then we can reconnect
                refreshData()
            }.onFailure { error ->
                _error.value = "Restart failed: $error"
            }
        }
    }
    
    /**
     * Get current service state as string
     */
    fun getStateDescription(): String {
        return when (_serviceState.value) {
            ServiceState.CONNECTED -> "Service connected and ready"
            ServiceState.CONNECTING -> "Connecting to service..."
            ServiceState.DISCONNECTED -> "Service not connected. Tap to connect."
            ServiceState.ERROR -> "Connection error: ${_error.value ?: "Unknown"}"
            ServiceState.UNKNOWN -> "Checking service status..."
        }
    }
}
