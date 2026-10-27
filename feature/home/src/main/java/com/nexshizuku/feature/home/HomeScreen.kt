package com.nexshizuku.feature.home

import androidx.compose.animation.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import com.nexshizuku.core.designsystem.Components.GlassSurface
import com.nexshizuku.core.designsystem.Components.GlassSurfaceStrong
import com.nexshizuku.core.designsystem.Components.GlassSurfaceSubtle
import com.nexshizuku.core.designsystem.Components.MetricCard
import com.nexshizuku.core.designsystem.Components.SectionHeader
import com.nexshizuku.core.designsystem.Components.StatusIndicator
import com.nexshizuku.core.designsystem.Components.StatusIndicator.Companion.ServiceStatus
import com.nexshizuku.core.designsystem.Components.StatusIndicator.ServiceStatus
import com.nexshizuku.core.designsystem.NexShizukuTheme
import com.nexshizuku.core.designsystem.Theme.GlassAnimation
import com.nexshizuku.core.designsystem.Theme.GlassBorderWidth
import com.nexshizuku.core.designsystem.Theme.GlassElevation
import com.nexshizuku.core.designsystem.Theme.IconSize
import com.nexshizuku.core.designsystem.Theme.Spacing
import com.nexshizuku.core.model.ServiceState
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val serviceState by viewModel.serviceState.collectAsState()
    val serviceInfo by viewModel.serviceInfo.collectAsState()
    val binderStatus by viewModel.binderStatus.collectAsState()
    val lastCheck by viewModel.lastCheck.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Status indicator animation
    val pulseAnimation = androidx.compose.animation.animateFloatAsState(
        targetValue = if (serviceState == ServiceState.CONNECTED) 1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, delayMillis = 0, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    // Main content
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.TopCenter
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.Standard, vertical = Spacing.Standard),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            HeaderSection(
                serviceState = serviceState,
                serviceInfo = serviceInfo,
                pulseAnimation = pulseAnimation,
                onSettingsClick = { /* Navigate to settings */ },
                onConnectClick = { viewModel.connect() },
                onDisconnectClick = { viewModel.disconnect() },
                isLoading = isLoading
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
            
            // Main Status Card
            MainStatusCard(
                serviceState = serviceState,
                serviceInfo = serviceInfo,
                error = error,
                onConnectClick = { viewModel.connect() },
                onDisconnectClick = { viewModel.disconnect() },
                isLoading = isLoading
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
            
            // Metrics Grid
            MetricsSection(
                serviceInfo = serviceInfo,
                binderStatus = binderStatus,
                lastCheck = lastCheck
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
            
            // Quick Actions
            QuickActionsSection(
                onTerminalClick = { /* Navigate to terminal */ },
                onPermissionsClick = { /* Navigate to permissions */ },
                onLogsClick = { /* Navigate to logs */ },
                onDiagnosticsClick = { /* Navigate to diagnostics */ },
                serviceState = serviceState
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Xxl))
        }
    }
}

