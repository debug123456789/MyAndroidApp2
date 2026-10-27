package com.nexshizuku.designsystem

import androidx.compose.ui.graphics.Color

/**
 * NexShizuku Design System - Color Tokens
 * Obsidian Glass Theme
 * 
 * A premium dark-first color system with deep graphite surfaces,
 * electric blue accents, and glass-morphism effects.
 */

// ============================================================================
// PRIMITIVE COLORS (Raw values)
// ============================================================================

// Base neutrals - Deep graphite foundation
private val Gray50 = Color(0xFFF8FAFC)
private val Gray100 = Color(0xFFF1F5F9)
private val Gray200 = Color(0xFFE2E8F0)
private val Gray300 = Color(0xFFCBD5E1)
private val Gray400 = Color(0xFF94A3B8)
private val Gray500 = Color(0xFF64748B)
private val Gray600 = Color(0xFF475569)
private val Gray700 = Color(0xFF334155)
private val Gray800 = Color(0xFF1E293B)
private val Gray900 = Color(0xFF0F172A)
private val Gray950 = Color(0xFF020617)

// Accent - Electric Blue
private val Blue50 = Color(0xFFEFF6FF)
private val Blue100 = Color(0xFFDBEAFE)
private val Blue200 = Color(0xFFBFDBFE)
private val Blue300 = Color(0xFF93C5FD)
private val Blue400 = Color(0xFF60A5FA)
private val Blue500 = Color(0xFF3B82F6)
private val Blue600 = Color(0xFF2563EB)
private val Blue700 = Color(0xFF1D4ED8)
private val Blue800 = Color(0xFF1E40AF)
private val Blue900 = Color(0xFF1E3A8A)
private val Blue950 = Color(0xFF172554)

// Secondary Accent - Muted Purple
private val Purple50 = Color(0xFFFAF5FF)
private val Purple100 = Color(0xFFF3E8FF)
private val Purple200 = Color(0xFFE9D5FF)
private val Purple300 = Color(0xFFD8B4FE)
private val Purple400 = Color(0xFFC084FC)
private val Purple500 = Color(0xFFA855F7)
private val Purple600 = Color(0xFF9333EA)
private val Purple700 = Color(0xFF7E22CE)
private val Purple800 = Color(0xFF6B21A8)
private val Purple900 = Color(0xFF581C87)
private val Purple950 = Color(0xFF3B0764)

// Semantic - Success (Soft Green)
private val Green50 = Color(0xFFF0FDF4)
private val Green100 = Color(0xFFDCFCE7)
private val Green200 = Color(0xFFBBF7D0)
private val Green300 = Color(0xFF86EFAC)
private val Green400 = Color(0xFF4ADE80)
private val Green500 = Color(0xFF22C55E)
private val Green600 = Color(0xFF16A34A)
private val Green700 = Color(0xFF15803D)
private val Green800 = Color(0xFF166534)
private val Green900 = Color(0xFF14532D)

// Semantic - Warning (Amber)
private val Amber50 = Color(0xFFFFFBEB)
private val Amber100 = Color(0xFFFFF3C0)
private val Amber200 = Color(0xFFFFE8A0)
private val Amber300 = Color(0xFFFDE047)
private val Amber400 = Color(0xFFFBBF24)
private val Amber500 = Color(0xFFF59E0B)
private val Amber600 = Color(0xFFD97706)
private val Amber700 = Color(0xFFB45309)
private val Amber800 = Color(0xFF92400E)
private val Amber900 = Color(0xFF78350F)

// Semantic - Error (Muted Red)
private val Red50 = Color(0xFFFEF2F2)
private val Red100 = Color(0xFFFEE2E2)
private val Red200 = Color(0xFFFECACA)
private val Red300 = Color(0xFFFCA5A5)
private val Red400 = Color(0xFFF87171)
private val Red500 = Color(0xFFEF4444)
private val Red600 = Color(0xFFDC2626)
private val Red700 = Color(0xFFB91C1C)
private val Red800 = Color(0xFF991B1B)
private val Red900 = Color(0xFF7F1D1D)

// ============================================================================
// DARK THEME TOKENS (Primary)
// ============================================================================

object DarkTokens {
    // Background layers - Deep graphite hierarchy
    val Background = Gray950           // #020617 - Deepest
    val BackgroundSecondary = Gray900  // #0F172A - Secondary
    val BackgroundTertiary = Gray800   // #1E293B - Tertiary
    
