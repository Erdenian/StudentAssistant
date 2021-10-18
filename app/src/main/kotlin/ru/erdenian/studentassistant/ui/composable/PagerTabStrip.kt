package ru.erdenian.studentassistant.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun PagerTabStrip(
    state: PagerState,
    titleGetter: (page: Int) -> String,
    modifier: Modifier = Modifier,
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

                val previousTitle = getText(state.currentPage - 1)
                val currentTitle = titleGetter(state.currentPage)
                val nextTitle = getText(state.currentPage + 1)

                Text(
                    text = previousTitle ?: "",
                    color = colors.backgroundColor().value
                )
                Text(
                    text = currentTitle,
                    color = colors.backgroundColor().value
                )
                Text(
                    text = nextTitle ?: "",
                    color = colors.backgroundColor().value
                )
            }
        ) { measurables, constraints ->
            val width = constraints.maxWidth
            val height = constraints.maxHeight

            val childConstraints = constraints.copy(maxWidth = width / 3, minHeight = 0)
            val placeables = measurables.map { it.measure(childConstraints) }

            layout(width, height) {
                val previousPlaceable = placeables[0]
                val currentPlaceable = placeables[1]
                val nextPlaceable = placeables[2]

                val pageOffset = -state.currentPageOffset

                val offset = (width / 2 * pageOffset).toInt()
                val previousOffset = (previousPlaceable.width / 2 * (1.0f - pageOffset)).toInt()
                val currentOffset = -(currentPlaceable.width / 2 * pageOffset).toInt()
                val nextOffset = (nextPlaceable.width / 2 * (-1.0f - pageOffset)).toInt()

                val y = (height - currentPlaceable.height) / 2

                previousPlaceable.placeRelative(0 - previousPlaceable.width / 2 + offset + previousOffset, y)
                currentPlaceable.placeRelative(width / 2 - currentPlaceable.width / 2 + offset + currentOffset, y)
                nextPlaceable.placeRelative(width - nextPlaceable.width / 2 + offset + nextOffset, y)
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = colors.backgroundColor().value)
        )
    }
}

object PagerTabStripDefaults {

    @Composable
    fun pagerTabStripColors(
        backgroundColor: Color = MaterialTheme.colors.onSurface,
        tabIndicatorColor: Color = Color.Unspecified
    ): PagerTabStripColors = DefaultPagerTabStripColors(
        backgroundColor = backgroundColor,
        tabIndicatorColor = tabIndicatorColor
    )
}

@Stable
interface PagerTabStripColors {

    @Composable
    fun backgroundColor(): State<Color>

    @Composable
    fun tabIndicatorColor(): State<Color>
}

@Immutable
private class DefaultPagerTabStripColors(
    private val backgroundColor: Color,
    private val tabIndicatorColor: Color
) : PagerTabStripColors {

    @Composable
    override fun backgroundColor() = rememberUpdatedState(backgroundColor)

    @Composable
    override fun tabIndicatorColor() = rememberUpdatedState(tabIndicatorColor)
}
