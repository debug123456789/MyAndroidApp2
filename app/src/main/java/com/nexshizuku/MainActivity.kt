package com.nexshizuku

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen
import com.nexshizuku.core.designsystem.NexShizukuTheme
import com.nexshizuku.core.designsystem.Theme.Spacing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexshizuku.feature.home.HomeViewModel
import com.nexshizuku.feature.logs.LogsViewModel
import com.nexshizuku.feature.permissions.PermissionsViewModel
import com.nexshizuku.feature.service.ServiceViewModel
import com.nexshizuku.feature.settings.SettingsViewModel
import com.nexshizuku.feature.terminal.TerminalViewModel

/**
 * Main Activity - Entry point for NexShizuku app
 * Uses Jetpack Compose for UI with Navigation
 */
class MainActivity : ComponentActivity() {
    
    // ViewModels - created at activity level for shared state
    private val homeViewModel: HomeViewModel by viewModels()
    private val serviceViewModel: ServiceViewModel by viewModels()
    private val permissionsViewModel: PermissionsViewModel by viewModels()
    private val terminalViewModel: TerminalViewModel by viewModels()
    private val logsViewModel: LogsViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen
        SplashScreen.installSplashScreen(this)
        
        super.onCreate(savedInstanceState)
        
        // Handle intent actions
        handleIntent(intent)
        
        // Set up Compose content with navigation
        setContent {
            NexShizukuTheme {
                Surface(
                    modifier = androidx.compose.foundation.layout.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHostWithBottomBar(
                        homeViewModel = homeViewModel,
                        serviceViewModel = serviceViewModel,
                        permissionsViewModel = permissionsViewModel,
                        terminalViewModel = terminalViewModel,
                        logsViewModel = logsViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }
    
    private fun handleIntent(intent: Intent?) {
        intent?.let { intent ->
            when (intent.action) {
                Intent.ACTION_VIEW -> {
                    // Handle deep links if needed
                }
                "com.nexshizuku.ACTION_CONNECT" -> {
                    homeViewModel.connect()
                }
                "com.nexshizuku.ACTION_DISCONNECT" -> {
                    homeViewModel.disconnect()
                }
            }
        }
    }
}

@Composable
fun AppNavHostWithBottomBar(
    homeViewModel: HomeViewModel,
    serviceViewModel: ServiceViewModel,
    permissionsViewModel: PermissionsViewModel,
    terminalViewModel: TerminalViewModel,
    logsViewModel: LogsViewModel,
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()
    val startDestination = "home"
    
    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        content = { innerPadding ->
            NavHost(navController, startDestination) {
                navigation(startDestination = "home", route = "home") {
                    composable("home") {
                        com.nexshizuku.feature.home.HomeScreen(viewModel = homeViewModel)
                    }
                }
                
                navigation(startDestination = "service", route = "service") {
                    composable("service") {
                        com.nexshizuku.feature.service.ServiceScreen(viewModel = serviceViewModel)
                    }
                }
                
                navigation(startDestination = "permissions", route = "permissions") {
                    composable("permissions") {
                        com.nexshizuku.feature.permissions.PermissionsScreen(viewModel = permissionsViewModel)
                    }
                }
                
                navigation(startDestination = "terminal", route = "terminal") {
                    composable("terminal") {
                        com.nexshizuku.feature.terminal.TerminalScreen(viewModel = terminalViewModel)
                    }
                }
                
                navigation(startDestination = "logs", route = "logs") {
                    composable("logs") {
                        com.nexshizuku.feature.logs.LogsScreen(viewModel = logsViewModel)
                    }
                }
                
                navigation(startDestination = "settings", route = "settings") {
                    composable("settings") {
                        com.nexshizuku.feature.settings.SettingsScreen(viewModel = settingsViewModel)
                    }
                }
            }
        }
    )
}
