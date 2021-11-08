package ru.erdenian.studentassistant.style

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class Dimensions(

    val activityHorizontalMargin: Dp,
    val activityVerticalMargin: Dp,

    val cardsSpacing: Dp,

    val cardMarginInside: Dp,

    val dividerMarginTopBottom: Dp
)

internal val LocalDimensions = staticCompositionLocalOf { DefaultDimensions }

internal val DefaultDimensions = Dimensions(
    activityHorizontalMargin = 16.dp,
    activityVerticalMargin = 16.dp,

    cardsSpacing = 10.dp,

    cardMarginInside = 8.dp,

    dividerMarginTopBottom = 4.dp
)

internal val LargeScreenDimensions = Dimensions(
    activityHorizontalMargin = 64.dp,
    activityVerticalMargin = 16.dp,

    cardsSpacing = 10.dp,

    cardMarginInside = 8.dp,

    dividerMarginTopBottom = 4.dp
)
