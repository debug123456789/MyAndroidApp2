package com.nexshizuku.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intent.Intent
import androidx.compose.ui.unit.sp

/**
 * NexShizuku Design System - Typography
 * Obsidian Glass Theme
 * 
 * Carefully crafted type scale using system fonts for optimal performance
 * and platform consistency, with distinct styles for every use case.
 */

// ============================================================================
// FONT FAMILIES
// ============================================================================

// System font stacks for best platform integration
private val SystemFontFamily = FontFamily.Default
private val SystemMonospaceFontFamily = FontFamily.Monospace

// ============================================================================
// TEXT STYLES - DARK THEME (Primary)
// ============================================================================

object DarkTypography {
    // Display styles - Large headlines
    val DisplayLarge = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = -0.25.sp,
    )
    
    val DisplayMedium = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
    )
    
    val DisplaySmall = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
    )
    
    // Headline styles - Screen titles
    val HeadlineLarge = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    )
    
    val HeadlineMedium = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    )
    
    val HeadlineSmall = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    )
    
    // Title styles - Card/section headers
    val TitleLarge = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    )
    
    val TitleMedium = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
    )
    
    val TitleSmall = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    )
    
    // Body styles - Main content
    val BodyLarge = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    )
    
    val BodyMedium = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    )
    
    val BodySmall = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    )
    
    // Label styles - Buttons, chips, tabs
    val LabelLarge = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    )
    
    val LabelMedium = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    )
    
    val LabelSmall = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    )
    
    // Custom NexShizuku styles
    // Large status display (main screen status)
    val StatusLarge = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = -0.2.sp,
    )
    
    // Screen title (toolbar)
    val ScreenTitle = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.1.sp,
    )
    
    // Card title
    val CardTitle = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
    )
    
    // Card subtitle
    val CardSubtitle = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp,
    )
    
    // Technical/monospace values
    val TechnicalLarge = TextStyle(
        fontFamily = SystemMonospaceFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
    )
    
    val TechnicalMedium = TextStyle(
        fontFamily = SystemMonospaceFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
    )
    
    val TechnicalSmall = TextStyle(
        fontFamily = SystemMonospaceFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
    )
    
    // Code block
    val CodeBlock = TextStyle(
        fontFamily = SystemMonospaceFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
    )
    
    // Inline code
    val CodeInline = TextStyle(
        fontFamily = SystemMonospaceFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
    )
    
    // Status badge
    val StatusBadge = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    )
    
    // Button text
    val ButtonLarge = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
    )
    
    val ButtonMedium = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    )
    
    val ButtonSmall = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    )
    
    // Caption/footnote
    val Caption = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    )
    
    // Overline (category labels)
    val Overline = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.5.sp,
    )
}

// ============================================================================
// TEXT STYLES - LIGHT THEME
// ============================================================================

object LightTypography {
    // Display styles
    val DisplayLarge = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = -0.25.sp,
    )
    
    val DisplayMedium = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
    )
    
    val DisplaySmall = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
    )
    
    // Headline styles
    val HeadlineLarge = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    )
    
    val HeadlineMedium = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    )
    
    val HeadlineSmall = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    )
    
    // Title styles
    val TitleLarge = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    )
    
    val TitleMedium = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
    )
    
    val TitleSmall = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    )
    
    // Body styles
    val BodyLarge = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    )
    
    val BodyMedium = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    )
    
    val BodySmall = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    )
    
    // Label styles
    val LabelLarge = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    )
    
    val LabelMedium = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    )
    
    val LabelSmall = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    )
    
    // Custom NexShizuku styles
    val StatusLarge = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = -0.2.sp,
    )
    
    val ScreenTitle = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.1.sp,
    )
    
    val CardTitle = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
    )
    
    val CardSubtitle = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp,
    )
    
    // Technical/monospace
    val TechnicalLarge = TextStyle(
        fontFamily = SystemMonospaceFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
    )
    
    val TechnicalMedium = TextStyle(
        fontFamily = SystemMonospaceFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
    )
    
    val TechnicalSmall = TextStyle(
        fontFamily = SystemMonospaceFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
    )
    
    val CodeBlock = TextStyle(
        fontFamily = SystemMonospaceFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
    )
    
    val CodeInline = TextStyle(
        fontFamily = SystemMonospaceFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
    )
    
    val StatusBadge = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    )
    
    val ButtonLarge = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
    )
    
    val ButtonMedium = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    )
    
    val ButtonSmall = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    )
    
    val Caption = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    )
    
    val Overline = TextStyle(
        fontFamily = SystemFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.5.sp,
    )
}

