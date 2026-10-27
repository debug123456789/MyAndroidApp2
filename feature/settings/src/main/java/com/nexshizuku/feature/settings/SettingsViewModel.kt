package com.nexshizuku.feature.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.rxjs3
import androidx.datastore.rxjs3.RxDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexshizuku.core.common.Result
import com.nexshizuku.core.model.AppSettings
import com.nexshizuku.core.model.ThemeMode
import com.nexshizuku.core.model.LaunchMode
import com.nexshizuku.core.model.TerminalTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Settings Screen ViewModel
 */
class SettingsViewModel(private val context: Context) : ViewModel() {
    
    private val dataStore: RxDataStore<Preferences> = context.dataStore
    
    // Settings keys
    private val THEME_KEY = stringPreferencesKey("theme")
    private val ANIMATIONS_KEY = preferencesKey<Boolean>("animations")
    private val REDUCED_MOTION_KEY = preferencesKey<Boolean>("reduced_motion")
    private val LOG_LEVEL_KEY = stringPreferencesKey("log_level")
    private val MAX_LOGS_KEY = preferencesKey<Int>("max_logs")
    private val AUTO_CONNECT_KEY = preferencesKey<Boolean>("auto_connect")
    private val LAUNCH_MODE_KEY = stringPreferencesKey("launch_mode")
    private val TECH_DETAILS_KEY = preferencesKey<Boolean>("tech_details")
    private val TERMINAL_FONT_SIZE_KEY = preferencesKey<Float>("terminal_font_size")
    private val TERMINAL_THEME_KEY = stringPreferencesKey("terminal_theme")
    
    private val _settings = MutableStateFlow<AppSettings>(AppSettings())
    val settings = _settings.asStateFlow()
    
    private val _isLoading = MutableStateFlow<Boolean>(true)
    val isLoading = _isLoading.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            val prefs = dataStore.data.firstOrNull()?.await() ?: Preferences()
            
            val loadedSettings = AppSettings(
                theme = ThemeMode.valueOf(prefs[THEME_KEY] ?: ThemeMode.SYSTEM.name),
                animationsEnabled = prefs[ANIMATIONS_KEY] ?: true,
                reducedMotion = prefs[REDUCED_MOTION_KEY] ?: false,
                logLevel = com.nexshizuku.core.model.LogLevel.valueOf(prefs[LOG_LEVEL_KEY] ?: com.nexshizuku.core.model.LogLevel.INFO.name),
                maxLogEntries = prefs[MAX_LOGS_KEY] ?: 1000,
                autoConnect = prefs[AUTO_CONNECT_KEY] ?: false,
                preferredLaunchMode = LaunchMode.valueOf(prefs[LAUNCH_MODE_KEY] ?: LaunchMode.AUTO.name),
                showTechnicalDetails = prefs[TECH_DETAILS_KEY] ?: true,
                terminalFontSize = prefs[TERMINAL_FONT_SIZE_KEY] ?: 13f,
                terminalTheme = TerminalTheme.valueOf(prefs[TERMINAL_THEME_KEY] ?: TerminalTheme.DARK.name)
            )
            
            _settings.value = loadedSettings
            _isLoading.value = false
        }
    }
    
    fun updateTheme(mode: ThemeMode) {
        updateSettings { it.copy(theme = mode) }
        saveSetting(THEME_KEY, mode.name)
    }
    
    fun updateAnimationsEnabled(enabled: Boolean) {
        updateSettings { it.copy(animationsEnabled = enabled) }
        saveSetting(ANIMATIONS_KEY, enabled)
    }
    
    fun updateReducedMotion(enabled: Boolean) {
        updateSettings { it.copy(reducedMotion = enabled) }
        saveSetting(REDUCED_MOTION_KEY, enabled)
    }
    
    fun updateLogLevel(level: com.nexshizuku.core.model.LogLevel) {
        updateSettings { it.copy(logLevel = level) }
        saveSetting(LOG_LEVEL_KEY, level.name)
    }
    
    fun updateMaxLogs(max: Int) {
        updateSettings { it.copy(maxLogEntries = max) }
        saveSetting(MAX_LOGS_KEY, max)
    }
    
    fun updateAutoConnect(enabled: Boolean) {
        updateSettings { it.copy(autoConnect = enabled) }
        saveSetting(AUTO_CONNECT_KEY, enabled)
    }
    
    fun updateLaunchMode(mode: LaunchMode) {
        updateSettings { it.copy(preferredLaunchMode = mode) }
        saveSetting(LAUNCH_MODE_KEY, mode.name)
    }
    
    fun updateTechDetails(enabled: Boolean) {
        updateSettings { it.copy(showTechnicalDetails = enabled) }
        saveSetting(TECH_DETAILS_KEY, enabled)
    }
    
    fun updateTerminalFontSize(size: Float) {
        updateSettings { it.copy(terminalFontSize = size) }
        saveSetting(TERMINAL_FONT_SIZE_KEY, size)
    }
    
    fun updateTerminalTheme(theme: TerminalTheme) {
        updateSettings { it.copy(terminalTheme = theme) }
        saveSetting(TERMINAL_THEME_KEY, theme.name)
    }
    
    private fun updateSettings(block: (AppSettings) -> AppSettings) {
        _settings.update { block(it) }
    }
    
    private suspend fun saveSetting<T>(key: androidx.datastore.preferences.core.Preferences.Key<T>, value: T) {
        dataStore.edit { it.put(key, value) }.await()
    }
    
    fun getAppVersion(): String {
        return context.getAppVersion()
    }
    
    fun getAppVersionCode(): Int {
        return context.getAppVersionCode()
    }
}
