@file:Suppress("MagicNumber")

package ru.erdenian.studentassistant.style

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

internal val LightColorScheme = lightColorScheme(
    primary = Color(0xFF005FAF),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD4E3FF),
    onPrimaryContainer = Color(0xFF001C3A),
    inversePrimary = Color(0xFFD4E3FF),

    secondary = Color(0xFF2653D3),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDCE1FF),
    onSecondaryContainer = Color(0xFF001550),

    tertiary = Color(0xFF6E5676),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF7D8FF),
    onTertiaryContainer = Color(0xFF271430),
)

internal val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA5C8FF),
    onPrimary = Color(0xFF00315F),
    primaryContainer = Color(0xFF004786),
    onPrimaryContainer = Color(0xFFD4E3FF),
    inversePrimary = Color(0xFF004786),

    secondary = Color(0xFFB6C4FF),
    onSecondary = Color(0xFF002780),
    secondaryContainer = Color(0xFF003AB3),
    onSecondaryContainer = Color(0xFFDCE1FF),

    tertiary = Color(0xFFDABDE2),
    onTertiary = Color(0xFF3D2846),
    tertiaryContainer = Color(0xFF553F5D),
    onTertiaryContainer = Color(0xFFF7D8FF),
)
