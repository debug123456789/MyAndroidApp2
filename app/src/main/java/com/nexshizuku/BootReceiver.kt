package com.nexshizuku

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.nexshizuku.core.common.Logger
import com.nexshizuku.core.ipc.NexShizukuClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Boot Receiver - Auto-starts service on device boot
 */
class BootReceiver : BroadcastReceiver() {
    
    private val TAG = "BootReceiver"
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Logger.i(TAG, "Boot completed, checking auto-start preference")
            
            val prefs = context.getSharedPreferences("nexshizuku_prefs", Context.MODE_PRIVATE)
            val autoStart = prefs.getBoolean("auto_start_service", false)
            
            if (autoStart) {
                // Start service in foreground
                val serviceIntent = Intent(context, com.nexshizuku.service.NexShizukuService::class.java)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
                
                Logger.i(TAG, "Service auto-started on boot")
            }
        }
    }
}
