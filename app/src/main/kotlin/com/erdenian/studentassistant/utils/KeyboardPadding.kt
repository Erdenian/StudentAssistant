package com.erdenian.studentassistant.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import kotlin.math.max

@Composable
internal fun KeyboardPadding(content: @Composable () -> Unit) = Box(
    content = { content() },
    modifier = Modifier.padding(bottom = LocalKeyboardPadding.current)
)

@Composable
internal fun ProvideKeyboardPadding(
    paddingValues: PaddingValues,
    content: @Composable () -> Unit
) {
    val keyboardBottom = with(LocalDensity.current) {
        val insets = LocalWindowInsets.current
        max(insets.navigationBars.bottom, insets.ime.bottom).toDp()
    }

    val keyboardPadding = (keyboardBottom - paddingValues.calculateBottomPadding()).coerceAtLeast(0.dp)

    CompositionLocalProvider(
        LocalKeyboardPadding provides keyboardPadding,
        content = content
    )
}

internal val LocalKeyboardPadding = compositionLocalOf { 0.dp }