    // Surface layers - Glass-morphism ready
    val Surface = Gray900.copy(alpha = 0.92)           // Primary surface
    val SurfaceVariant = Gray800.copy(alpha = 0.88)    // Secondary surface
    val SurfaceElevated = Gray800.copy(alpha = 0.95)   // Elevated surface
    val SurfaceOverlay = Gray950.copy(alpha = 0.96)    // Modal overlay
    
    // Glass surfaces - Semi-transparent with blur effect
    val GlassSurface = Gray900.copy(alpha = 0.72)      // Standard glass
    val GlassSurfaceStrong = Gray900.copy(alpha = 0.80) // Stronger glass
    val GlassSurfaceSubtle = Gray900.copy(alpha = 0.56) // Subtle glass
    val GlassBorder = Gray700.copy(alpha = 0.30)       // Glass border
    val GlassBorderStrong = Gray600.copy(alpha = 0.45) // Stronger glass border
    
    // Content colors
    val OnBackground = Gray50              // Primary text
    val OnBackgroundSecondary = Gray300    // Secondary text
    val OnBackgroundTertiary = Gray500     // Tertiary text
    val OnBackgroundDisabled = Gray600     // Disabled text
    
    val OnSurface = Gray50
    val OnSurfaceVariant = Gray300
    val OnSurfaceDisabled = Gray600
    
    // Accent colors
    val Primary = Blue400                  // Electric blue
    val PrimaryVariant = Blue500
    val PrimaryContainer = Blue900.copy(alpha = 0.40)
    val OnPrimary = Gray950
    val OnPrimaryContainer = Gray50
    
    val Secondary = Purple400              // Muted purple
    val SecondaryVariant = Purple500
    val SecondaryContainer = Purple900.copy(alpha = 0.40)
    val OnSecondary = Gray950
    val OnSecondaryContainer = Gray50
    
    // Semantic colors
    val Success = Green400
    val SuccessContainer = Green900.copy(alpha = 0.35)
    val OnSuccess = Gray950
    val OnSuccessContainer = Gray50
    
    val Warning = Amber400
    val WarningContainer = Amber900.copy(alpha = 0.35)
    val OnWarning = Gray950
    val OnWarningContainer = Gray50
    
    val Error = Red400
    val ErrorContainer = Red900.copy(alpha = 0.35)
    val OnError = Gray950
    val OnErrorContainer = Gray50
    
    // Outline & divider
    val Outline = Gray700.copy(alpha = 0.50)
    val OutlineVariant = Gray700.copy(alpha = 0.25)
    val Divider = Gray700.copy(alpha = 0.18)
    
    // Shadow & elevation
    val Shadow = Color.Black.copy(alpha = 0.45)
    val Scrim = Color.Black.copy(alpha = 0.60)
    
    // Status specific
    val StatusConnected = Green400
    val StatusConnecting = Amber400
    val StatusDisconnected = Red400
    val StatusUnknown = Gray500
    
    // Code/technical
    val CodeBackground = Gray950.copy(alpha = 0.95)
    val CodeText = Gray100
    val CodeComment = Gray500
    val CodeKeyword = Blue300
    val CodeString = Green300
    val CodeNumber = Amber300
}

// ============================================================================
// LIGHT THEME TOKENS
// ============================================================================

object LightTokens {
    // Background layers
    val Background = Gray50
    val BackgroundSecondary = Gray100
    val BackgroundTertiary = Gray200
    
    // Surface layers
    val Surface = Color.White
    val SurfaceVariant = Gray50
    val SurfaceElevated = Color.White
    val SurfaceOverlay = Color.White.copy(alpha = 0.96)
    
    // Glass surfaces
    val GlassSurface = Color.White.copy(alpha = 0.72)
    val GlassSurfaceStrong = Color.White.copy(alpha = 0.80)
    val GlassSurfaceSubtle = Color.White.copy(alpha = 0.56)
    val GlassBorder = Gray300.copy(alpha = 0.50)
    val GlassBorderStrong = Gray400.copy(alpha = 0.65)
    
    // Content colors
    val OnBackground = Gray950
    val OnBackgroundSecondary = Gray700
    val OnBackgroundTertiary = Gray500
    val OnBackgroundDisabled = Gray400
    
    val OnSurface = Gray950
    val OnSurfaceVariant = Gray700
    val OnSurfaceDisabled = Gray400
    
