package com.erdenian.studentassistant.style

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val dimensions = if (configuration.screenWidthDp <= 820) DefaultDimensions else LargeScreenDimensions
    val colors = if (isDarkTheme) DarkColors else LightColors

    CompositionLocalProvider(LocalDimensions provides dimensions) {
        MaterialTheme(
            colors = colors,
            content = content
        )
    }
}

@Suppress("UnusedReceiverParameter")
val MaterialTheme.dimensions: Dimensions
    @Composable
    @ReadOnlyComposable
    get() = LocalDimensions.current

typealias AppIcons = Icons.Filled
