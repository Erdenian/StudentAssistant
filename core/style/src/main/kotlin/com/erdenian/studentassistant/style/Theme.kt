package ru.erdenian.studentassistant.style

import android.app.Activity
import android.content.ContextWrapper
import android.os.Build
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
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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
        val window = remember(context) {
            var currentContext = context
            while (currentContext is ContextWrapper) {
                if (currentContext is Activity) return@remember checkNotNull(currentContext.window)
                currentContext = currentContext.baseContext
            }
            error("Activity not found")
        }
        val view = LocalView.current

        SideEffect {
            WindowCompat.setDecorFitsSystemWindows(window, false) // To make insets work

            val insetsController = WindowCompat.getInsetsController(window, view)
            window.statusBarColor = Color.Transparent.toArgb()
            insetsController.isAppearanceLightStatusBars = !isDarkTheme
            window.navigationBarColor = Color.Transparent.toArgb()
            insetsController.isAppearanceLightNavigationBars = !isDarkTheme
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
