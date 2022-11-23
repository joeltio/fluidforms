package io.joelt.texttemplate.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColorScheme(
    primary = Color(0xFF565C84),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDEE0FF),
    onPrimaryContainer = Color(0xFF12183D),
    inversePrimary = Color(0xFFBEC4F2),
    secondary = Color(0xFF5D5D68),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE2E1EE),
    onSecondaryContainer = Color(0xFF1A1B24),
    tertiary = Color(0xFF6D5866),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF6DBEB),
    onTertiaryContainer = Color(0xFF261722),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFFBFD),
    onBackground = Color(0xFF1C1B1D),
    surface = Color(0xFFFFFBFD),
    onSurface = Color(0xFF1C1B1D),
    surfaceTint = Color(0xFF565C84),
    inverseSurface = Color(0xFF313032),
    inverseOnSurface = Color(0xFFF3F0F2),
    surfaceVariant = Color(0xFFE4E1E7),
    onSurfaceVariant = Color(0xFF47464B),
    outline = Color(0xFF77767B),
)

private val DarkColorPalette = LightColorPalette

@Composable
fun TextTemplateTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
