package com.nexshizuku.feature.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexshizuku.core.common.Result
import com.nexshizuku.core.ipc.NexShizukuClient
import com.nexshizuku.core.model.ServiceInfo
import com.nexshizuku.core.model.ServiceCapabilities
import com.nexshizuku.core.model.BinderStatus
import com.nexshizuku.core.model.LaunchMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Service Screen ViewModel
 */
class ServiceViewModel(
    private val ipcClient: NexShizukuClient
) : ViewModel() {
    
    private val _serviceInfo = MutableStateFlow<ServiceInfo?>(null)
    val serviceInfo = _serviceInfo.asStateFlow()
    
    private val _capabilities = MutableStateFlow<ServiceCapabilities?>(null)
    val capabilities = _capabilities.asStateFlow()
    
    private val _binderStatus = MutableStateFlow<BinderStatus>(BinderStatus())
    val binderStatus = _binderStatus.asStateFlow()
    
    private val _launchModes = MutableStateFlow<List<LaunchMode>>(LaunchMode.values().toList())
    val launchModes = _launchModes.asStateFlow()
    
    private val _selectedLaunchMode = MutableStateFlow<LaunchMode>(LaunchMode.AUTO)
    val selectedLaunchMode = _selectedLaunchMode.asStateFlow()
    
    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    
    init {
        refreshData()
    }
    
    fun refreshData() {
        if (!ipcClient.isConnected) return
        
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            ipcClient.getServiceInfo().onSuccess { info ->
                _serviceInfo.value = info
            }.onFailure { error ->
                _error.value = error
            }
            
            ipcClient.getCapabilities().onSuccess { caps ->
                _capabilities.value = caps
            }
            
            ipcClient.getBinderStatus().onSuccess { status ->
                _binderStatus.value = status
            }
            
            _isLoading.value = false
        }
    }
    
    fun setLaunchMode(mode: LaunchMode) {
        _selectedLaunchMode.value = mode
        // Save preference
    }
    
    fun restartService() {
        viewModelScope.launch {
            ipcClient.requestRestart()
        }
    }
    
    fun shutdownService() {
        viewModelScope.launch {
            ipcClient.shutdown()
        }
    }
}