    // Accent colors
    val Primary = Blue600
    val PrimaryVariant = Blue700
    val PrimaryContainer = Blue100
    val OnPrimary = Gray50
    val OnPrimaryContainer = Gray900
    
    val Secondary = Purple600
    val SecondaryVariant = Purple700
    val SecondaryContainer = Purple100
    val OnSecondary = Gray50
    val OnSecondaryContainer = Gray900
    
    // Semantic colors
    val Success = Green600
    val SuccessContainer = Green100
    val OnSuccess = Gray50
    val OnSuccessContainer = Gray900
    
    val Warning = Amber600
    val WarningContainer = Amber100
    val OnWarning = Gray50
    val OnWarningContainer = Gray900
    
    val Error = Red600
    val ErrorContainer = Red100
    val OnError = Gray50
    val OnErrorContainer = Gray900
    
    // Outline & divider
    val Outline = Gray400.copy(alpha = 0.60)
    val OutlineVariant = Gray300.copy(alpha = 0.50)
    val Divider = Gray300.copy(alpha = 0.30)
    
    // Shadow & elevation
    val Shadow = Color.Black.copy(alpha = 0.15)
    val Scrim = Color.Black.copy(alpha = 0.40)
    
    // Status specific
    val StatusConnected = Green600
    val StatusConnecting = Amber600
    val StatusDisconnected = Red600
    val StatusUnknown = Gray500
    
    // Code/technical
    val CodeBackground = Gray100
    val CodeText = Gray900
    val CodeComment = Gray500
    val CodeKeyword = Blue700
    val CodeString = Green700
    val CodeNumber = Amber700
}

// ============================================================================
// COLOR SCHEME FACTORIES
// ============================================================================

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

internal fun darkColorScheme(): ColorScheme = darkColorScheme(
    primary = DarkTokens.Primary,
    onPrimary = DarkTokens.OnPrimary,
    primaryContainer = DarkTokens.PrimaryContainer,
    onPrimaryContainer = DarkTokens.OnPrimaryContainer,
    secondary = DarkTokens.Secondary,
    onSecondary = DarkTokens.OnSecondary,
    secondaryContainer = DarkTokens.SecondaryContainer,
    onSecondaryContainer = DarkTokens.OnSecondaryContainer,
    tertiary = DarkTokens.Success,
    onTertiary = DarkTokens.OnSuccess,
    tertiaryContainer = DarkTokens.SuccessContainer,
    onTertiaryContainer = DarkTokens.OnSuccessContainer,
    error = DarkTokens.Error,
    onError = DarkTokens.OnError,
    errorContainer = DarkTokens.ErrorContainer,
    onErrorContainer = DarkTokens.OnErrorContainer,
    background = DarkTokens.Background,
    onBackground = DarkTokens.OnBackground,
    surface = DarkTokens.Surface,
    onSurface = DarkTokens.OnSurface,
    surfaceVariant = DarkTokens.SurfaceVariant,
    onSurfaceVariant = DarkTokens.OnSurfaceVariant,
    outline = DarkTokens.Outline,
    outlineVariant = DarkTokens.OutlineVariant,
    shadow = DarkTokens.Shadow,
    scrim = DarkTokens.Scrim,
    inverseSurface = LightTokens.Surface,
    inverseOnSurface = LightTokens.OnSurface,
    inversePrimary = LightTokens.Primary,
    surfaceTint = DarkTokens.Primary,
)

internal fun lightColorScheme(): ColorScheme = lightColorScheme(
    primary = LightTokens.Primary,
    onPrimary = LightTokens.OnPrimary,
    primaryContainer = LightTokens.PrimaryContainer,
    onPrimaryContainer = LightTokens.OnPrimaryContainer,
    secondary = LightTokens.Secondary,
    onSecondary = LightTokens.OnSecondary,
    secondaryContainer = LightTokens.SecondaryContainer,
    onSecondaryContainer = LightTokens.OnSecondaryContainer,
    tertiary = LightTokens.Success,
    onTertiary = LightTokens.OnSuccess,
    tertiaryContainer = LightTokens.SuccessContainer,
    onTertiaryContainer = LightTokens.OnSuccessContainer,
    error = LightTokens.Error,
    onError = LightTokens.OnError,
    errorContainer = LightTokens.ErrorContainer,
    onErrorContainer = LightTokens.OnErrorContainer,
    background = LightTokens.Background,
    onBackground = LightTokens.OnBackground,
    surface = LightTokens.Surface,
    onSurface = LightTokens.OnSurface,
    surfaceVariant = LightTokens.SurfaceVariant,
    onSurfaceVariant = LightTokens.OnSurfaceVariant,
    outline = LightTokens.Outline,
    outlineVariant = LightTokens.OutlineVariant,
    shadow = LightTokens.Shadow,
    scrim = LightTokens.Scrim,
    inverseSurface = DarkTokens.Surface,
    inverseOnSurface = DarkTokens.OnSurface,
    inversePrimary = DarkTokens.Primary,
    surfaceTint = LightTokens.Primary,
)

