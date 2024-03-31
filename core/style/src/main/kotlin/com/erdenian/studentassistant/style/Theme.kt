package com.erdenian.studentassistant.style

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val dimensions = if (LocalConfiguration.current.screenWidthDp <= 820) DefaultDimensions else LargeScreenDimensions
    val isDynamicColor = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)

    @Suppress("KotlinConstantConditions")
    val colorScheme = when {
        (isDynamicColor && isDarkTheme) -> dynamicDarkColorScheme(LocalContext.current)
        (isDynamicColor && !isDarkTheme) -> dynamicLightColorScheme(LocalContext.current)
        (!isDynamicColor && isDarkTheme) -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(LocalDimensions provides dimensions) {
        MaterialTheme(
            colorScheme = colorScheme,
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

@Suppress("UnusedReceiverParameter")
val AppIcons.AutoMirrored get() = Icons.AutoMirrored.Filled
