package com.nexshizuku.feature.service

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
import com.nexshizuku.core.designsystem.Components.GlassSurface
import com.nexshizuku.core.designsystem.Components.GlassSurfaceStrong
import com.nexshizuku.core.designsystem.Components.GlassSurfaceSubtle
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
import com.nexshizuku.core.model.LaunchMode
import kotlinx.coroutines.launch

@Composable
fun ServiceScreen(viewModel: ServiceViewModel) {
    val serviceInfo by viewModel.serviceInfo.collectAsState()
    val capabilities by viewModel.capabilities.collectAsState()
    val binderStatus by viewModel.binderStatus.collectAsState()
    val launchModes by viewModel.launchModes.collectAsState()
    val selectedLaunchMode by viewModel.selectedLaunchMode.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    val context = androidx.compose.ui.platform.LocalContext.current
    
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
                Text("Service Management", style = MaterialTheme.typography.headlineSmall)
                Text("Configure and monitor the privileged service", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        
        // Service Status Card
        if (serviceInfo != null) {
            ServiceStatusCard(serviceInfo = serviceInfo)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
        }
        
        // Launch Mode Selection
        LaunchModeSection(
            modes = launchModes,
            selected = selectedLaunchMode,
            onSelect = { viewModel.setLaunchMode(it) },
            enabled = !isLoading
        )
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
        
        // Capabilities
        capabilities?.let { caps ->
            CapabilitiesSection(capabilities = caps)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
        }
        
        // Binder Status
        BinderStatusSection(binderStatus = binderStatus)
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
        
        // Service Controls
        ServiceControlsSection(
            onRestart = { viewModel.restartService() },
            onShutdown = { viewModel.shutdownService() },
            enabled = !isLoading
        )
        
        // Error display
        error?.let { err ->
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
            com.nexshizuku.core.designsystem.Components.ErrorState(
                title = "Error",
                message = err
            )
        }
    }
}

@Composable
fun ServiceStatusCard(serviceInfo: com.nexshizuku.core.model.ServiceInfo) {
    val colors = MaterialTheme.colorScheme
    
    GlassSurfaceStrong(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2
    ) {
        Column(modifier = Modifier.padding(Spacing.Lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Service Status", style = MaterialTheme.typography.titleMedium)
                    Text("v${serviceInfo.version} (API ${serviceInfo.apiVersion})", style = MaterialTheme.typography.caption, color = colors.onSurfaceVariant)
                }
                
                StatusIndicator(
                    status = if (serviceInfo.isPrivileged) ServiceStatus.CONNECTED else ServiceStatus.UNKNOWN,
                    showLabel = true
                )
            }
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
            Divider()
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(listOf(
                    DetailItem("Process ID", "${serviceInfo.pid}"),
                    DetailItem("User ID", "${serviceInfo.uid}"),
                    DetailItem("Process Name", serviceInfo.processName),
                    DetailItem("Start Time", serviceInfo.startTime.formatRelative()),
                    DetailItem("SELinux Context", serviceInfo.selinuxContext),
                    DetailItem("Privileged", if (serviceInfo.isPrivileged) "Yes" else "No")
                )) { item ->
                    DetailRow(item = item)
                }
            }
        }
    }
}

@Composable
fun DetailRow(item: DetailItem) {
    val colors = MaterialTheme.colorScheme
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.Sm, horizontal = Spacing.Standard),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(item.label, style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
        Text(item.value, style = MaterialTheme.typography.technicalMedium, color = colors.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

data class DetailItem(val label: String, val value: String)

@Composable
fun LaunchModeSection(
    modes: List<LaunchMode>,
    selected: LaunchMode,
    onSelect: (LaunchMode) -> Unit,
    enabled: Boolean
) {
    val colors = MaterialTheme.colorScheme
    
    GlassSurface(modifier = Modifier.fillMaxWidth(), elevation = 1) {
        Column {
            SectionHeader(title = "Launch Mode")
            
            Column(
                modifier = Modifier.padding(Spacing.Standard)
            ) {
                modes.forEach { mode ->
                    LaunchModeRow(
                        mode = mode,
                        isSelected = mode == selected,
                        onClick = { onSelect(mode) },
                        enabled = enabled
                    )
                    if (mode != modes.last()) Divider()
                }
            }
        }
    }
}

@Composable
fun LaunchModeRow(
    mode: LaunchMode,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean
) {
    val colors = MaterialTheme.colorScheme
    
    androidx.compose.material3.TextButton(
        onClick = if (enabled) onClick else {},
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.Sm),
        enabled = enabled
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(mode.displayName, style = MaterialTheme.typography.titleSmall, color = if (enabled) colors.onSurface else colors.onSurfaceDisabled)
                Text(mode.description, style = MaterialTheme.typography.caption, color = if (enabled) colors.onSurfaceVariant else colors.onSurfaceDisabled)
            }
            
            if (isSelected) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = colors.primary
                )
            }
        }
    }
}

