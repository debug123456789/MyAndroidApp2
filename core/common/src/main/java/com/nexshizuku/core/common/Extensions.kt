package com.nexshizuku.core.common

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Common extensions and utilities for NexShizuku
 */

// ============================================================================
// CONTEXT EXTENSIONS
// ============================================================================

fun Context.getAppVersion(): String {
    return try {
        packageManager.getPackageInfo(packageName, 0).versionName
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }
}

fun Context.getAppVersionCode(): Int {
    return try {
        packageManager.getPackageInfo(packageName, 0).versionCode
    } catch (e: PackageManager.NameNotFoundException) {
        -1
    }
}

fun Context.isSystemApp(packageName: String): Boolean {
    return try {
        val appInfo = packageManager.getApplicationInfo(packageName, 0)
        (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

fun Context.getUid(): Int = Process.myUid()

fun Context.isRootAvailable(): Boolean {
    return try {
        Runtime.getRuntime().exec("su -c id").waitFor() == 0
    } catch (e: Exception) {
        false
    }
}

fun Context.isShizukuAvailable(): Boolean {
    return try {
        packageManager.getPackageInfo("moe.shizuku.privileged.api", 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

// ============================================================================
// STRING EXTENSIONS
// ============================================================================

fun String.truncate(maxLength: Int, suffix: String = "…"): String {
    return if (length <= maxLength) this else substring(0, maxLength - suffix.length) + suffix
}

fun String.isNullOrBlank(): Boolean = this.isNullOrEmpty() || this.trim().isEmpty()

fun String.toCommandArgs(): List<String> {
    // Simple shell argument parsing - handles quoted strings
    val args = mutableListOf<String>()
    val sb = StringBuilder()
    var inQuotes = false
    var quoteChar: Char? = null
    var escaping = false
    
    for (c in this) {
        when {
            escaping -> {
                sb.append(c)
                escaping = false
            }
            c == '\\' -> escaping = true
            c == '"' || c == '\'' -> {
                if (!inQuotes) {
                    inQuotes = true
                    quoteChar = c
                } else if (c == quoteChar) {
                    inQuotes = false
                    quoteChar = null
                } else {
                    sb.append(c)
                }
            }
            c.isWhitespace() -> {
                if (inQuotes) {
                    sb.append(c)
                } else if (sb.isNotEmpty()) {
                    args.add(sb.toString())
                    sb.clear()
                }
            }
            else -> sb.append(c)
        }
    }
    
    if (sb.isNotEmpty()) args.add(sb.toString())
    return args
}

// ============================================================================
// TIME FORMATTING
// ============================================================================

private val TimeFormatters = ThreadLocal<SimpleDateFormat> {
    SimpleDateFormat("HH:mm:ss", Locale.getDefault())
}

private val DateTimeFormatters = ThreadLocal<SimpleDateFormat> {
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
}

private val RelativeTimeFormatters = ThreadLocal<SimpleDateFormat> {
    SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
}

fun Long.formatTime(): String = TimeFormatters.get().format(this)

fun Long.formatDateTime(): String = DateTimeFormatters.get().format(this)

fun Long.formatRelative(): String = RelativeTimeFormatters.get().format(this)

fun Long.formatDuration(): String {
    val hours = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) % 60
    val millis = this % 1000
    
    return when {
        hours > 0 -> String.format(Locale.getDefault(), "%dh %dm %ds", hours, minutes, seconds)
        minutes > 0 -> String.format(Locale.getDefault(), "%dm %ds", minutes, seconds)
        seconds > 0 -> String.format(Locale.getDefault(), "%d.%03ds", seconds, millis)
        else -> "${this}ms"
    }
}

fun Long.formatUptime(): String {
    val days = TimeUnit.MILLISECONDS.toDays(this)
    val hours = TimeUnit.MILLISECONDS.toHours(this) % 24
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    
    return when {
        days > 0 -> String.format(Locale.getDefault(), "%dd %dh", days, hours)
        hours > 0 -> String.format(Locale.getDefault(), "%dh %dm", hours, minutes)
        else -> "${minutes}m"
    }
}

// ============================================================================
// BYTE FORMATTING
// ============================================================================

fun Long.formatBytes(): String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    var size = this.toDouble()
    var unitIndex = 0
    
    while (size >= 1024 && unitIndex < units.lastIndex) {
        size /= 1024
        unitIndex++
    }
    
    return String.format(Locale.getDefault(), "%.1f %s", size, units[unitIndex])
}

// ============================================================================
// COROUTINE HELPERS
// ============================================================================

class ViewModelScope(private val viewModel: ViewModel) : CoroutineScope {
    override val coroutineContext: kotlinx.coroutines.CoroutineContext
        get() = SupervisorJob() + viewModel.coroutineContext
    
    fun launch(block: suspend CoroutineScope.() -> Unit) {
        launch(coroutineContext) { block() }
    }
    
    fun cancelAll() {
        coroutineContext.cancelChildren()
    }
}

@Composable
fun <T : ViewModel> rememberViewModel(factory: ViewModelProvider.Factory? = null): T {
    return viewModel(factory)
}

// ============================================================================
// STATE HELPERS
// ============================================================================

@Composable
fun <T> rememberMutableState(initialValue: T): androidx.compose.runtime.MutableState<T> {
    return remember { mutableStateOf(initialValue) }
}

@Composable
fun <T> rememberMutableState(initialValue: T, onChange: (T) -> Unit): androidx.compose.runtime.MutableState<T> {
    val state = rememberMutableState(initialValue)
    androidx.compose.runtime.DisposableEffect(state.value) {
        onChange(state.value)
        onDispose { onChange(state.value) }
    }
    return state
}

// ============================================================================
// DEVICE INFO HELPERS
// ============================================================================

object DeviceInfo {
    val manufacturer: String = Build.MANUFACTURER
    val model: String = Build.MODEL
    val androidVersion: String = Build.VERSION.RELEASE
    val apiLevel: Int = Build.VERSION.SDK_INT
    val abi: String = android.os.Build.SUPPORTED_ABIS.firstOrNull() ?: "unknown"
    val isDebuggable: Boolean = Build.TYPE == "debug"
    val fingerprint: String = Build.FINGERPRINT
    val hardware: String = Build.HARDWARE
    val device: String = Build.DEVICE
    val product: String = Build.PRODUCT
    val brand: String = Build.BRAND
    
    fun toDiagnosticInfo(context: Context): String {
        return """
            Device: $manufacturer $model
            Android: $androidVersion (API $apiLevel)
            ABI: $abi
            Fingerprint: $fingerprint
            Hardware: $hardware
            Device: $device
            Product: $product
            Brand: $brand
            Debuggable: $isDebuggable
            Root: ${context.isRootAvailable()}
            Shizuku: ${context.isShizukuAvailable()}
        """.trimIndent()
    }
}

// ============================================================================
// VALIDATION HELPERS
// ============================================================================

fun String.isValidPackageName(): Boolean {
    return android.util.Patterns.PACKAGE_NAME.matcher(this).matches()
}

fun Int.isValidUid(): Boolean {
    return this >= 0 && this <= 99999
}

fun String.isValidCommand(): Boolean {
    return isNotBlank() && length <= 4096
}

// ============================================================================
// SAFE EXECUTION
// ============================================================================

inline fun <T> safeExecute(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Exception) {
        Result.failure(e.message ?: "Unknown error", e)
    }
}

suspend fun <T> safeExecuteSuspend(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Exception) {
        Result.failure(e.message ?: "Unknown error", e)
    }
}

// ============================================================================
// LOGGING HELPER
// ============================================================================

object Logger {
    private const val TAG = "NexShizuku"
    
    fun d(tag: String, message: String) {
        android.util.Log.d(TAG, "[$tag] $message")
    }
    
    fun i(tag: String, message: String) {
        android.util.Log.i(TAG, "[$tag] $message")
    }
    
    fun w(tag: String, message: String) {
        android.util.Log.w(TAG, "[$tag] $message")
    }
    
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        android.util.Log.e(TAG, "[$tag] $message", throwable)
    }
    
    fun wtf(tag: String, message: String, throwable: Throwable? = null) {
        android.util.Log.wtf(TAG, "[$tag] $message", throwable)
    }
}