// ============================================================================
// EXTENSION PROPERTIES FOR EASY ACCESS
// ============================================================================

import androidx.compose.ui.graphics.Color

val ColorScheme.glassSurface: Color
    get() = when (this.brightness) {
        androidx.compose.material3.ColorScheme.Brightness.DARK -> DarkTokens.GlassSurface
        else -> LightTokens.GlassSurface
    }

val ColorScheme.glassSurfaceStrong: Color
    get() = when (this.brightness) {
        androidx.compose.material3.ColorScheme.Brightness.DARK -> DarkTokens.GlassSurfaceStrong
        else -> LightTokens.GlassSurfaceStrong
    }

val ColorScheme.glassSurfaceSubtle: Color
    get() = when (this.brightness) {
        androidx.compose.material3.ColorScheme.Brightness.DARK -> DarkTokens.GlassSurfaceSubtle
        else -> LightTokens.GlassSurfaceSubtle
    }

val ColorScheme.glassBorder: Color
    get() = when (this.brightness) {
        androidx.compose.material3.ColorScheme.Brightness.DARK -> DarkTokens.GlassBorder
        else -> LightTokens.GlassBorder
    }

val ColorScheme.glassBorderStrong: Color
    get() = when (this.brightness) {
        androidx.compose.material3.ColorScheme.Brightness.DARK -> DarkTokens.GlassBorderStrong
        else -> LightTokens.GlassBorderStrong
    }

val ColorScheme.statusConnected: Color
    get() = when (this.brightness) {
        androidx.compose.material3.ColorScheme.Brightness.DARK -> DarkTokens.StatusConnected
        else -> LightTokens.StatusConnected
    }

val ColorScheme.statusConnecting: Color
    get() = when (this.brightness) {
        androidx.compose.material3.ColorScheme.Brightness.DARK -> DarkTokens.StatusConnecting
        else -> LightTokens.StatusConnecting
    }

val ColorScheme.statusDisconnected: Color
    get() = when (this.brightness) {
        androidx.compose.material3.ColorScheme.Brightness.DARK -> DarkTokens.StatusDisconnected
        else -> LightTokens.StatusDisconnected
    }

val ColorScheme.statusUnknown: Color
    get() = when (this.brightness) {
        androidx.compose.material3.ColorScheme.Brightness.DARK -> DarkTokens.StatusUnknown
        else -> LightTokens.StatusUnknown
    }

val ColorScheme.codeBackground: Color
    get() = when (this.brightness) {
        androidx.compose.material3.ColorScheme.Brightness.DARK -> DarkTokens.CodeBackground
        else -> LightTokens.CodeBackground
    }

val ColorScheme.codeText: Color
    get() = when (this.brightness) {
        androidx.compose.material3.ColorScheme.Brightness.DARK -> DarkTokens.CodeText
        else -> LightTokens.CodeText
    }

val ColorScheme.codeComment: Color
    get() = when (this.brightness) {
        androidx.compose.material3.ColorScheme.Brightness.DARK -> DarkTokens.CodeComment
        else -> LightTokens.CodeComment
    }

val ColorScheme.codeKeyword: Color
    get() = when (this.brightness) {
        androidx.compose.material3.ColorScheme.Brightness.DARK -> DarkTokens.CodeKeyword
        else -> LightTokens.CodeKeyword
    }

val ColorScheme.codeString: Color
    get() = when (this.brightness) {
        androidx.compose.material3.ColorScheme.Brightness.DARK -> DarkTokens.CodeString
        else -> LightTokens.CodeString
    }

val ColorScheme.codeNumber: Color
    get() = when (this.brightness) {
        androidx.compose.material3.ColorScheme.Brightness.DARK -> DarkTokens.CodeNumber
        else -> LightTokens.CodeNumber
    }
