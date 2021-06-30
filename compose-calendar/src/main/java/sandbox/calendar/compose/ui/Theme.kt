package sandbox.calendar.compose.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

data class ColorPalette(
    val label: Color,
    val icon: Color,
    val selectedColor: Color,
    val background: Color,
    val colors: Colors
)

private val DarkColorPalette = ColorPalette(
    label = white,
    icon = white,
    selectedColor = lightBlue,
    background = blue,
    darkColors(
        primary = white,
        primaryVariant = lightBlue,
        secondary = gray,
        background = blue
    )
)

private val LightColorPalette = ColorPalette(
    label = white,
    icon = white,
    selectedColor = lightBlue,
    background = blue,
    lightColors(
        primary = white,
        primaryVariant = lightBlue,
        secondary = gray,
        background = blue
    )
)

val LocalPalette = staticCompositionLocalOf { LightColorPalette }

object CalendarTheme {
    val palette: ColorPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalPalette.current
}

@Composable
fun SandboxTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val palette = remember {
        if (darkTheme) {
            DarkColorPalette
        } else {
            LightColorPalette
        }
    }
    MaterialTheme(
        colors = palette.colors,
        typography = typography,
        shapes = shapes
    ) {
        CompositionLocalProvider(
            LocalPalette provides palette,
            content = content
        )
    }
}