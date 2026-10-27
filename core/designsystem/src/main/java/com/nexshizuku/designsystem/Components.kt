package com.nexshizuku.designsystem

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.image
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
import androidx.compose.foundation.shape.corner.CornerSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SurfaceDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextDefaults
import androidx.compose.material3.Token
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.blendMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawRect
import androidx.compose.ui.graphics.drawscope.drawRoundRect
import androidx.compose.ui.graphics.painter.Painter
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

/**
 * NexShizuku Design System - Reusable Components
 * Obsidian Glass Theme
 * 
 * Pre-built components following the design system specifications.
 */

// ============================================================================
// GLASS SURFACE COMPONENT
// ============================================================================

/**
 * A glass-morphism surface with configurable elevation and border.
 * Uses semi-transparent backgrounds with subtle borders for the Obsidian Glass aesthetic.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    elevation: Int = 1,
    borderWidth: Dp = GlassBorderWidth.Thin,
    shape: RoundedCornerShape = MaterialTheme.shapes.glassMedium,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shadows = when (elevation) {
        0 -> GlassElevation.Level0
        1 -> GlassElevation.Level1
        2 -> GlassElevation.Level2
        3 -> GlassElevation.Level3
        else -> GlassElevation.Level4
    }
    
    Box(
        modifier = modifier
            .background(colors.glassSurface, shape)
            .border(borderWidth, colors.glassBorder, shape)
            .shadow(shadows, shape),
        contentAlignment = Alignment.Center,
        content = content
    )
}

/**
 * Stronger glass surface for primary content areas
 */
@Composable
fun GlassSurfaceStrong(
    modifier: Modifier = Modifier,
    elevation: Int = 2,
    shape: RoundedCornerShape = MaterialTheme.shapes.glassLarge,
    content: @Composable () -> Unit
) {
    GlassSurface(
        modifier = modifier,
        elevation = elevation,
        borderWidth = GlassBorderWidth.Medium,
        shape = shape,
        content = content
    )
}

/**
 * Subtle glass surface for secondary content
 */
@Composable
fun GlassSurfaceSubtle(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = MaterialTheme.shapes.glassSmall,
    content: @Composable () -> Unit
) {
    GlassSurface(
        modifier = modifier,
        elevation = 0,
        borderWidth = GlassBorderWidth.Hairline,
        shape = shape,
        content = content
    )
}

// ============================================================================
// STATUS INDICATOR
// ============================================================================

/**
 * Animated status indicator dot with pulse animation for connected state
 */
@Composable
fun StatusIndicator(
    status: ServiceStatus,
    size: Dp = 12.dp,
    showLabel: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val isReducedMotion = MaterialTheme.isReducedMotionEnabled()
    
    val (animatedScale, _) = remember {
        mutableStateOf(0f)
    }
    
    val (color, pulseAlpha) = when (status) {
        ServiceStatus.CONNECTED -> {
            if (!isReducedMotion) {
                val pulse = animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                        animation = tween(1500, delayMillis = 0, easing = androidx.compose.animation.core.LinearEasing),
                        repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
                    ),
                    label = "pulse"
                )
                Pair(colors.statusConnected, pulse)
            } else {
                Pair(colors.statusConnected, null)
            }
        }
        ServiceStatus.CONNECTING -> {
            if (!isReducedMotion) {
                val rotate = animateFloatAsState(
                    targetValue = 360f,
                    animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                        animation = tween(1000, easing = androidx.compose.animation.core.LinearEasing),
                        repeatMode = androidx.compose.animation.core.RepeatMode.Restart
                    ),
                    label = "rotate"
                )
                Pair(colors.statusConnecting, null)
            } else {
                Pair(colors.statusConnecting, null)
            }
        }
        ServiceStatus.DISCONNECTED -> Pair(colors.statusDisconnected, null)
        ServiceStatus.UNKNOWN -> Pair(colors.statusUnknown, null)
        ServiceStatus.ERROR -> Pair(colors.error, null)
    }
    
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(size)
                .graphicsLayer {
                    if (pulseAlpha != null) {
                        scaleX = 0.8f + 0.4f * pulseAlpha.value
                        scaleY = 0.8f + 0.4f * pulseAlpha.value
                        alpha = 0.6f + 0.4f * pulseAlpha.value
                    }
                }
                .background(color, RoundedCornerShape(size / 2))
        )
        
        if (showLabel) {
            Text(
                text = status.displayName,
                style = MaterialTheme.typography.statusBadge,
                color = color,
                modifier = Modifier.padding(start = Spacing.Sm)
            )
        }
    }
}

