package com.nexshizuku.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * NexShizuku Design System - Shape Tokens
 * Obsidian Glass Theme
 * 
 * Consistent corner radius system for cohesive visual language.
 * Subtle rounded corners that feel premium without being playful.
 */

// ============================================================================
// PRIMITIVE SHAPE VALUES
// ============================================================================

// Base corner radii
private val RadiusNone = 0.dp
private val RadiusXs = 4.dp
private val RadiusSm = 8.dp
private val RadiusMd = 12.dp
private val RadiusLg = 16.dp
private val RadiusXl = 24.dp
private val RadiusFull = 9999.dp

// ============================================================================
// SHAPE TOKENS
// ============================================================================

object ShapeTokens {
    // None - Sharp corners
    val None = RoundedCornerShape(RadiusNone)
    
    // Extra small - Tiny elements, badges, chips
    val Xs = RoundedCornerShape(RadiusXs)
    
    // Small - Buttons, inputs, small cards
    val Small = RoundedCornerShape(RadiusSm)
    
    // Medium - Standard cards, dialogs, sheets
    val Medium = RoundedCornerShape(RadiusMd)
    
    // Large - Feature cards, modal bottom sheets
    val Large = RoundedCornerShape(RadiusLg)
    
    // Extra large - Hero sections, major surfaces
    val XLarge = RoundedCornerShape(RadiusXl)
    
    // Full - Pills, avatars, circular elements
    val Full = RoundedCornerShape(RadiusFull)
    
    // Glass-specific shapes (slightly more rounded for premium feel)
    val GlassSmall = RoundedCornerShape(10.dp)
    val GlassMedium = RoundedCornerShape(14.dp)
    val GlassLarge = RoundedCornerShape(20.dp)
    val GlassXLarge = RoundedCornerShape(28.dp)
}

// ============================================================================
// MATERIAL 3 SHAPES MAPPING
// ============================================================================

internal fun shapes(): Shapes = Shapes(
    extraSmall = ShapeTokens.Xs,
    small = ShapeTokens.Small,
    medium = ShapeTokens.Medium,
    large = ShapeTokens.Large,
    extraLarge = ShapeTokens.XLarge,
)

// ============================================================================
// EXTENSION PROPERTIES FOR EASY ACCESS
// ============================================================================

import androidx.compose.foundation.shape.CornerBasedShape

val Shapes.glassSmall: CornerBasedShape
    get() = ShapeTokens.GlassSmall

val Shapes.glassMedium: CornerBasedShape
    get() = ShapeTokens.GlassMedium

val Shapes.glassLarge: CornerBasedShape
    get() = ShapeTokens.GlassLarge

val Shapes.glassXLarge: CornerBasedShape
    get() = ShapeTokens.GlassXLarge

val Shapes.none: CornerBasedShape
    get() = ShapeTokens.None

val Shapes.full: CornerBasedShape
    get() = ShapeTokens.Full
