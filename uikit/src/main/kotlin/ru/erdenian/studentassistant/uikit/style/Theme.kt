package ru.erdenian.studentassistant.uikit.style

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) = MaterialTheme(
    if (isDarkTheme) DarkColors else LightColors,
    content = content
)

typealias AppIcons = Icons.Filled
