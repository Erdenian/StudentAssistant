package com.erdenian.studentassistant.style

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
class Dimensions(

    val screenPaddingHorizontal: Dp = 16.dp,
    val screenPaddingVertical: Dp = 16.dp,

    val cardsSpacing: Dp = 12.dp,

    val cardContentPadding: Dp = 8.dp,

    val dividerPaddingVertical: Dp = 4.dp
)

internal val LocalDimensions = staticCompositionLocalOf { DefaultDimensions }

internal val DefaultDimensions = Dimensions()

internal val LargeScreenDimensions = Dimensions(
    screenPaddingHorizontal = 64.dp
)
