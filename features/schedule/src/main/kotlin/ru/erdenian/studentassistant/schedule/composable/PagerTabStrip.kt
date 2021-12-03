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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun PagerTabStrip(
    state: PagerState,
    titleGetter: (page: Int) -> String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 12.sp,
    textSpacing: Dp = 32.dp,
    otherTabsAlpha: Float = ContentAlpha.medium,
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
                    fontSize = fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                val textColor = colors.textColor().value
                val page = state.currentPage + state.currentPageOffset.roundToInt()
                val offset = abs(abs(state.currentPageOffset) % 1 - 0.5f) * 2.0f
                val animatedAlpha = otherTabsAlpha + (textColor.alpha - otherTabsAlpha) * offset

                createText(
                    title = getText(page - 1) ?: "",
                    color = textColor.copy(alpha = otherTabsAlpha)
                )
                createText(
                    title = titleGetter(page),
                    color = textColor.copy(alpha = animatedAlpha)
                )
                createText(
                    title = getText(page + 1) ?: "",
                    color = textColor.copy(alpha = otherTabsAlpha)
                )
            }
        ) { measurables, constraints ->
            val width = constraints.maxWidth
            val height = constraints.maxHeight

            val childConstraints = constraints.copy(maxWidth = width / 2 - textSpacing.toPx().toInt(), minHeight = 0)
            val placeables = measurables.map { it.measure(childConstraints) }

            layout(width, height) {
                val previousPlaceable = placeables[0]
                val currentPlaceable = placeables[1]
                val nextPlaceable = placeables[2]

                val halfCurrWidth = currentPlaceable.width / 2
                val contentWidth = width - currentPlaceable.width

                val currOffset = run {
                    val offset = (state.currentPageOffset + 0.5f) % 1
                    if (offset >= 0.0f) offset else offset + 1.0f
                }

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
        textColor: Color = MaterialTheme.colors.primary,
        tabIndicatorColor: Color = MaterialTheme.colors.primary
    ): PagerTabStripColors = DefaultPagerTabStripColors(
        textColor = textColor,
        tabIndicatorColor = tabIndicatorColor
    )
}

@Stable
internal interface PagerTabStripColors {

    @Composable
    fun textColor(): State<Color>

    @Composable
    fun tabIndicatorColor(): State<Color>
}

@Immutable
private class DefaultPagerTabStripColors(
    private val textColor: Color,
    private val tabIndicatorColor: Color
) : PagerTabStripColors {

    @Composable
    override fun textColor() = rememberUpdatedState(textColor)

    @Composable
    override fun tabIndicatorColor() = rememberUpdatedState(tabIndicatorColor)
}
