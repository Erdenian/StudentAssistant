package ru.erdenian.studentassistant.style

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

internal val LightColors = lightColors(
    primary = Color(0xFF1976D2),
    onPrimary = Color.White,
    primaryVariant = Color(0xFF1976D2),

    secondary = Color(0xFFD81B60),
    onSecondary = Color.White,
    secondaryVariant = Color(0xFFA00037),

    background = Color(0xFFF6F6FB),
    onBackground = Color.Black,

    surface = Color.White,
    onSurface = Color.Black,

    error = Color(0xFFD32F2F),
    onError = Color.White
)

internal val DarkColors = darkColors(
    primary = Color(0xFF60B0FF),
    onPrimary = Color.White,
    primaryVariant = Color(0xFF60B0FF),

    secondary = Color(0xFFD81B60),
    onSecondary = Color.White,
    secondaryVariant = Color(0xFFA00037),

    background = Color(0xFF202124),
    onBackground = Color.White,

    surface = Color(0xFF323232),
    onSurface = Color.White,

    error = Color(0xFFD32F2F),
    onError = Color.White
)
