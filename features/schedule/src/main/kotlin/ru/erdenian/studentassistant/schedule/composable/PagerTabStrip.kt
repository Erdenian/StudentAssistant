package ru.erdenian.studentassistant.schedule.composable

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlinx.coroutines.launch
import ru.erdenian.studentassistant.style.AppTheme

@Composable
internal fun PagerTabStrip(
    state: PagerState,
    titleGetter: (page: Int) -> String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 12.sp,
    textSpacing: Dp = 32.dp,
    underscoreHeight: Dp = 2.dp,
    colors: PagerTabStripColors = PagerTabStripDefaults.pagerTabStripColors(),
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        val scrollCoroutineScope = rememberCoroutineScope()
        Layout(
            modifier = modifier
                .height(32.dp)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        scrollCoroutineScope.launch { state.scrollBy(-delta) }
                    },
                    onDragStopped = {
                        launch {
                            state.animateScrollToPage(state.currentPage + state.currentPageOffsetFraction.roundToInt())
                        }
                    },
                ),
            content = {
                val indices = 0 until state.pageCount

                @Composable
                fun createText(page: Int, color: Color) = Text(
                    text = if (page in indices) titleGetter(page) else "",
                    color = color,
                    fontSize = fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = (page in indices),
                    ) {
                        scrollCoroutineScope.launch { state.animateScrollToPage(page) }
                    },
                )

                val selectedTabTextColor = colors.selectedTabTextColor().value
                val notSelectedTabTextColor = colors.notSelectedTabTextColor().value
                val tabIndicatorColor = colors.tabIndicatorColor().value

                val page = state.currentPage + state.currentPageOffsetFraction.roundToInt()
                val offset = 1 - abs(abs(state.currentPageOffsetFraction) % 1 - 0.5f) * 2.0f
                val animatedCurrentTabTextColor = selectedTabTextColor.transitionTo(notSelectedTabTextColor, offset)
                val animatedUnderscoreAlpha = tabIndicatorColor.alpha * (1 - offset)

                createText(
                    page = page - 1,
                    color = notSelectedTabTextColor,
                )
                createText(
                    page = page,
                    color = animatedCurrentTabTextColor,
                )
                createText(
                    page = page + 1,
                    color = notSelectedTabTextColor,
                )

                Box(modifier = Modifier.background(tabIndicatorColor.copy(alpha = animatedUnderscoreAlpha)))
            },
        ) { measurables, constraints ->
            val width = constraints.maxWidth
            val height = constraints.maxHeight
            val textSpacingPx = textSpacing.roundToPx()

            val titlesConstraints = constraints.copy(maxWidth = width / 2 - textSpacingPx, minHeight = 0)
            val titlesPlaceables = measurables.dropLast(1).map { it.measure(titlesConstraints) }

            val previousPlaceable = titlesPlaceables[0]
            val currentPlaceable = titlesPlaceables[1]
            val nextPlaceable = titlesPlaceables[2]

            val underscorePlaceable = measurables.last().measure(
                Constraints.fixed(currentPlaceable.width + textSpacingPx, underscoreHeight.roundToPx()),
            )

            layout(width, height) {
                val halfCurrWidth = currentPlaceable.width / 2
                val contentWidth = width - currentPlaceable.width

                val currOffset = run {
                    val offset = (state.currentPageOffsetFraction + 0.5f) % 1
                    if (offset >= 0.0f) offset else offset + 1.0f
                }

                val currCenter = width - halfCurrWidth - (contentWidth * currOffset).toInt()
                val currLeft = currCenter - currentPlaceable.width / 2
                val currRight = currLeft + currentPlaceable.width

                val y = (height - currentPlaceable.height) / 2

                currentPlaceable.placeRelative(currLeft, y)

                val prevLeft = min(0, currLeft - textSpacingPx - previousPlaceable.width)
                previousPlaceable.placeRelative(prevLeft, y)

                val nextLeft = max(width - nextPlaceable.width, currRight + textSpacingPx)
                nextPlaceable.placeRelative(nextLeft, y)

                underscorePlaceable.placeRelative(currLeft - textSpacingPx / 2, height - underscorePlaceable.height)
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = colors.tabIndicatorColor().value),
        )
    }
}

internal object PagerTabStripDefaults {

    @Composable
    fun pagerTabStripColors(
        selectedTabTextColor: Color = MaterialTheme.colorScheme.primary,
        notSelectedTabTextColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
        tabIndicatorColor: Color = MaterialTheme.colorScheme.primary,
    ): PagerTabStripColors = DefaultPagerTabStripColors(
        selectedTabTextColor = selectedTabTextColor,
        notSelectedTabTextColor = notSelectedTabTextColor,
        tabIndicatorColor = tabIndicatorColor,
    )
}

@Stable
internal interface PagerTabStripColors {

    @Composable
    fun selectedTabTextColor(): State<Color>

    @Composable
    fun notSelectedTabTextColor(): State<Color>

    @Composable
    fun tabIndicatorColor(): State<Color>
}

@Immutable
private class DefaultPagerTabStripColors(
    private val selectedTabTextColor: Color,
    private val notSelectedTabTextColor: Color,
    private val tabIndicatorColor: Color,
) : PagerTabStripColors {

    @Composable
    override fun selectedTabTextColor() = rememberUpdatedState(selectedTabTextColor)

    @Composable
    override fun notSelectedTabTextColor() = rememberUpdatedState(notSelectedTabTextColor)

    @Composable
    override fun tabIndicatorColor() = rememberUpdatedState(tabIndicatorColor)
}

private fun Color.transitionTo(color: Color, progress: Float): Color {
    val invertedProgress = 1 - progress
    fun Float.square() = this * this
    return Color(
        red = sqrt(this.red.square() * invertedProgress + color.red.square() * progress),
        green = sqrt(this.green.square() * invertedProgress + color.green.square() * progress),
        blue = sqrt(this.blue.square() * invertedProgress + color.blue.square() * progress),
        alpha = sqrt(this.alpha.square() * invertedProgress + color.alpha.square() * progress),
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PagerTabStripPreview() = AppTheme {
    Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        val state = rememberPagerState { 10 }

        PagerTabStrip(
            state = state,
            titleGetter = { "Page $it" },
        )

        HorizontalPager(
            state = state,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            Text(
                text = page.toString(),
                modifier = Modifier.wrapContentSize(),
            )
        }
    }
}
