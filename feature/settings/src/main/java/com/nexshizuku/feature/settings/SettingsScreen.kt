package com.nexshizuku.feature.settings

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
import com.nexshizuku.core.designsystem.NexShizukuTheme
import com.nexshizuku.core.designsystem.Theme.GlassAnimation
import com.nexshizuku.core.designsystem.Theme.GlassBorderWidth
import com.nexshizuku.core.designsystem.Theme.GlassElevation
import com.nexshizuku.core.designsystem.Theme.IconSize
import com.nexshizuku.core.designsystem.Theme.Spacing
import com.nexshizuku.core.model.AppSettings
import com.nexshizuku.core.model.LaunchMode
import com.nexshizuku.core.model.LogLevel
import com.nexshizuku.core.model.TerminalTheme
import com.nexshizuku.core.model.ThemeMode
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val settings by viewModel.settings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    if (isLoading) {
        com.nexshizuku.core.designsystem.Components.LoadingState(message = "Loading settings...")
        return
    }
    
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
                Text("Settings", style = MaterialTheme.typography.headlineSmall)
                Text("Customize your NexShizuku experience", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
        
        // Appearance Section
        AppearanceSection(settings = settings, viewModel = viewModel)
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
        
        // Behavior Section
        BehaviorSection(settings = settings, viewModel = viewModel)
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
        
        // Terminal Section
        TerminalSection(settings = settings, viewModel = viewModel)
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
        
        // Logging Section
        LoggingSection(settings = settings, viewModel = viewModel)
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
        
        // About Section
        AboutSection(viewModel = viewModel)
    }
}