@Composable
fun HeaderSection(
    serviceState: ServiceState,
    serviceInfo: com.nexshizuku.core.model.ServiceInfo?,
    pulseAnimation: androidx.compose.animation.core.AnimationState<Float>,
    onSettingsClick: () -> Unit,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    isLoading: Boolean
) {
    val colors = MaterialTheme.colorScheme
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo and Title
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon / Logo
            Box(
                modifier = Modifier.size(IconSize.Xxl),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.CloudQueue,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = 0.9f + 0.1f * pulseAnimation.value
                            scaleY = 0.9f + 0.1f * pulseAnimation.value
                        }
                )
            }
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(Spacing.Md))
            
            Column {
                Text(
                    text = "NexShizuku",
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.onSurface
                )
                Text(
                    text = "v${context.packageManager.getPackageInfo(context.packageName, 0).versionName}",
                    style = MaterialTheme.typography.caption,
                    color = colors.onSurfaceVariant
                )
            }
        }
        
        // Status Indicator and Actions
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Connection Status Badge
            StatusIndicator(
                status = when (serviceState) {
                    ServiceState.CONNECTED -> ServiceStatus.CONNECTED
                    ServiceState.CONNECTING -> ServiceStatus.CONNECTING
                    ServiceState.DISCONNECTED -> ServiceStatus.DISCONNECTED
                    ServiceState.ERROR -> ServiceStatus.ERROR
                    else -> ServiceStatus.UNKNOWN
                },
                size = 10.dp,
                showLabel = true
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(Spacing.Md))
            
            // Settings Button
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = colors.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MainStatusCard(
    serviceState: ServiceState,
    serviceInfo: com.nexshizuku.core.model.ServiceInfo?,
    error: String?,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    isLoading: Boolean
) {
    val colors = MaterialTheme.colorScheme
    
    GlassSurfaceStrong(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.Standard),
        elevation = 2
    ) {
        Column(
            modifier = Modifier.padding(Spacing.Lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Large Status Text
            Text(
                text = when (serviceState) {
                    ServiceState.CONNECTED -> "Connected"
                    ServiceState.CONNECTING -> "Connecting..."
                    ServiceState.DISCONNECTED -> "Disconnected"
                    ServiceState.ERROR -> "Error"
                    else -> "Unknown"
                },
                style = MaterialTheme.typography.statusLarge,
                color = when (serviceState) {
                    ServiceState.CONNECTED -> colors.statusConnected
                    ServiceState.CONNECTING -> colors.statusConnecting
                    ServiceState.DISCONNECTED -> colors.statusDisconnected
                    ServiceState.ERROR -> colors.error
                    else -> colors.statusUnknown
                }
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Sm))
            
            // Description
            Text(
                text = viewModel.getStateDescription(),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            
            // Service details if connected
            serviceInfo?.let { info ->
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Md))
                Divider()
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Md))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    DetailItem("API Version", "v${info.apiVersion}")
                    DetailItem("Process ID", "${info.pid}")
                    DetailItem("UID", "${info.uid}")
                    DetailItem("Privileged", if (info.isPrivileged) "Yes" else "No")
                }
            }
            
            // Error display
            error?.let { err ->
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Md))
                Divider()
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Md))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = err,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.error,
                        maxLines = 3
                    )
                }
            }
            
            // Action Buttons
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Md)
            ) {
                when (serviceState) {
                    ServiceState.CONNECTED, ServiceState.ERROR -> {
                        androidx.compose.material3.Button(
                            onClick = onDisconnectClick,
                            modifier = Modifier.weight(1f),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = colors.glassSurfaceSubtle,
                                contentColor = colors.onSurface
                            )
                        ) {
                            Text("Disconnect", style = MaterialTheme.typography.buttonMedium)
                        }
                        
                        androidx.compose.material3.OutlinedButton(
                            onClick = { viewModel.refreshData() },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(IconSize.Sm),
                                    strokeWidth = 2.dp,
                                    color = colors.primary
                                )
                            } else {
                                Text("Refresh", style = MaterialTheme.typography.buttonMedium)
                            }
                        }
                    }
                    else -> {
                        androidx.compose.material3.Button(
                            onClick = onConnectClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(IconSize.Sm),
                                    strokeWidth = 2.dp,
                                    color = colors.onPrimary
                                )
                            } else {
                                Text("Connect to Service", style = MaterialTheme.typography.buttonLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.technicalMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun MetricsSection(
    serviceInfo: com.nexshizuku.core.model.ServiceInfo?,
    binderStatus: com.nexshizuku.core.model.BinderStatus,
    lastCheck: Long
) {
    val colors = MaterialTheme.colorScheme
    
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.Standard),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(listOf(
            MetricData(
                label = "Binder Status",
                value = if (binderStatus.isConnected) "Connected" else "Disconnected",
                trend = if (binderStatus.isConnected) "Stable" else null,
                trendPositive = binderStatus.isConnected,
                icon = { androidx.compose.material3.Icon(
                    imageVector = if (binderStatus.isConnected) 
                        androidx.compose.material.icons.Icons.Filled.Link 
                    else 
                        androidx.compose.material.icons.Icons.Filled.LinkOff,
                    contentDescription = null,
                    modifier = Modifier,
                    tint = if (binderStatus.isConnected) colors.statusConnected else colors.statusDisconnected
                )}
            ),
            MetricData(
                label = "Privilege Level",
                value = if (serviceInfo?.isPrivileged == true) "Privileged" else "Standard",
                trend = if (serviceInfo?.isPrivileged == true) "Full Access" else "Limited",
                trendPositive = serviceInfo?.isPrivileged == true,
                icon = { androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.Security,
                    contentDescription = null,
                    modifier = Modifier,
                    tint = if (serviceInfo?.isPrivileged == true) colors.statusConnected else colors.statusUnknown
                )}
            ),
            MetricData(
                label = "Last Check",
                value = if (lastCheck > 0) lastCheck.formatRelative() else "Never",
                trend = null,
                trendPositive = true,
                icon = { androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.AccessTime,
                    contentDescription = null,
                    modifier = Modifier,
                    tint = colors.onSurfaceVariant
                )}
            ),
            MetricData(
                label = "API Version",
                value = "v${serviceInfo?.apiVersion ?: 0}",
                trend = null,
                trendPositive = true,
                icon = { androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.Api,
                    contentDescription = null,
                    modifier = Modifier,
                    tint = colors.onSurfaceVariant
                )}
            )
        )) { metric ->
            MetricCard(
                label = metric.label,
                value = metric.value,
                trend = metric.trend,
                trendPositive = metric.trendPositive,
                icon = metric.icon,
                modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.Xs)
            )
        }
    }
}

