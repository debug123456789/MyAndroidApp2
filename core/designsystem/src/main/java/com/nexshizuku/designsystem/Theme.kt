package com.nexshizuku.designsystem

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext

/**
 * NexShizuku Design System - Theme
 * Obsidian Glass Theme
 * 
 * Main theme entry point providing dark/light color schemes,
 * custom typography, shapes, and glass-morphism utilities.
 */

// ============================================================================
// COLOR SCHEMES
// ============================================================================

private val DarkColorScheme = darkColorScheme()
private val LightColorScheme = lightColorScheme()

// ============================================================================
// THEME COMPOSABLE
// ============================================================================

@Composable
fun NexShizukuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val typography = if (darkTheme) darkTypography() else lightTypography()
    val shapes = shapes()
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

// ============================================================================
// GLASS MORPHISM UTILITIES
// ============================================================================

/**
 * Glass surface elevation levels for consistent depth hierarchy
 */
object GlassElevation {
    val Level0 = Shadow(color = Color.Transparent, offset = androidx.compose.ui.geometry.Offset.Zero, blurRadius = 0.dp)
    val Level1 = Shadow(
        color = Color.Black.copy(alpha = 0.18),
        offset = androidx.compose.ui.geometry.Offset(0, 2),
        blurRadius = 8.dp
    )
    val Level2 = Shadow(
        color = Color.Black.copy(alpha = 0.24),
        offset = androidx.compose.ui.geometry.Offset(0, 8),
        blurRadius = 24.dp
    )
    val Level3 = Shadow(
        color = Color.Black.copy(alpha = 0.32),
        offset = androidx.compose.ui.geometry.Offset(0, 16),
        blurRadius = 48.dp
    )
    val Level4 = Shadow(
        color = Color.Black.copy(alpha = 0.40),
        offset = androidx.compose.ui.geometry.Offset(0, 24),
        blurRadius = 64.dp
    )
}

/**
 * Glass border widths
 */
object GlassBorderWidth {
    val Hairline = 0.5.dp
    val Thin = 1.dp
    val Medium = 1.5.dp
    val Thick = 2.dp
}

/**
 * Animation specifications for glass transitions
 */
object GlassAnimation {
    val Quick = androidx.compose.animation.core.tween(
        durationMillis = 150,
        easing = androidx.compose.animation.core.FastOutSlowInEasing
    )
    
    val Standard = androidx.compose.animation.core.tween(
        durationMillis = 250,
        easing = androidx.compose.animation.core.FastOutSlowInEasing
    )
    
    val Smooth = androidx.compose.animation.core.tween(
        durationMillis = 350,
        easing = androidx.compose.animation.core.FastOutSlowInEasing
    )
    
    val Spring = androidx.compose.animation.core.spring(
        dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
        stiffness = androidx.compose.animation.core.Spring.StiffnessLow
    )
}

// ============================================================================
// SPACING TOKENS
// ============================================================================

object Spacing {
    val Xxs = 2.dp
    val Xs = 4.dp
    val Sm = 8.dp
    val Md = 16.dp
    val Lg = 24.dp
    val Xl = 32.dp
    val Xxl = 48.dp
    val Xxxl = 64.dp
    
    // Semantic spacing
    val Inline = Xs
    val Compact = Sm
    val Standard = Md
    val Comfortable = Lg
    val Spacious = Xl
    val Section = Xxl
    val Screen = Xxxl
}

// ============================================================================
// ICON SIZES
// ============================================================================

object IconSize {
    val Xs = 16.dp
    val Sm = 20.dp
    val Md = 24.dp
    val Lg = 28.dp
    val Xl = 32.dp
    val Xxl = 40.dp
    val Xxxl = 48.dp
}

// ============================================================================
// ANIMATION DURATIONS
// ============================================================================

object MotionDuration {
    val Instant = 0
    val Quick = 100
    val Fast = 150
    val Standard = 250
    val Smooth = 350
    val Slow = 500
    val VerySlow = 700
}

// ============================================================================
// Z-INDEX LAYERS
// ============================================================================

object ZIndex {
    val Base = 0
    val Content = 100
    val Raised = 200
    val Overlay = 300
    val Modal = 400
    val Toast = 500
    val Dropdown = 600
    val Tooltip = 700
}

// ============================================================================
// BREAKPOINTS (for responsive design)
// ============================================================================

object Breakpoints {
    val Compact = 599 // dp
    val Medium = 839
    val Expanded = 1199
    val Large = 1599
    val ExtraLarge = 1919
}

// ============================================================================
// COMPOSITION LOCALS FOR THEME EXTENSIONS
// ============================================================================

/**
 * Local configuration for glass morphism effects
 */
data class GlassConfiguration(
    val enabled: Boolean = true,
    val blurRadius: Float = 20f,
    val saturation: Float = 1.2f,
)

val LocalGlassConfiguration = staticCompositionLocalOf {
    GlassConfiguration()
}

/**
 * Local configuration for reduced motion
 */
data class MotionConfiguration(
    val reducedMotion: Boolean = false,
)

val LocalMotionConfiguration = staticCompositionLocalOf {
    MotionConfiguration()
}

// ============================================================================
// HELPER EXTENSIONS
// ============================================================================

@Composable
fun glassConfiguration(): GlassConfiguration = LocalGlassConfiguration.current

@Composable
fun motionConfiguration(): MotionConfiguration = LocalMotionConfiguration.current

@Composable
fun isReducedMotionEnabled(): Boolean {
    val context = LocalContext.current
    val systemReducedMotion = context.resources.configuration.uiMode.and(
        android.content.res.Configuration.UI_MODE_NIGHT_MASK
    ) != 0 // Simplified check
    return motionConfiguration().reducedMotion || systemReducedMotion
}

// ============================================================================
// THEME EXTENSION PROPERTIES
// ============================================================================

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocal

/**
 * Easy access to NexShizuku theme extensions
 */
@Composable
fun MaterialTheme.glassConfig(): GlassConfiguration = glassConfiguration()

@Composable
fun MaterialTheme.motionConfig(): MotionConfiguration = motionConfiguration()

@Composable
fun MaterialTheme.spacing(): Spacing = Spacing

@Composable
fun MaterialTheme.iconSize(): IconSize = IconSize

@Composable
fun MaterialTheme.motionDuration(): MotionDuration = MotionDuration

@Composable
fun MaterialTheme.zIndex(): ZIndex = ZIndex

@Composable
fun MaterialTheme.breakpoints(): Breakpoints = Breakpoints
