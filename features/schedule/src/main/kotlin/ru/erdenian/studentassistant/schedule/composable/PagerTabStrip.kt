package ru.erdenian.studentassistant.schedule.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun PagerTabStrip(
    state: PagerState,
    titleGetter: (page: Int) -> String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.body2,
    textSpacing: Dp = 32.dp,
    colors: PagerTabStripColors = PagerTabStripDefaults.pagerTabStripColors()
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Layout(
            modifier = modifier.height(32.dp),
            content = {
                val indices = 0 until state.pageCount
                fun getText(index: Int) = index.takeIf { it in indices }?.let { titleGetter(it) }

                @Composable
                fun createText(title: String, color: Color) = Text(
                    text = title,
                    color = color,
                    style = textStyle
                )

                val page = state.currentPage + state.currentPageOffset.roundToInt()
                createText(title = getText(page - 1) ?: "", color = colors.otherTabsColor().value)
                createText(title = titleGetter(page), color = colors.currentTabColor().value)
                createText(title = getText(page + 1) ?: "", color = colors.otherTabsColor().value)
            }
        ) { measurables, constraints ->
            val width = constraints.maxWidth
            val height = constraints.maxHeight

            val childConstraints = constraints.copy(maxWidth = width / 2, minHeight = 0)
            val placeables = measurables.map { it.measure(childConstraints) }

            layout(width, height) {
                val previousPlaceable = placeables[0]
                val currentPlaceable = placeables[1]
                val nextPlaceable = placeables[2]

                val halfCurrWidth = currentPlaceable.width / 2
                val contentWidth = width - currentPlaceable.width

                val currOffset = (state.currentPageOffset + 0.5f) % 1

                val currCenter = width - halfCurrWidth - (contentWidth * currOffset).toInt()
                val currLeft = currCenter - currentPlaceable.width / 2
                val currRight = currLeft + currentPlaceable.width

                val mScaledTextSpacing = textSpacing.toPx().toInt()
                val y = (height - currentPlaceable.height) / 2

                currentPlaceable.placeRelative(currLeft, y)

                val prevLeft = min(0, currLeft - mScaledTextSpacing - previousPlaceable.width)
                previousPlaceable.placeRelative(prevLeft, y)

                val nextLeft = max(width - nextPlaceable.width, currRight + mScaledTextSpacing)
                nextPlaceable.placeRelative(nextLeft, y)
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = colors.tabIndicatorColor().value)
        )
    }
}

internal object PagerTabStripDefaults {

    @Composable
    fun pagerTabStripColors(
        currentTabColor: Color = MaterialTheme.colors.primary,
        otherTabsColor: Color = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.medium),
        tabIndicatorColor: Color = MaterialTheme.colors.primary
    ): PagerTabStripColors = DefaultPagerTabStripColors(
        currentTabColor = currentTabColor,
        otherTabsColor = otherTabsColor,
        tabIndicatorColor = tabIndicatorColor
    )
}

@Stable
internal interface PagerTabStripColors {

    @Composable
    fun currentTabColor(): State<Color>

    @Composable
    fun otherTabsColor(): State<Color>

    @Composable
    fun tabIndicatorColor(): State<Color>
}

@Immutable
private class DefaultPagerTabStripColors(
    private val currentTabColor: Color,
    private val otherTabsColor: Color,
    private val tabIndicatorColor: Color
) : PagerTabStripColors {

    @Composable
    override fun currentTabColor() = rememberUpdatedState(currentTabColor)

    @Composable
    override fun otherTabsColor() = rememberUpdatedState(otherTabsColor)

    @Composable
    override fun tabIndicatorColor() = rememberUpdatedState(tabIndicatorColor)
}
