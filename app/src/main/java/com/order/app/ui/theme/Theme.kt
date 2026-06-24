package com.order.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AccentAmber,
    secondary = AccentGreen,
    tertiary = Color(0xFF7D5260),
    background = Color(0xFFFAFAFA),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    error = AccentRed
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentAmber,
    secondary = AccentGreen,
    tertiary = Color(0xFF7D5260),
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = AccentRed
)

@Composable
fun OrderTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