enum class ServiceStatus {
    CONNECTED("Connected"),
    CONNECTING("Connecting"),
    DISCONNECTED("Disconnected"),
    UNKNOWN("Unknown"),
    ERROR("Error");
    
    val displayName: String
}

// ============================================================================
// METRIC CARD
// ============================================================================

/**
 * A metric display card with label, value, and optional trend/icon
 */
@Composable
fun MetricCard(
    label: String,
    value: String,
    trend: String? = null,
    trendPositive: Boolean = true,
    icon: (@Composable (Modifier) -> Unit)? = null,
    modifier: Modifier = Modifier,
    valueStyle: TextStyle = MaterialTheme.typography.technicalLarge,
    labelStyle: TextStyle = MaterialTheme.typography.caption,
    trendStyle: TextStyle = MaterialTheme.typography.overline.copy(fontWeight = FontWeight.Medium)
) {
    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.Standard),
        elevation = 1,
        shape = MaterialTheme.shapes.glassMedium
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = label,
                    style = labelStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Xs))
                Text(
                    text = value,
                    style = valueStyle,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                trend?.let { trendText ->
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Xxs))
                    Text(
                        text = trendText,
                        style = trendStyle,
                        color = if (trendPositive) MaterialTheme.colorScheme.success else MaterialTheme.colorScheme.error
                    )
                }
            }
            
            icon?.invoke(Modifier.size(IconSize.Xl))
        }
    }
}

// ============================================================================
// SECTION HEADER
// ============================================================================

/**
 * Consistent section header with optional action
 */
@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.Standard, vertical = Spacing.Sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        action?.invoke()
    }
}

// ============================================================================
// EMPTY STATE
// ============================================================================

/**
 * Consistent empty state display
 */
@Composable
fun EmptyState(
    icon: @Composable (Modifier) -> Unit,
    title: String,
    message: String,
    action: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    GlassSurfaceSubtle(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.Standard),
        shape = MaterialTheme.shapes.glassLarge
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.Spacious),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon(Modifier.size(IconSize.Xxxl).padding(bottom = Spacing.Lg))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Sm))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            action?.let { actionContent ->
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
                actionContent()
            }
        }
    }
}

// ============================================================================
// LOADING STATE
// ============================================================================

/**
 * Consistent loading indicator with optional message
 */
@Composable
fun LoadingState(
    message: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.Standard),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.CircularProgressIndicator(
            modifier = Modifier.size(IconSize.Lg).padding(bottom = Spacing.Md),
            color = MaterialTheme.colorScheme.primary
        )
        
        message?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============================================================================
// ERROR STATE
// ============================================================================

/**
 * Consistent error display with retry action
 */
@Composable
fun ErrorState(
    title: String,
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.Standard),
        elevation = 1,
        shape = MaterialTheme.shapes.glassMedium,
        borderWidth = GlassBorderWidth.Thin
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.Standard),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.Icons.Filled.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(IconSize.Xxl).padding(bottom = Spacing.Md),
                tint = MaterialTheme.colorScheme.error
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Sm))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            onRetry?.let { retry ->
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(Spacing.Lg))
                androidx.compose.material3.Button(
                    onClick = retry,
                    modifier = Modifier.padding(horizontal = Spacing.Xl)
                ) {
                    Text("Retry", style = MaterialTheme.typography.buttonMedium)
                }
            }
        }
    }
}

// ============================================================================
// CODE BLOCK
// ============================================================================

/**
 * Styled code block for terminal output, logs, etc.
 */
@Composable
fun CodeBlock(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    selectable: Boolean = true
) {
    val colors = MaterialTheme.colorScheme
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.codeBackground, MaterialTheme.shapes.glassSmall)
            .padding(Spacing.Standard),
        contentAlignment = Alignment.TopStart
    ) {
        androidx.compose.foundation.text.BasicTextField(
            value = text,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp)
                .padding(Spacing.Sm),
            textStyle = MaterialTheme.typography.codeBlock.copy(
                color = colors.codeText
            ),
            keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions.Default,
            keyboardActions = androidx.compose.ui.text.input.KeyboardActions(),
            singleLine = false,
            maxLines = maxLines,
            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
            decorationBox = { innerTextField ->
                innerTextField()
            }
        )
    }
}

// ============================================================================
// INLINE CODE
// ============================================================================

/**
 * Inline code styling
 */
