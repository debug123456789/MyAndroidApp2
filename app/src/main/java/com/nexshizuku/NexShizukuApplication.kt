package com.nexshizuku

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.nexshizuku.core.common.Logger
import com.nexshizuku.core.ipc.NexShizukuClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * NexShizuku Application Class
 * Initializes core components and manages global coroutine scope
 */
class NexShizukuApplication : Application() {
    
    private val TAG = "NexShizukuApp"
    
    // Global coroutine scope for app-wide operations
    val appScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // IPC Client instance
    lateinit var ipcClient: NexShizukuClient
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize logger
        Logger.i(TAG, "NexShizuku Application starting")
        
        // Initialize IPC client
        ipcClient = NexShizukuClient.getInstance(this, appScope)
        
        // Observe app lifecycle for auto-connect/disconnect
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver(this))
    }
    
    override fun onTerminate() {
        Logger.i(TAG, "NexShizuku Application terminating")
        ipcClient.disconnect()
        appScope.cancel()
        super.onTerminate()
    }
    
    /**
     * Get the global IPC client
     */
    companion object {
        @Suppress("UNUSED_PARAMETER")
        fun getInstance(context: android.content.Context): NexShizukuApplication {
            return context.applicationContext as NexShizukuApplication
        }
    }
}

/**
 * App lifecycle observer for automatic service connection management
 */
class AppLifecycleObserver(private val app: NexShizukuApplication) :
    androidx.lifecycle.DefaultLifecycleObserver {
    
    override fun onStart(owner: androidx.lifecycle.LifecycleOwner) {
        // Auto-connect when app comes to foreground
        if (getAutoConnectPreference(app)) {
            app.appScope.launch {
                app.ipcClient.connect()
            }
        }
    }
    
    override fun onStop(owner: androidx.lifecycle.LifecycleOwner) {
        // Optionally disconnect when app goes to background
        // For now, keep connection alive
    }
    
    private fun getAutoConnectPreference(app: NexShizukuApplication): Boolean {
        return app.getSharedPreferences("nexshizuku_prefs", MODE_PRIVATE)
            .getBoolean("auto_connect", false)
    }
}
