package com.erdenian.studentassistant.style

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
class Dimensions(

    val activityHorizontalMargin: Dp = 16.dp,
    val activityVerticalMargin: Dp = 16.dp,

    val cardsSpacing: Dp = 10.dp,

    val cardMarginInside: Dp = 8.dp,

    val dividerMarginTopBottom: Dp = 4.dp
)

internal val LocalDimensions = staticCompositionLocalOf { DefaultDimensions }

internal val DefaultDimensions = Dimensions()

internal val LargeScreenDimensions = Dimensions(
    activityHorizontalMargin = 64.dp
)