@Composable
fun AppearanceSection(settings: AppSettings, viewModel: SettingsViewModel) {
    val colors = MaterialTheme.colorScheme
    
    GlassSurfaceStrong(modifier = Modifier.fillMaxWidth(), elevation = 2) {
        Column {
            SectionHeader(title = "Appearance")
            
            Column(modifier = Modifier.padding(Spacing.Standard)) {
                // Theme
                SettingRow(
                    icon = { androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.Palette,
                        contentDescription = null,
                        tint = colors.primary
                    )},
                    title = "Theme",
                    subtitle = "Choose color scheme",
                    action = {
                        ThemeSelector(
                            selected = settings.theme,
                            onSelect = { viewModel.updateTheme(it) }
                        )
                    }
                )
                
                Divider()
                
                // Animations
                SettingRow(
                    icon = { androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.Animation,
                        contentDescription = null,
                        tint = colors.secondary
                    )},
                    title = "Animations",
                    subtitle = "Enable UI animations",
                    action = {
                        androidx.compose.material3.Switch(
                            checked = settings.animationsEnabled,
                            onCheckedChange = { viewModel.updateAnimationsEnabled(it) },
                            modifier = Modifier.padding(start = Spacing.Md)
                        )
                    }
                )
                
                Divider()
                
                // Reduced Motion
                SettingRow(
                    icon = { androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.Accessibility,
                        contentDescription = null,
                        tint = colors.tertiary
                    )},
                    title = "Reduced Motion",
                    subtitle = "Minimize animations for accessibility",
                    action = {
                        androidx.compose.material3.Switch(
                            checked = settings.reducedMotion,
                            onCheckedChange = { viewModel.updateReducedMotion(it) },
                            modifier = Modifier.padding(start = Spacing.Md)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun ThemeSelector(selected: ThemeMode, onSelect: (ThemeMode) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)
    ) {
        ThemeMode.values().forEach { mode ->
            androidx.compose.material3.TextButton(
                onClick = { onSelect(mode) },
                modifier = Modifier.weight(1f).padding(vertical = Spacing.Xs),
                enabled = selected != mode
            ) {
                Text(mode.name, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun BehaviorSection(settings: AppSettings, viewModel: SettingsViewModel) {
    val colors = MaterialTheme.colorScheme
    
    GlassSurface(modifier = Modifier.fillMaxWidth(), elevation = 1) {
        Column {
            SectionHeader(title = "Behavior")
            
            Column(modifier = Modifier.padding(Spacing.Standard)) {
                SettingRow(
                    icon = { androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.AutoFixHigh,
                        contentDescription = null,
                        tint = colors.primary
                    )},
                    title = "Auto Connect",
                    subtitle = "Connect to service on app start",
                    action = {
                        androidx.compose.material3.Switch(
                            checked = settings.autoConnect,
                            onCheckedChange = { viewModel.updateAutoConnect(it) },
                            modifier = Modifier.padding(start = Spacing.Md)
                        )
                    }
                )
                
                Divider()
                
                SettingRow(
                    icon = { androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.Launch,
                        contentDescription = null,
                        tint = colors.secondary
                    )},
                    title = "Launch Mode",
                    subtitle = "Preferred service launch method",
                    action = {
                        LaunchModeSelector(
                            selected = settings.preferredLaunchMode,
                            onSelect = { viewModel.updateLaunchMode(it) }
                        )
                    }
                )
                
                Divider()
                
                SettingRow(
                    icon = { androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.InfoOutline,
                        contentDescription = null,
                        tint = colors.tertiary
                    )},
                    title = "Technical Details",
                    subtitle = "Show detailed technical information",
                    action = {
                        androidx.compose.material3.Switch(
                            checked = settings.showTechnicalDetails,
                            onCheckedChange = { viewModel.updateTechDetails(it) },
                            modifier = Modifier.padding(start = Spacing.Md)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun LaunchModeSelector(selected: LaunchMode, onSelect: (LaunchMode) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LaunchMode.values().forEach { mode ->
            androidx.compose.material3.TextButton(
                onClick = { onSelect(mode) },
                modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.Xs),
                enabled = selected != mode
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(mode.displayName, style = MaterialTheme.typography.bodyMedium)
                    Text(mode.description, style = MaterialTheme.typography.caption, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (mode != LaunchMode.values().last()) Divider()
        }
    }
}

@Composable
fun TerminalSection(settings: AppSettings, viewModel: SettingsViewModel) {
    val colors = MaterialTheme.colorScheme
    
    GlassSurface(modifier = Modifier.fillMaxWidth(), elevation = 1) {
        Column {
            SectionHeader(title = "Terminal")
            
            Column(modifier = Modifier.padding(Spacing.Standard)) {
                SettingRow(
                    icon = { androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.FontDownload,
                        contentDescription = null,
                        tint = colors.primary
                    )},
                    title = "Font Size",
                    subtitle = "${settings.terminalFontSize.toInt()}sp",
                    action = {
                        FontSizeSlider(
                            value = settings.terminalFontSize,
                            onChange = { viewModel.updateTerminalFontSize(it) }
                        )
                    }
                )
                
                Divider()
                
                SettingRow(
                    icon = { androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.ColorLens,
                        contentDescription = null,
                        tint = colors.secondary
                    )},
                    title = "Theme",
                    subtitle = "Terminal color scheme",
                    action = {
                        TerminalThemeSelector(
                            selected = settings.terminalTheme,
                            onSelect = { viewModel.updateTerminalTheme(it) }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun FontSizeSlider(value: Float, onChange: (Float) -> Unit) {
    androidx.compose.material3.Slider(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.Md),
        valueRange = 10f..20f,
        steps = 10
    )
}

@Composable
fun TerminalThemeSelector(selected: TerminalTheme, onSelect: (TerminalTheme) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)
    ) {
        TerminalTheme.values().forEach { theme ->
            androidx.compose.material3.TextButton(
                onClick = { onSelect(theme) },
                modifier = Modifier.weight(1f).padding(vertical = Spacing.Xs),
                enabled = selected != theme
            ) {
                Text(theme.name, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun LoggingSection(settings: AppSettings, viewModel: SettingsViewModel) {
    val colors = MaterialTheme.colorScheme
    
    GlassSurface(modifier = Modifier.fillMaxWidth(), elevation = 1) {
        Column {
            SectionHeader(title = "Logging")
            
            Column(modifier = Modifier.padding(Spacing.Standard)) {
                SettingRow(
                    icon = { androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.FilterList,
                        contentDescription = null,
                        tint = colors.primary
                    )},
                    title = "Log Level",
                    subtitle = "Minimum level to record",
                    action = {
                        LogLevelSelector(
                            selected = settings.logLevel,
                            onSelect = { viewModel.updateLogLevel(it) }
                        )
                    }
                )
                
                Divider()
                
                SettingRow(
                    icon = { androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.Storage,
                        contentDescription = null,
                        tint = colors.secondary
                    )},
                    title = "Max Log Entries",
                    subtitle = "${settings.maxLogEntries} entries",
                    action = {
                        MaxLogsSelector(
                            value = settings.maxLogEntries,
                            onChange = { viewModel.updateMaxLogs(it) }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun LogLevelSelector(selected: LogLevel, onSelect: (LogLevel) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LogLevel.values().forEach { level ->
            androidx.compose.material3.TextButton(
                onClick = { onSelect(level) },
                modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.Xs),
                enabled = selected != level
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(level.name, style = MaterialTheme.typography.bodyMedium)
                    // Show color indicator
                    Box(
                        modifier = Modifier.size(16.dp).background(
                            when (level) {
                                LogLevel.DEBUG -> MaterialTheme.colorScheme.onSurfaceVariant
                                LogLevel.INFO -> MaterialTheme.colorScheme.primary
                                LogLevel.WARNING -> MaterialTheme.colorScheme.warning
                                LogLevel.ERROR -> MaterialTheme.colorScheme.error
                                LogLevel.FATAL -> MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                            },
                            RoundedCornerShape(4.dp)
                        )
                    )
                }
            }
            if (level != LogLevel.values().last()) Divider()
        }
    }
}

@Composable
fun MaxLogsSelector(value: Int, onChange: (Int) -> Unit) {
    val options = listOf(100, 500, 1000, 2000, 5000)
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)
    ) {
        options.forEach { option ->
            androidx.compose.material3.TextButton(
                onClick = { onChange(option) },
                modifier = Modifier.weight(1f).padding(vertical = Spacing.Xs),
                enabled = value != option
            ) {
                Text("$option", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun AboutSection(viewModel: SettingsViewModel) {
    val colors = MaterialTheme.colorScheme
    
    GlassSurface(modifier = Modifier.fillMaxWidth(), elevation = 1) {
        Column {
            SectionHeader(title = "About")
            
            Column(modifier = Modifier.padding(Spacing.Standard)) {
                SettingRow(
                    icon = { androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.Info,
                        contentDescription = null,
                        tint = colors.primary
                    )},
                    title = "Version",
                    subtitle = "v${viewModel.getAppVersion()} (${viewModel.getAppVersionCode()})",
                    action = { /* Copy to clipboard */ }
                )
                
                Divider()
                
                SettingRow(
                    icon = { androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.Code,
                        contentDescription = null,
                        tint = colors.secondary
                    )},
                    title = "Open Source Licenses",
                    subtitle = "View third-party licenses",
                    action = { /* Navigate to licenses */ }
                )
                
                Divider()
                
                SettingRow(
                    icon = { androidx.compose.material.icons.Icons.Filled.BugReport },
                    title = "Report Issue",
                    subtitle = "Open GitHub issues page",
                    action = { /* Open browser */ }
                )
            }
        }
    }
}

@Composable
fun SettingRow(
    icon: @Composable (Modifier) -> Unit,
    title: String,
    subtitle: String,
    action: @Composable () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon(Modifier.size(IconSize.Lg).padding(end = Spacing.Md))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = colors.onSurface)
            Text(subtitle, style = MaterialTheme.typography.caption, color = colors.onSurfaceVariant)
        }
        
        action()
    }
}

@Composable
fun Divider() {
    androidx.compose.material3.Divider(
        color = MaterialTheme.colorScheme.divider,
        modifier = Modifier.padding(horizontal = Spacing.Standard)
    )
}