data class MetricData(
    val label: String,
    val value: String,
    val trend: String? = null,
    val trendPositive: Boolean = true,
    val icon: (@Composable (Modifier) -> Unit)? = null
)

@Composable
fun QuickActionsSection(
    onTerminalClick: () -> Unit,
    onPermissionsClick: () -> Unit,
    onLogsClick: () -> Unit,
    onDiagnosticsClick: () -> Unit,
    serviceState: ServiceState
) {
    val colors = MaterialTheme.colorScheme
    val isConnected = serviceState == ServiceState.CONNECTED
    
    SectionHeader(title = "Quick Actions")
    
    GlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.Standard),
        elevation = 1
    ) {
        Column {
            ActionRow(
                icon = { androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.Terminal,
                    contentDescription = null,
                    tint = colors.primary
                )},
                title = "Terminal",
                subtitle = "Execute commands",
                enabled = isConnected,
                onClick = onTerminalClick
            )
            
            Divider()
            
            ActionRow(
                icon = { androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.Apps,
                    contentDescription = null,
                    tint = colors.secondary
                )},
                title = "Permissions",
                subtitle = "Manage app access",
                enabled = isConnected,
                onClick = onPermissionsClick
            )
            
            Divider()
            
            ActionRow(
                icon = { androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.Article,
                    contentDescription = null,
                    tint = colors.tertiary
                )},
                title = "Logs",
                subtitle = "View event history",
                enabled = true,
                onClick = onLogsClick
            )
            
            Divider()
            
            ActionRow(
                icon = { androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.MedicalInformation,
                    contentDescription = null,
                    tint = colors.warning
                )},
                title = "Diagnostics",
                subtitle = "System information",
                enabled = true,
                onClick = onDiagnosticsClick
            )
        }
    }
}

@Composable
fun ActionRow(
    icon: @Composable (Modifier) -> Unit,
    title: String,
    subtitle: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val contentColor = if (enabled) colors.onSurface else colors.onSurfaceDisabled
    
    androidx.compose.material3.TextButton(
        onClick = if (enabled) onClick else {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.Standard),
        enabled = enabled
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon(Modifier.size(IconSize.Lg).padding(end = Spacing.Md))
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = contentColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.caption,
                    color = if (enabled) colors.onSurfaceVariant else colors.onSurfaceDisabled
                )
            }
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
            
            if (enabled) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = colors.onSurfaceVariant
                )
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