@Composable
fun CapabilitiesSection(capabilities: com.nexshizuku.core.model.ServiceCapabilities) {
    val colors = MaterialTheme.colorScheme
    
    GlassSurface(modifier = Modifier.fillMaxWidth(), elevation = 1) {
        Column {
            SectionHeader(title = "Capabilities")
            
            LazyColumn(
                modifier = Modifier.padding(Spacing.Standard),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(listOf(
                    CapabilityItem("Shell Commands", capabilities.supportsShell),
                    CapabilityItem("File Operations", capabilities.supportsFileOps),
                    CapabilityItem("Process Management", capabilities.supportsProcessManagement),
                    CapabilityItem("Binder Calls", capabilities.supportsBinderCalls),
                    CapabilityItem("Max Command Length", "${capabilities.maxCommandLength} chars"),
                    CapabilityItem("Max Output Length", "${capabilities.maxOutputLength / 1024} KB"),
                    CapabilityItem("Supported ABIs", capabilities.supportedAbis.joinToString(", ")),
                    CapabilityItem("Features", capabilities.features.joinToString(", "))
                )) { item ->
                    CapabilityRow(item = item)
                }
            }
        }
    }
}

data class CapabilityItem(val name: String, val value: Any)

@Composable
fun CapabilityRow(item: CapabilityItem) {
    val colors = MaterialTheme.colorScheme
    val (name, value) = item.name to item.value
    val (enabled, displayValue) = when (value) {
        is Boolean -> value to (if (value) "Available" else "Not Available")
        else -> true to value.toString()
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.Sm, horizontal = Spacing.Standard),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(name, style = MaterialTheme.typography.bodyMedium, color = if (enabled) colors.onSurface else colors.onSurfaceVariant)
        Text(displayValue, style = MaterialTheme.typography.bodyMedium, color = if (enabled) colors.onSurface else colors.onSurfaceVariant)
        
        if (value is Boolean) {
            Icon(
                imageVector = if (value) androidx.compose.material.icons.Icons.Filled.Check else androidx.compose.material.icons.Icons.Filled.Close,
                contentDescription = null,
                tint = if (value) colors.success else colors.error
            )
        }
    }
}

@Composable
fun BinderStatusSection(binderStatus: com.nexshizuku.core.model.BinderStatus) {
    val colors = MaterialTheme.colorScheme
    
    GlassSurface(modifier = Modifier.fillMaxWidth(), elevation = 1) {
        Column {
            SectionHeader(title = "Binder Connection")
            
            Column(modifier = Modifier.padding(Spacing.Standard)) {
                DetailRow(DetailItem("Connected", if (binderStatus.isConnected) "Yes" else "No"))
                Divider()
                DetailRow(DetailItem("Service PID", "${binderStatus.servicePid}"))
                Divider()
                DetailRow(DetailItem("Service UID", "${binderStatus.serviceUid}"))
                Divider()
                DetailRow(DetailItem("API Version", "${binderStatus.apiVersion}"))
                Divider()
                DetailRow(DetailItem("Last Ping", if (binderStatus.lastPingMs > 0) "${binderStatus.lastPingMs}ms ago" else "Never"))
                binderStatus.error?.let { error ->
                    Divider()
                    DetailRow(DetailItem("Error", error))
                }
            }
        }
    }
}

@Composable
fun ServiceControlsSection(
    onRestart: () -> Unit,
    onShutdown: () -> Unit,
    enabled: Boolean
) {
    val colors = MaterialTheme.colorScheme
    
    GlassSurface(modifier = Modifier.fillMaxWidth(), elevation = 1) {
        Column {
            SectionHeader(title = "Service Controls")
            
            Column(modifier = Modifier.padding(Spacing.Standard)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Md)
                ) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = if (enabled) onRestart else {},
                        modifier = Modifier.weight(1f).height(48.dp),
                        enabled = enabled
                    ) {
                        Text("Restart Service", style = MaterialTheme.typography.buttonMedium)
                    }
                    
                    androidx.compose.material3.OutlinedButton(
                        onClick = if (enabled) onShutdown else {},
                        modifier = Modifier.weight(1f).height(48.dp),
                        enabled = enabled,
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            contentColor = colors.error,
                            outlineColor = colors.error.copy(alpha = 0.5f)
                        )
                    ) {
                        Text("Shutdown", style = MaterialTheme.typography.buttonMedium)
                    }
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
