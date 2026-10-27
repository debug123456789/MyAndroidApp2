package com.nexshizuku.feature.terminal

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
import com.nexshizuku.core.model.CommandResult
import kotlinx.coroutines.launch

@Composable
fun TerminalScreen(viewModel: TerminalViewModel) {
    val history by viewModel.history.collectAsState()
    val isExecuting by viewModel.isExecuting.collectAsState()
    val error by viewModel.error.collectAsState()
    val availableCommands = viewModel.availableCommands
    
    var inputText by remember { mutableStateOf("") }
    var showCommands by remember { mutableStateOf(false) }
    
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
                Text("Terminal", style = MaterialTheme.typography.headlineSmall)
                Text("Execute commands on the service", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            Row {
                IconButton(onClick = { showCommands = !showCommands }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.Code,
                        contentDescription = "Commands",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = { viewModel.clearHistory() }, enabled = history.isNotEmpty()) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.DeleteSweep,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
        
        // Quick Commands
        if (showCommands) {
            QuickCommandsSection(commands = availableCommands, onExecute = { cmd ->
                viewModel.executeQuickCommand(cmd)
            })
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Md))
        }
        
        // Input Area
        InputArea(
            inputText = inputText,
            onInputChange = { inputText = it },
            onExecute = { text ->
                val parts = text.toCommandArgs()
                val command = parts.firstOrNull() ?: ""
                val args = if (parts.size > 1) parts.drop(1) else emptyList()
                viewModel.executeCommand(command, args)
                inputText = ""
            },
            isExecuting = isExecuting
        )
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Md))
        
        // Error display
        error?.let { err ->
            com.nexshizuku.core.designsystem.Components.ErrorState(
                title = "Execution Error",
                message = err
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Md))
        }
        
        // History
        if (history.isNotEmpty()) {
            HistorySection(history = history)
        } else {
            EmptyState(
                icon = { androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.Terminal,
                    contentDescription = null,
                    modifier = Modifier,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )},
                title = "No Commands Executed",
                message = "Enter a command above or select from quick commands to get started."
            )
        }
    }
}

@Composable
fun QuickCommandsSection(
    commands: List<TerminalCommand>,
    onExecute: (TerminalCommand) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    
    GlassSurface(modifier = Modifier.fillMaxWidth(), elevation = 1) {
        Column {
            SectionHeader(title = "Quick Commands")
            
            LazyColumn(
                modifier = Modifier.padding(Spacing.Standard),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(commands) { cmd ->
                    QuickCommandRow(cmd = cmd, onExecute = { onExecute(cmd) })
                    if (cmd != commands.last()) Divider()
                }
            }
        }
    }
}

@Composable
fun QuickCommandRow(cmd: TerminalCommand, onExecute: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    
    androidx.compose.material3.TextButton(
        onClick = onExecute,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.Sm, horizontal = Spacing.Standard)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (cmd.requireRoot) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.Security,
                            contentDescription = "Requires root",
                            tint = colors.warning,
                            modifier = Modifier.size(IconSize.Sm).padding(end = Spacing.Xs)
                        )
                    }
                    Text(cmd.command, style = MaterialTheme.typography.codeInline, color = colors.onSurface)
                }
                Text(cmd.description, style = MaterialTheme.typography.caption, color = colors.onSurfaceVariant)
            }
            
            Text(cmd.example, style = MaterialTheme.typography.technicalSmall, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
fun InputArea(
    inputText: String,
    onInputChange: (String) -> Unit,
    onExecute: (String) -> Unit,
    isExecuting: Boolean
) {
    val colors = MaterialTheme.colorScheme
    
    GlassSurface(modifier = Modifier.fillMaxWidth(), elevation = 1) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.Standard),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$ ",
                style = MaterialTheme.typography.technicalLarge,
                color = colors.primary
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(Spacing.Sm))
            
            androidx.compose.material3.TextField(
                value = inputText,
                onValueChange = onInputChange,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                placeholder = { Text("Enter command...", style = MaterialTheme.typography.technicalMedium, color = colors.onSurfaceVariant) },
                textStyle = MaterialTheme.typography.technicalMedium.copy(color = colors.onSurface),
                colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
                    containerColor = colors.glassSurfaceSubtle,
                    focusedIndicatorColor = colors.primary,
                    unfocusedIndicatorColor = colors.glassBorder,
                    disabledIndicatorColor = colors.glassBorder,
                    cursorColor = colors.primary
                ),
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
                    imeAction = androidx.compose.ui.text.input.ImeAction.Done
                ),
                keyboardActions = androidx.compose.ui.text.input.KeyboardActions(
                    onDone = { onExecute(inputText) }
                ),
                singleLine = true
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(Spacing.Sm))
            
            androidx.compose.material3.Button(
                onClick = { onExecute(inputText) },
                modifier = Modifier.height(48.dp),
                enabled = inputText.isNotBlank() && !isExecuting
            ) {
                if (isExecuting) {
                    CircularProgressIndicator(modifier = Modifier.size(IconSize.Sm), strokeWidth = 2.dp, color = colors.onPrimary)
                } else {
                    Icon(androidx.compose.material.icons.Icons.Filled.PlayArrow, contentDescription = "Execute", tint = colors.onPrimary)
                }
            }
        }
    }
}

@Composable
fun HistorySection(history: List<CommandResult>) {
    val colors = MaterialTheme.colorScheme
    
    GlassSurface(modifier = Modifier.fillMaxWidth().weight(1f), elevation = 1) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SectionHeader(
                title = "History (${history.size})",
                action = { /* Clear button in header */ }
            )
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.Standard)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(history.reversed()) { result ->
                    HistoryItem(result = result)
                    if (result != history.first()) Divider()
                }
            }
        }
    }
}

@Composable
fun HistoryItem(result: CommandResult) {
    val colors = MaterialTheme.colorScheme
    val isSuccess = result.isSuccess
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Command header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.Xs),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${result.requestId.take(8)} • ${result.timestamp.formatTime()} • ${result.executionTimeMs}ms",
                style = MaterialTheme.typography.caption,
                color = colors.onSurfaceVariant
            )
            
            Row {
                Icon(
                    imageVector = if (isSuccess) androidx.compose.material.icons.Icons.Filled.CheckCircle else androidx.compose.material.icons.Icons.Filled.Error,
                    contentDescription = null,
                    tint = if (isSuccess) colors.success else colors.error,
                    modifier = Modifier.size(IconSize.Sm)
                )
                
                Text(
                    text = "Exit: ${result.exitCode}",
                    style = MaterialTheme.typography.technicalSmall,
                    color = if (isSuccess) colors.success else colors.error
                )
            }
        }
        
        // Output
        if (result.stdout.isNotBlank()) {
            CodeBlock(text = result.stdout, modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.Sm))
        }
        
        if (result.stderr.isNotBlank()) {
            CodeBlock(text = result.stderr, modifier = Modifier.fillMaxWidth())
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
