package com.nexshizuku.feature.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawRoundRect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutDirection
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.width
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import com.nexshizuku.core.designsystem.Components.EmptyState
import com.nexshizuku.core.designsystem.Components.GlassSurface
import com.nexshizuku.core.designsystem.Components.GlassSurfaceStrong
import com.nexshizuku.core.designsystem.Components.GlassSurfaceSubtle
import com.nexshizuku.core.designsystem.Components.SectionHeader
import com.nexshizuku.core.designsystem.NexShizukuTheme
import com.nexshizuku.core.designsystem.Theme.GlassAnimation
import com.nexshizuku.core.designsystem.Theme.GlassBorderWidth
import com.nexshizuku.core.designsystem.Theme.GlassElevation
import com.nexshizuku.core.designsystem.Theme.IconSize
import com.nexshizuku.core.designsystem.Theme.Spacing
import kotlinx.coroutines.launch

@Composable
fun PermissionsScreen(viewModel: PermissionsViewModel) {
    val apps by viewModel.apps.collectAsState()
    val grants by viewModel.grants.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.Standard),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("App Permissions", style = MaterialTheme.typography.headlineSmall)
                Text("Manage which apps can access the service", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            IconButton(onClick = { viewModel.refreshData() }, enabled = !isLoading) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(IconSize.Md), strokeWidth = 2.dp)
                } else {
                    Icon(androidx.compose.material.icons.Icons.Filled.Refresh, contentDescription = "Refresh")
                }
            }
        }
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
        
        // Error display
        error?.let { err ->
            com.nexshizuku.core.designsystem.Components.ErrorState(
                title = "Error",
                message = err,
                onRetry = { viewModel.refreshData() }
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
        }
        
        // Granted Permissions
        if (grants.isNotEmpty()) {
            GrantedPermissionsSection(grants = grants)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
        }
        
        // Available Apps
        if (apps.isNotEmpty()) {
            AvailableAppsSection(apps = apps)
        } else {
            // Empty state
            EmptyState(
                icon = { androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.Apps,
                    contentDescription = null,
                    modifier = Modifier,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )},
                title = "No Apps Found",
                message = "No applications with service access found. Grant permissions from the service management screen or via ADB.",
                action = {
                    androidx.compose.material3.Button(onClick = { viewModel.refreshData() }) {
                        Text("Refresh", style = MaterialTheme.typography.buttonMedium)
                    }
                }
            )
        }
        
        // Info card
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
        InfoCard()
    }
}

@Composable
fun GrantedPermissionsSection(grants: List<com.nexshizuku.core.model.PermissionGrant>) {
    val colors = MaterialTheme.colorScheme
    
    GlassSurfaceStrong(modifier = Modifier.fillMaxWidth(), elevation = 2) {
        Column {
            SectionHeader(title = "Granted Permissions (${grants.size})")
            
            LazyColumn(
                modifier = Modifier.padding(Spacing.Standard),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(grants) { grant ->
                    PermissionGrantRow(grant = grant)
                    if (grant != grants.last()) Divider()
                }
            }
        }
    }
}

@Composable
fun PermissionGrantRow(grant: com.nexshizuku.core.model.PermissionGrant) {
    val colors = MaterialTheme.colorScheme
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.Standard),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(grant.packageName, style = MaterialTheme.typography.titleSmall, color = colors.onSurface)
            Text("UID: ${grant.uid} • Granted: ${grant.grantedAt.formatRelative()}", style = MaterialTheme.typography.caption, color = colors.onSurfaceVariant)
            Text("Permissions: ${grant.permissions.joinToString(", ")}", style = MaterialTheme.typography.caption, color = colors.onSurfaceVariant)
        }
        
        androidx.compose.material3.IconButton(
            onClick = { /* Revoke */ },
            enabled = grant.isActive
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Filled.Revoke,
                contentDescription = "Revoke",
                tint = if (grant.isActive) colors.error else colors.onSurfaceDisabled
            )
        }
    }
}

@Composable
fun AvailableAppsSection(apps: List<com.nexshizuku.core.model.AppInfo>) {
    val colors = MaterialTheme.colorScheme
    
    GlassSurface(modifier = Modifier.fillMaxWidth(), elevation = 1) {
        Column {
            SectionHeader(title = "Available Apps (${apps.size})")
            
            LazyColumn(
                modifier = Modifier.padding(Spacing.Standard),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(apps) { app ->
                    AvailableAppRow(app = app)
                    if (app != apps.last()) Divider()
                }
            }
        }
    }
}

@Composable
fun AvailableAppRow(app: com.nexshizuku.core.model.AppInfo) {
    val colors = MaterialTheme.colorScheme
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.Standard),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(IconSize.Lg)
                    .background(colors.primaryContainer, RoundedCornerShape(Spacing.Sm)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.Android,
                    contentDescription = null,
                    tint = colors.primary
                )
            }
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(Spacing.Md))
            
            Column {
                Text(app.label, style = MaterialTheme.typography.titleSmall, color = colors.onSurface)
                Text("${app.packageName} (v${app.versionName})", style = MaterialTheme.typography.caption, color = colors.onSurfaceVariant)
            }
        }
        
        androidx.compose.material3.TextButton(
            onClick = { /* Grant */ }
        ) {
            Text("Grant", style = MaterialTheme.typography.buttonSmall)
        }
    }
}

@Composable
fun InfoCard() {
    val colors = MaterialTheme.colorScheme
    
    GlassSurfaceSubtle(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.Standard)) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.InfoOutline,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.padding(top = Spacing.Xs)
                )
                
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(Spacing.Sm))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text("How to Grant Access", style = MaterialTheme.typography.titleSmall, color = colors.onSurface)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Xs))
                    Text(
                        "1. Connect to the service first\n" +
                        "2. Use ADB: adb shell sh /data/local/tmp/nexshizuku/grant.sh <package>\n" +
                        "3. Or use the Terminal screen to run grant commands\n" +
                        "4. Apps must declare the NexShizuku permission in their manifest",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun Divider() {
    androidx.compose.material3.Divider(
        color = MaterialTheme.colorScheme.divider,
        modifier = Modifier.padding(horizontal = Spacing.Standard)
    )
}