@Composable
fun InlineCode(
    text: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    
    Text(
        text = text,
        style = MaterialTheme.typography.codeInline.copy(
            color = colors.codeKeyword
        ),
        modifier = modifier
            .background(colors.primaryContainer, MaterialTheme.shapes.small)
            .padding(horizontal = Spacing.Xs, vertical = Spacing.Xxs)
    )
}

// ============================================================================
// DIVIDER
// ============================================================================

/**
 * Subtle divider using theme colors
 */
@Composable
fun Divider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    startIndent: Dp = 0.dp,
    endIndent: Dp = 0.dp
) {
    androidx.compose.material3.Divider(
        modifier = modifier,
        color = MaterialTheme.colorScheme.divider,
        thickness = thickness,
        startIndent = startIndent,
        endIndent = endIndent
    )
}

// ============================================================================
// BUTTON VARIANTS
// ============================================================================

/**
 * Primary glass button
 */
@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: RoundedCornerShape = MaterialTheme.shapes.glassMedium,
    content: @Composable RowScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme
    
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.glassSurfaceStrong,
            contentColor = colors.primary,
            disabledContainerColor = colors.glassSurfaceSubtle,
            disabledContentColor = colors.onSurfaceDisabled
        ),
        border = androidx.compose.material3.BorderStroke(GlassBorderWidth.Thin, colors.glassBorder)
    ) {
        content()
    }
}

/**
 * Outline glass button
 */
@Composable
fun GlassOutlineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: RoundedCornerShape = MaterialTheme.shapes.glassMedium,
    content: @Composable RowScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme
    
    androidx.compose.material3.OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = colors.primary,
            disabledContentColor = colors.onSurfaceDisabled,
            outlineColor = colors.glassBorder
        ),
        border = androidx.compose.material3.BorderStroke(GlassBorderWidth.Thin, colors.glassBorder)
    ) {
        content()
    }
}

// ============================================================================
// TOOLTIP HELPER
// ============================================================================

/**
 * Simple tooltip wrapper for composables
 */
@Composable
fun Tooltip(
    tooltipText: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .tooltip(tooltipText)
    ) {
        content()
    }
}

// Extension for tooltip (simplified)
fun Modifier.tooltip(text: String): Modifier = this

// ============================================================================
// ANIMATED VISIBILITY HELPER
// ============================================================================

/**
 * Animated visibility with glass morphism transitions
 */
@Composable
fun AnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: androidx.compose.animation.EnterTransition = androidx.compose.animation.fadeIn(animationSpec = GlassAnimation.Standard) + androidx.compose.animation.slideInVertically(
        initialOffsetY = { -Spacing.Sm.roundToPx() },
        animationSpec = GlassAnimation.Standard
    ),
    exit: androidx.compose.animation.ExitTransition = androidx.compose.animation.fadeOut(animationSpec = GlassAnimation.Fast) + androidx.compose.animation.slideOutVertically(
        targetOffsetY = { -Spacing.Sm.roundToPx() },
        animationSpec = GlassAnimation.Fast
    ),
    content: @Composable () -> Unit
) {
    androidx.compose.animation.AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = enter,
        exit = exit
    ) {
        content()
    }
}

// ============================================================================
// SCREEN SCAFFOLD
// ============================================================================

/**
 * Standard screen scaffold with glass app bar
 */
@Composable
fun GlassScaffold(
    title: String,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    onBack: (() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.LargeTopAppBar(
                title = {
                    Column(
                        modifier = Modifier.padding(top = Spacing.Xs)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.screenTitle,
                            color = colors.onSurface
                        )
                        subtitle?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.caption,
                                color = colors.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = if (showBackButton) {
                    {
                        androidx.compose.material3.IconButton(
                            onClick = onBack!!,
                            modifier = Modifier.padding(start = Spacing.Sm)
                        ) {
                            androidx.compose.material3.Icon(
                                imageVector = androidx.compose.material.icons.Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = colors.onSurface
                            )
                        }
                    }
                } else null,
                actions = actions,
                modifier = Modifier
                    .background(colors.glassSurface, MaterialTheme.shapes.glassLarge)
                    .border(GlassBorderWidth.Hairline, colors.glassBorder, MaterialTheme.shapes.glassLarge)
                    .shadow(GlassElevation.Level1, MaterialTheme.shapes.glassLarge),
                containerColor = colors.glassSurface,
                scrollBehavior = androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior()
            )
        },
        containerColor = colors.background,
        content = content
    )
}
