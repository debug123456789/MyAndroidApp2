package com.nexshizuku.feature.terminal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexshizuku.core.common.Result
import com.nexshizuku.core.ipc.NexShizukuClient
import com.nexshizuku.core.model.CommandRequest
import com.nexshizuku.core.model.CommandResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Terminal Screen ViewModel
 */
class TerminalViewModel(
    private val ipcClient: NexShizukuClient
) : ViewModel() {
    
    private val _history = MutableStateFlow<List<CommandResult>>(emptyList())
    val history = _history.asStateFlow()
    
    private val _isExecuting = MutableStateFlow<Boolean>(false)
    val isExecuting = _isExecuting.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    
    // Predefined commands
    val availableCommands = listOf(
        TerminalCommand("ls", "List directory contents", "ls -la /"),
        TerminalCommand("pwd", "Print working directory", "pwd"),
        TerminalCommand("id", "Show current user ID", "id"),
        TerminalCommand("ps", "List processes", "ps"),
        TerminalCommand("df", "Disk usage", "df -h"),
        TerminalCommand("free", "Memory usage", "free -h"),
        TerminalCommand("uname", "System info", "uname -a"),
        TerminalCommand("whoami", "Current user", "whoami"),
    )
    
    fun executeCommand(command: String, args: List<String> = emptyList(), requireRoot: Boolean = false) {
        if (!ipcClient.isConnected) {
            _error.value = "Service not connected"
            return
        }
        
        _isExecuting.value = true
        _error.value = null
        
        val request = CommandRequest(
            command = command,
            args = args,
            requireRoot = requireRoot
        )
        
        viewModelScope.launch {
            val result = ipcClient.executeCommand(request)
            result.onSuccess { cmdResult ->
                _history.value = _history.value + cmdResult
                _isExecuting.value = false
            }.onFailure { error ->
                _error.value = error
                _isExecuting.value = false
            }
        }
    }
    
    fun executeQuickCommand(cmd: TerminalCommand) {
        val parts = cmd.command.toCommandArgs()
        val command = parts.firstOrNull() ?: ""
        val args = if (parts.size > 1) parts.drop(1) else emptyList()
        executeCommand(command, args, cmd.requireRoot)
    }
    
    fun clearHistory() {
        _history.value = emptyList()
    }
    
    fun cancelExecution() {
        // TODO: Implement cancellation
    }
}

data class TerminalCommand(
    val command: String,
    val description: String,
    val example: String,
    val requireRoot: Boolean = false
)
