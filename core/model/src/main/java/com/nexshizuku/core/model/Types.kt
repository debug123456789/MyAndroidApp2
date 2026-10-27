package com.nexshizuku.core.model

import kotlinx.serialization.Serializable

/**
 * Service connection state
 */
enum class ServiceState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR,
    UNKNOWN
}

/**
 * Service launch mode
 */
enum class LaunchMode {
    AUTO("Auto", "Automatically determine best launch method"),
    ADB("ADB", "Launch via ADB shell"),
    ROOT("Root", "Launch with root privileges"),
    SHIZUKU("Shizuku", "Launch via Shizuku service");
    
    val displayName: String
    val description: String
}

/**
 * Service capabilities
 */
@Serializable
data class ServiceCapabilities(
    val apiVersion: Int = 1,
    val supportsShell: Boolean = true,
    val supportsFileOps: Boolean = true,
    val supportsProcessManagement: Boolean = false,
    val supportsBinderCalls: Boolean = true,
    val maxCommandLength: Int = 4096,
    val maxOutputLength: Int = 1024 * 1024, // 1MB
    val supportedAbis: List<String> = listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86"),
    val features: Set<String> = emptySet(),
)

/**
 * Service info
 */
@Serializable
data class ServiceInfo(
    val version: String = "1.0.0",
    val apiVersion: Int = 1,
    val pid: Int = -1,
    val uid: Int = -1,
    val processName: String = "",
    val startTime: Long = 0,
    val capabilities: ServiceCapabilities = ServiceCapabilities(),
    val isPrivileged: Boolean = false,
    val selinuxContext: String = "",
)

/**
 * Command execution request
 */
@Serializable
data class CommandRequest(
    val command: String,
    val args: List<String> = emptyList(),
    val workingDirectory: String = "/",
    val environment: Map<String, String> = emptyMap(),
    val timeoutMs: Long = 30000,
    val requireRoot: Boolean = false,
    val requestId: String = java.util.UUID.randomUUID().toString(),
)

/**
 * Command execution result
 */
@Serializable
data class CommandResult(
    val requestId: String,
    val exitCode: Int,
    val stdout: String,
    val stderr: String,
    val executionTimeMs: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val isSuccess: Boolean = exitCode == 0,
)

/**
 * Permission grant entry
 */
@Serializable
data class PermissionGrant(
    val packageName: String,
    val uid: Int,
    val grantedAt: Long = System.currentTimeMillis(),
    val grantedBy: String = "user",
    val permissions: Set<String> = setOf("shell"),
    val isActive: Boolean = true,
)

/**
 * Log entry
 */
@Serializable
data class LogEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val level: LogLevel = LogLevel.INFO,
    val tag: String,
    val message: String,
    val throwable: String? = null,
    val context: Map<String, String> = emptyMap(),
)

enum class LogLevel {
    DEBUG(0),
    INFO(1),
    WARNING(2),
    ERROR(3),
    FATAL(4);
    
    val priority: Int
}

/**
 * Diagnostic info
 */
@Serializable
data class DiagnosticInfo(
    val androidVersion: String,
    val apiLevel: Int,
    val abi: String,
    val deviceModel: String,
    val deviceManufacturer: String,
    val isRootAvailable: Boolean,
    val isShizukuAvailable: Boolean,
    val isAdbAvailable: Boolean,
    val binderStatus: BinderStatus,
    val availableTools: List<String>,
    val lastCheckTimestamp: Long = System.currentTimeMillis(),
    val recommendations: List<String> = emptyList(),
)

/**
 * Binder connection status
 */
@Serializable
data class BinderStatus(
    val isConnected: Boolean = false,
    val servicePid: Int = -1,
    val serviceUid: Int = -1,
    val apiVersion: Int = 0,
    val lastPingMs: Long = 0,
    val error: String? = null,
)

/**
 * App info for permission management
 */
@Serializable
data class AppInfo(
    val packageName: String,
    val label: String,
    val uid: Int,
    val versionName: String,
    val versionCode: Int,
    val icon: String? = null, // Base64 or resource reference
    val isSystemApp: Boolean = false,
    val isEnabled: Boolean = true,
)

/**
 * Settings model
 */
@Serializable
data class AppSettings(
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val animationsEnabled: Boolean = true,
    val reducedMotion: Boolean = false,
    val logLevel: LogLevel = LogLevel.INFO,
    val maxLogEntries: Int = 1000,
    val autoConnect: Boolean = false,
    val preferredLaunchMode: LaunchMode = LaunchMode.AUTO,
    val showTechnicalDetails: Boolean = true,
    val terminalFontSize: Float = 13f,
    val terminalTheme: TerminalTheme = TerminalTheme.DARK,
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

enum class TerminalTheme {
    DARK, LIGHT, SOLARIZED_DARK, SOLARIZED_LIGHT, MONOKAI
}

/**
 * Event for UI communication
 */
sealed interface UiEvent {
    data class ShowSnackbar(val message: String, val action: String? = null, val onAction: (() -> Unit)? = null) : UiEvent
    data class ShowDialog(val title: String, val message: String, val confirmText: String, val dismissText: String, val onConfirm: () -> Unit) : UiEvent
    data class NavigateTo(val route: String) : UiEvent
    data class RequestPermission(val permission: String, val rationale: String) : UiEvent
    object RefreshData : UiEvent
    object ClearLogs : UiEvent
    data class CopyToClipboard(val text: String, val label: String) : UiEvent
}

/**
 * Result wrapper for operations
 */
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Failure(val error: String, val throwable: Throwable? = null, val recoverable: Boolean = true) : Result<Nothing>
    
    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun <T> failure(error: String, throwable: Throwable? = null, recoverable: Boolean = true): Result<T> = Failure(error, throwable, recoverable)
    }
}
