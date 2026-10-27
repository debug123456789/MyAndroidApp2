package com.nexshizuku

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.nexshizuku.core.designsystem.NexShizukuTheme
import com.nexshizuku.core.designsystem.Theme.IconSize
import com.nexshizuku.core.designsystem.Theme.Spacing

@Composable
fun BottomNavBar(
    navController: NavController,
    onNavigate: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    
    val tabs = listOf(
        TabItem("home", "Home", androidx.compose.material.icons.Icons.Filled.Home),
        TabItem("service", "Service", androidx.compose.material.icons.Icons.Filled.CloudQueue),
        TabItem("terminal", "Terminal", androidx.compose.material.icons.Icons.Filled.Terminal),
        TabItem("logs", "Logs", androidx.compose.material.icons.Icons.Filled.Article),
        TabItem("settings", "Settings", androidx.compose.material.icons.Icons.Filled.Settings),
    )
    
    val currentRoute = remember { mutableStateOf("home") }
    
    // Update current route based on nav controller
    androidx.compose.runtime.DisposableEffect(navController.currentBackStackEntryAsState()) {
        val route = navController.currentBackStackEntry?.destination?.route
        if (route != null) {
            tabs.find { route.startsWith(it.route) }?.let { currentRoute.value = it.route }
        }
    }
    
    NavigationBar(
        modifier = androidx.compose.foundation.layout.Modifier
            .fillMaxWidth()
            .padding(bottom = Spacing.Standard),
        containerColor = colors.glassSurfaceStrong,
        contentColor = colors.onSurface
    ) {
        tabs.forEach { tab ->
            val isSelected = currentRoute.value == tab.route
            
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(tab.route) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = if (isSelected) colors.primary else colors.onSurfaceVariant
                    )
                },
                label = { Text(tab.label, style = MaterialTheme.typography.labelSmall) },
                alwaysShowLabel = false
            )
        }
    }
}

data class TabItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
