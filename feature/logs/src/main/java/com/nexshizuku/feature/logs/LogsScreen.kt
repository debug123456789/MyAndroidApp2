package com.nexshizuku.feature.logs

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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.nexshizuku.core.designsystem.Components.CodeBlock
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
import com.nexshizuku.core.model.LogEntry
import com.nexshizuku.core.model.LogLevel
import kotlinx.coroutines.launch

@Composable
fun LogsScreen(viewModel: LogsViewModel) {
    val entries by viewModel.filteredEntries.collectAsState()
    val filterLevel by viewModel.filterLevel.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var localSearchQuery by remember { mutableStateOf(searchQuery) }
    
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
                Text("Logs", style = MaterialTheme.typography.headlineSmall)
                Text("${entries.size} entries", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            Row {
                FilterChip(
                    label = "All",
                    selected = filterLevel == null,
                    onClick = { viewModel.setFilterLevel(null) }
                )
                
                LogLevel.values().forEach { level ->
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(Spacing.Xs))
                    FilterChip(
                        label = level.name,
                        selected = filterLevel == level,
                        onClick = { viewModel.setFilterLevel(level) },
                        color = levelColor(level)
                    )
                }
            }
        }
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Md))
        
        // Search Bar
        SearchBar(
            query = localSearchQuery,
            onChange = { viewModel.setSearchQuery(it); localSearchQuery = it },
            onClear = { viewModel.setSearchQuery(""); localSearchQuery = "" }
        )
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Md))
        
        // Error display
        error?.let { err ->
            com.nexshizuku.core.designsystem.Components.ErrorState(
                title = "Error",
                message = err
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Md))
        }
        
        // Log Entries
        if (entries.isNotEmpty()) {
            LogEntriesList(entries = entries)
        } else {
            EmptyState(
                icon = { androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.Article,
                    contentDescription = null,
                    modifier = Modifier,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )},
                title = "No Log Entries",
                message = if (searchQuery.isNotBlank() || filterLevel != null) {
                    "No entries match your filter. Try adjusting the filter or clearing search."
                } else {
                    "No log entries available. Logs will appear here when the service is running."
                },
                action = {
                    androidx.compose.material3.Button(onClick = { viewModel.loadLogs() }) {
                        Text("Refresh", style = MaterialTheme.typography.buttonMedium)
                    }
                }
            )
        }
        
        // Actions
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Md)
        ) {
            androidx.compose.material3.OutlinedButton(
                onClick = { viewModel.clearLogs() },
                modifier = Modifier.weight(1f),
                enabled = entries.isNotEmpty()
            ) {
                Text("Clear Logs", style = MaterialTheme.typography.buttonMedium)
            }
            
            androidx.compose.material3.OutlinedButton(
                onClick = { viewModel.copyAllLogs() },
                modifier = Modifier.weight(1f),
                enabled = entries.isNotEmpty()
            ) {
                Text("Copy All", style = MaterialTheme.typography.buttonMedium)
            }
        }
    }
}

@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    val colors = MaterialTheme.colorScheme
    
    androidx.compose.material3.FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = Modifier.height(32.dp),
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        colors = androidx.compose.material3.FilterChipDefaults.colors(
            selectedContainerColor = color.copy(alpha = 0.2f),
            unselectedContainerColor = colors.glassSurfaceSubtle,
            selectedLabelColor = color,
            unselectedLabelColor = colors.onSurfaceVariant,
            selectedIconColor = color,
            unselectedIconColor = colors.onSurfaceVariant,
            borderColor = if (selected) color.copy(alpha = 0.5f) else colors.glassBorder
        )
    )
}

fun levelColor(level: LogLevel): androidx.compose.ui.graphics.Color = when (level) {
    LogLevel.DEBUG -> MaterialTheme.colorScheme.onSurfaceVariant
    LogLevel.INFO -> MaterialTheme.colorScheme.primary
    LogLevel.WARNING -> MaterialTheme.colorScheme.warning
    LogLevel.ERROR -> MaterialTheme.colorScheme.error
    LogLevel.FATAL -> MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
}

@Composable
fun SearchBar(
    query: String,
    onChange: (String) -> Unit,
    onClear: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    
    GlassSurface(modifier = Modifier.fillMaxWidth(), elevation = 1) {
        androidx.compose.material3.TextField(
            value = query,
            onValueChange = onChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.Standard)
                .height(48.dp),
            placeholder = { Text("Search logs...", style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant) },
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = colors.onSurface),
            leadingIcon = { Icon(androidx.compose.material.icons.Icons.Filled.Search, contentDescription = null, tint = colors.onSurfaceVariant) },
            trailingIcon = if (query.isNotBlank()) {
                { IconButton(onClick = onClear) { Icon(androidx.compose.material.icons.Icons.Filled.Close, contentDescription = "Clear", tint = colors.onSurfaceVariant) } }
            } else null,
            colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
                containerColor = colors.glassSurfaceSubtle,
                focusedIndicatorColor = colors.primary,
                unfocusedIndicatorColor = colors.glassBorder,
                cursorColor = colors.primary
            ),
            singleLine = true
        )
    }
}

@Composable
fun LogEntriesList(entries: List<LogEntry>) {
    val colors = MaterialTheme.colorScheme
    
    GlassSurface(modifier = Modifier.fillMaxWidth().weight(1f), elevation = 1) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.Standard)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(entries) { entry ->
                LogEntryItem(entry = entry)
                if (entry != entries.last()) Divider()
            }
        }
    }
}

@Composable
fun LogEntryItem(entry: LogEntry) {
    val colors = MaterialTheme.colorScheme
    val levelColor = levelColor(entry.level)
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.Xs),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(8.dp).background(levelColor, RoundedCornerShape(4.dp))
                )
                
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(Spacing.Sm))
                
                Text(
                    text = entry.level.name,
                    style = MaterialTheme.typography.statusBadge,
                    color = levelColor
                )
                
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(Spacing.Sm))
                
                Text(
                    text = entry.tag,
                    style = MaterialTheme.typography.caption,
                    color = colors.onSurfaceVariant
                )
            }
            
            Text(
                text = entry.timestamp.formatTime(),
                style = MaterialTheme.typography.technicalSmall,
                color = colors.onSurfaceVariant
            )
        }
        
        // Message
        Text(
            text = entry.message,
            style = MaterialTheme.typography.bodyMedium.copy(color = colors.onSurface),
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        
        // Throwable
        entry.throwable?.let { throwable ->
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Xs))
            CodeBlock(text = throwable, modifier = Modifier.fillMaxWidth().padding(start = 20.dp))
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
