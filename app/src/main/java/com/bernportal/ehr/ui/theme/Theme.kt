package com.bernportal.ehr.ui.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.bernportal.ehr.data.Settings

object Brand {
    val Midnight = Color(0xFF060C26)
    val Navy = Color(0xFF0B1638)
    val Obsidian = Color(0xFF05060A)
    val CyberBlue = Color(0xFF2563EB)
    val Cyan = Color(0xFF22D3EE)
    val Violet = Color(0xFF8B5CF6)
    val Neon = Color(0xFF4ADE80)
    val Gold = Color(0xFFF5B82E)
    val GoldDeep = Color(0xFFB7791F)
    val Soft = Color(0xFFE8EEF8)
    val Muted = Color(0xFF94A3B8)
    val Danger = Color(0xFFF87171)
    val Warn = Color(0xFFFBBF24)
}

data class Motion(val level: String, val effects3d: Boolean) {
    val enabled get() = level != "Off"
    val full get() = level == "Full"
    fun dur(ms: Int) = when (level) { "Full" -> ms; "Reduced" -> ms / 2; else -> 0 }
}

val LocalMotion = staticCompositionLocalOf { Motion("Full", true) }
val LocalSettings = staticCompositionLocalOf { Settings() }

@Composable
fun EhrTheme(settings: Settings, content: @Composable () -> Unit) {
    val scheme = when {
        settings.highContrast || settings.theme == "High contrast" -> darkColorScheme(
            primary = Color(0xFF00E5FF), onPrimary = Color.Black, secondary = Color(0xFFFFD600), onSecondary = Color.Black,
            tertiary = Color(0xFF76FF03), background = Color.Black, onBackground = Color.White, surface = Color(0xFF0A0A0A),
            onSurface = Color.White, surfaceVariant = Color(0xFF1A1A1A), onSurfaceVariant = Color(0xFFEEEEEE), outline = Color.White,
            error = Color(0xFFFF5252)
        )
        settings.theme == "Daylight" -> lightColorScheme(
            primary = Brand.CyberBlue, onPrimary = Color.White, secondary = Brand.Violet, tertiary = Color(0xFF059669),
            background = Color(0xFFF3F6FC), onBackground = Color(0xFF0B1638), surface = Color.White, onSurface = Color(0xFF0B1638),
            surfaceVariant = Color(0xFFE6ECF7), onSurfaceVariant = Color(0xFF334155), outline = Color(0xFF94A3B8)
        )
        settings.theme == "Obsidian" -> darkColorScheme(
            primary = Brand.Cyan, onPrimary = Color.Black, secondary = Brand.Violet, tertiary = Brand.Neon, background = Brand.Obsidian,
            onBackground = Brand.Soft, surface = Color(0xFF0D0F16), onSurface = Brand.Soft, surfaceVariant = Color(0xFF161A26),
            onSurfaceVariant = Color(0xFFB8C2D6), outline = Color(0xFF334155), error = Brand.Danger
        )
        else -> darkColorScheme(
            primary = Brand.Cyan, onPrimary = Color(0xFF00121A), secondary = Brand.Violet, tertiary = Brand.Neon, background = Brand.Midnight,
            onBackground = Brand.Soft, surface = Brand.Navy, onSurface = Brand.Soft, surfaceVariant = Color(0xFF12214D),
            onSurfaceVariant = Color(0xFFB8C6E3), outline = Color(0xFF2B3E73), error = Brand.Danger
        )
    }
    val spacing = if (settings.dyslexia) 0.06.em else 0.em
    val lh = if (settings.dyslexia) 1.6f else 1.4f
    fun st(size: Int, w: FontWeight = FontWeight.Normal) = TextStyle(
        fontFamily = FontFamily.SansSerif, fontSize = size.sp, fontWeight = w, letterSpacing = spacing, lineHeight = (size * lh).sp
    )
    val typography = Typography(
        displaySmall = st(32, FontWeight.Black), headlineLarge = st(28, FontWeight.ExtraBold), headlineMedium = st(24, FontWeight.Bold),
        headlineSmall = st(20, FontWeight.Bold), titleLarge = st(19, FontWeight.Bold), titleMedium = st(16, FontWeight.SemiBold),
        titleSmall = st(14, FontWeight.SemiBold), bodyLarge = st(16), bodyMedium = st(14), bodySmall = st(12),
        labelLarge = st(14, FontWeight.SemiBold), labelMedium = st(12, FontWeight.Medium), labelSmall = st(11, FontWeight.Medium)
    )
    val density = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(density.density, density.fontScale * settings.fontScale),
        LocalMotion provides Motion(settings.animation, settings.effects3d),
        LocalSettings provides settings
    ) {
        MaterialTheme(colorScheme = scheme, typography = typography) {
            CompositionLocalProvider(LocalContentColor provides scheme.onBackground, content = content)
        }
    }
}
