package com.nexshizuku.feature.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexshizuku.core.common.Result
import com.nexshizuku.core.ipc.NexShizukuClient
import com.nexshizuku.core.model.LogEntry
import com.nexshizuku.core.model.LogLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Logs Screen ViewModel
 */
class LogsViewModel(
    private val ipcClient: NexShizukuClient
) : ViewModel() {
    
    private val _entries = MutableStateFlow<List<LogEntry>>(emptyList())
    val entries = _entries.asStateFlow()
    
    private val _filterLevel = MutableStateFlow<LogLevel?>(null)
    val filterLevel = _filterLevel.asStateFlow()
    
    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery = _searchQuery.asStateFlow()
    
    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    
    init {
        loadLogs()
    }
    
    fun loadLogs() {
        // TODO: Load from local storage or service
        _entries.value = generateSampleLogs()
    }
    
    fun setFilterLevel(level: LogLevel?) {
        _filterLevel.value = level
    }
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun clearLogs() {
        _entries.value = emptyList()
    }
    
    fun copyAllLogs() {
        // TODO: Copy to clipboard
    }
    
    val filteredEntries: List<LogEntry>
        get() {
            var result = _entries.value
            val filter = _filterLevel.value
            val query = _searchQuery.value
            
            if (filter != null) {
                result = result.filter { it.level == filter }
            }
            
            if (query.isNotBlank()) {
                val lowerQuery = query.lowercase()
                result = result.filter { it.message.lowercase().contains(lowerQuery) || it.tag.lowercase().contains(lowerQuery) }
            }
            
            return result
        }
    
    private fun generateSampleLogs(): List<LogEntry> {
        return listOf(
            LogEntry(level = LogLevel.INFO, tag = "NexShizuku", message = "Application started"),
            LogEntry(level = LogLevel.INFO, tag = "Service", message = "Service connection established"),
            LogEntry(level = LogLevel.INFO, tag = "Binder", message = "API version 1 negotiated"),
            LogEntry(level = LogLevel.DEBUG, tag = "IPC", message = "Command executed: ls -la /"),
            LogEntry(level = LogLevel.INFO, tag = "Service", message = "Command completed with exit code 0"),
            LogEntry(level = LogLevel.WARNING, tag = "Permission", message = "App com.example.app requested access"),
            LogEntry(level = LogLevel.INFO, tag = "Permission", message = "Access granted for com.example.app"),
            LogEntry(level = LogLevel.ERROR, tag = "Service", message = "Failed to execute command: timeout"),
            LogEntry(level = LogLevel.INFO, tag = "Service", message = "Service disconnected"),
        )
    }
}