// ============================================================================
// TYPOGRAPHY FACTORIES
// ============================================================================

internal fun darkTypography(): Typography = Typography(
    displayLarge = DarkTypography.DisplayLarge,
    displayMedium = DarkTypography.DisplayMedium,
    displaySmall = DarkTypography.DisplaySmall,
    headlineLarge = DarkTypography.HeadlineLarge,
    headlineMedium = DarkTypography.HeadlineMedium,
    headlineSmall = DarkTypography.HeadlineSmall,
    titleLarge = DarkTypography.TitleLarge,
    titleMedium = DarkTypography.TitleMedium,
    titleSmall = DarkTypography.TitleSmall,
    bodyLarge = DarkTypography.BodyLarge,
    bodyMedium = DarkTypography.BodyMedium,
    bodySmall = DarkTypography.BodySmall,
    labelLarge = DarkTypography.LabelLarge,
    labelMedium = DarkTypography.LabelMedium,
    labelSmall = DarkTypography.LabelSmall,
)

internal fun lightTypography(): Typography = Typography(
    displayLarge = LightTypography.DisplayLarge,
    displayMedium = LightTypography.DisplayMedium,
    displaySmall = LightTypography.DisplaySmall,
    headlineLarge = LightTypography.HeadlineLarge,
    headlineMedium = LightTypography.HeadlineMedium,
    headlineSmall = LightTypography.HeadlineSmall,
    titleLarge = LightTypography.TitleLarge,
    titleMedium = LightTypography.TitleMedium,
    titleSmall = LightTypography.TitleSmall,
    bodyLarge = LightTypography.BodyLarge,
    bodyMedium = LightTypography.BodyMedium,
    bodySmall = LightTypography.BodySmall,
    labelLarge = LightTypography.LabelLarge,
    labelMedium = LightTypography.LabelMedium,
    labelSmall = LightTypography.LabelSmall,
)

// ============================================================================
// EXTENSION PROPERTIES FOR CUSTOM STYLES
// ============================================================================

import androidx.compose.ui.text.TextStyle

val Typography.statusLarge: TextStyle
    get() = when (this) {
        is Typography -> DarkTypography.StatusLarge // Default to dark, will be overridden by theme
        else -> DarkTypography.StatusLarge
    }

val Typography.screenTitle: TextStyle
    get() = DarkTypography.ScreenTitle

val Typography.cardTitle: TextStyle
    get() = DarkTypography.CardTitle

val Typography.cardSubtitle: TextStyle
    get() = DarkTypography.CardSubtitle

val Typography.technicalLarge: TextStyle
    get() = DarkTypography.TechnicalLarge

val Typography.technicalMedium: TextStyle
    get() = DarkTypography.TechnicalMedium

val Typography.technicalSmall: TextStyle
    get() = DarkTypography.TechnicalSmall

val Typography.codeBlock: TextStyle
    get() = DarkTypography.CodeBlock

val Typography.codeInline: TextStyle
    get() = DarkTypography.CodeInline

val Typography.statusBadge: TextStyle
    get() = DarkTypography.StatusBadge

val Typography.buttonLarge: TextStyle
    get() = DarkTypography.ButtonLarge

val Typography.buttonMedium: TextStyle
    get() = DarkTypography.ButtonMedium

val Typography.buttonSmall: TextStyle
    get() = DarkTypography.ButtonSmall

val Typography.caption: TextStyle
    get() = DarkTypography.Caption

val Typography.overline: TextStyle
    get() = DarkTypography.Overline
