package io.joelt.texttemplate.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ColorScheme.onPrimarySecondary: Color
    get() = TransparentGreyMid
val ColorScheme.highlightEnabled: Color
    get() = YellowHigh
val ColorScheme.onHighlightEnabled: Color
    get() = Black
val ColorScheme.highlightDisabled: Color
    get() = GreyMid
val ColorScheme.onHighlightDisabled: Color
    get() = Black

private val LightColorPalette = lightColorScheme(
    primary = BlueMid,
    onPrimary = White,
    primaryContainer = White,
    onPrimaryContainer = Black,
    tertiary = YellowHigh,
    onTertiary = Black,
    tertiaryContainer = DarkBlueLow,
    onTertiaryContainer = White,
    surfaceTint = SlightlyDarkerWhite,
    surface = SlightlyDarkerWhite,
    background = MuchDarkerWhite,
    onBackground = Black
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
