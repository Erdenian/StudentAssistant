package ru.erdenian.studentassistant.style

import android.content.ContextWrapper
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode

@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
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

    if (!LocalInspectionMode.current) {
        val context = LocalContext.current
        val activity = remember(context) {
            var currentContext = context
            while (currentContext is ContextWrapper) {
                if (currentContext is ComponentActivity) return@remember currentContext
                currentContext = currentContext.baseContext
            }
            error("Activity not found")
        }
        SideEffect {
            val systemBarStyle =
                if (isDarkTheme) SystemBarStyle.dark(Color.Transparent.toArgb())
                else SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb())
            activity.enableEdgeToEdge(
                statusBarStyle = systemBarStyle,
                navigationBarStyle = systemBarStyle,
            )
        }
    }

    CompositionLocalProvider(LocalDimensions provides dimensions) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
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
