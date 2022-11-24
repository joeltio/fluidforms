package io.joelt.texttemplate.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

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

private val DarkColorPalette = darkColorScheme(
    primary = Color(0xFFBEC4F2),
    onPrimary = Color(0xFF282E53),
    primaryContainer = Color(0xFF3E446B),
    onPrimaryContainer = Color(0xFFDEE0FF),
    inversePrimary = Color(0xFF565C84),
    secondary = Color(0xFFC6C5D2),
    onSecondary = Color(0xFF2F303A),
    secondaryContainer = Color(0xFF454650),
    onSecondaryContainer = Color(0xFFE2E1EE),
    tertiary = Color(0xFFD9BFCF),
    onTertiary = Color(0xFF3C2B37),
    tertiaryContainer = Color(0xFF54414E),
    onTertiaryContainer = Color(0xFFF6DBEB),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFB4AB),
    background = Color(0xFF1C1B1D),
    onBackground = Color(0xFFE5E1E4),
    surface = Color(0xFF1C1B1D),
    onSurface = Color(0xFFE5E1E4),
    surfaceTint = Color(0xFFBEC4F2),
    inverseSurface = Color(0xFFE5E1E4),
    inverseOnSurface = Color(0xFF313032),
    surfaceVariant = Color(0xFF47464B),
    onSurfaceVariant = Color(0xFFC8C5CB),
    outline = Color(0xFF919095),
)

@Composable
fun TextTemplateTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val systemUiController = rememberSystemUiController()
    LaunchedEffect(Unit) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = !darkTheme
        )
    }

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
