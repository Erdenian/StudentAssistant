package ru.erdenian.studentassistant.schedule.lessoneditor

import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
internal fun WeekdaysPicker(
    value: DayOfWeek,
    onValueChange: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    editable: Boolean = true,
    colors: WeekdaysPickerColors = WeekdaysPickerDefaults.weekdaysPickerColors()
) {
    val daysOfWeek = run {
        val locale = Locale.getDefault()
        remember(locale) {
            DayOfWeek.values().associateWith { it.getDisplayName(TextStyle.NARROW_STANDALONE, locale) }
        }
    }

    val highlightColor = colors.highlightColor().value
    val backgroundColor = colors.backgroundColor().value
    val weekendColor = colors.weekendColor().value
    val textColor = colors.textColor().value
    val textUnselectedColor = colors.textUnselectedColor().value
    val weekendTextColor = colors.weekendTextColor().value
    val borderColor = colors.borderColor().value
    val borderHighlightColor = colors.borderHighlightColor().value

    val density = LocalDensity.current

    val spacing = 8.dp
    val spacingPx = with(density) { spacing.toPx().toInt() }
    val textSize = 16.sp
    val textSizePx = with(density) { textSize.toPx() }

    Layout(
        modifier = modifier,
        measurePolicy = { measurables, constraints ->
            val spacingSum = spacingPx * (daysOfWeek.size - 1)

            val width = constraints.maxWidth
            val height = (width - spacingSum) / daysOfWeek.size

            val childConstraints = constraints.copy(minHeight = height, maxHeight = height)
            val placeables = measurables.map { it.measure(childConstraints) }

            layout(width, height) {
                placeables.single().placeRelative(0, 0)
            }
        },
        content = {
            val selectedPaint = remember(textSizePx, textColor) {
                Paint().apply {
                    this.textSize = textSizePx
                    color = textColor.toArgb()

                    isAntiAlias = true
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    textAlign = Paint.Align.CENTER
                }
            }
            val unselectedPaint = remember(textSizePx, textUnselectedColor) {
                Paint().apply {
                    this.textSize = textSizePx
                    color = textUnselectedColor.toArgb()

                    isAntiAlias = true
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    textAlign = Paint.Align.CENTER
                }
            }
            val bounds = remember { Rect() }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            if (!enabled || !editable) return@detectTapGestures

                            val spacingSum = spacingPx * (daysOfWeek.size - 1)
                            val dayWidth = (size.width - spacingSum) / 7.0f
                            val dayWidthWithSpacing = dayWidth + spacingPx
                            val clickedIndex = (offset.x / dayWidthWithSpacing).toInt()

                            onValueChange(DayOfWeek.of(clickedIndex + 1))
                        }
                    }
            ) {
                val spacingSum = spacingPx * (daysOfWeek.size - 1)
                val dayWidth = (size.width - spacingSum) / 7.0f
                val dayRadius = dayWidth / 2.0f

                daysOfWeek.entries.forEachIndexed { index, (day, name) ->
                    val isSelected = (day == value)
                    val center = Offset((dayWidth + spacingPx) * index + dayRadius, dayRadius)
                    drawCircle(
                        color = if (isSelected) highlightColor else backgroundColor,
                        radius = dayRadius,
                        center = center
                    )

                    drawIntoCanvas { canvas ->
                        selectedPaint.getTextBounds(name, 0, name.length, bounds)
                        val textVerticalOffset = bounds.height() / 2

                        canvas.nativeCanvas.drawText(
                            name,
                            center.x,
                            center.y + textVerticalOffset,
                            if (isSelected) selectedPaint else unselectedPaint
                        )
                    }
                }
            }
        }
    )
}

object WeekdaysPickerDefaults {

    @Composable
    fun weekdaysPickerColors(
        highlightColor: Color = MaterialTheme.colors.primary,
        backgroundColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.38f).compositeOver(MaterialTheme.colors.surface),
        weekendColor: Color = backgroundColor,
        textColor: Color = MaterialTheme.colors.onPrimary,
        textUnselectedColor: Color = MaterialTheme.colors.onPrimary,
        weekendTextColor: Color = textUnselectedColor,
        borderColor: Color = Color.Unspecified,
        borderHighlightColor: Color = Color.Unspecified
    ): WeekdaysPickerColors = DefaultWeekdaysPickerColors(
        highlightColor = highlightColor,
        backgroundColor = backgroundColor,
        weekendColor = weekendColor,
        textColor = textColor,
        textUnselectedColor = textUnselectedColor,
        weekendTextColor = weekendTextColor,
        borderColor = borderColor,
        borderHighlightColor = borderHighlightColor
    )
}

@Stable
interface WeekdaysPickerColors {

    @Composable
    fun highlightColor(): State<Color>

    @Composable
    fun backgroundColor(): State<Color>

    @Composable
    fun weekendColor(): State<Color>

    @Composable
    fun textColor(): State<Color>

    @Composable
    fun textUnselectedColor(): State<Color>

    @Composable
    fun weekendTextColor(): State<Color>

    @Composable
    fun borderColor(): State<Color>

    @Composable
    fun borderHighlightColor(): State<Color>
}

@Immutable
private class DefaultWeekdaysPickerColors(
    private val highlightColor: Color,
    private val backgroundColor: Color,
    private val weekendColor: Color,
    private val textColor: Color,
    private val textUnselectedColor: Color,
    private val weekendTextColor: Color,
    private val borderColor: Color,
    private val borderHighlightColor: Color
) : WeekdaysPickerColors {

    @Composable
    override fun highlightColor() = rememberUpdatedState(highlightColor)

    @Composable
    override fun backgroundColor() = rememberUpdatedState(backgroundColor)

    @Composable
    override fun weekendColor() = rememberUpdatedState(weekendColor)

    @Composable
    override fun textColor() = rememberUpdatedState(textColor)

    @Composable
    override fun textUnselectedColor() = rememberUpdatedState(textUnselectedColor)

    @Composable
    override fun weekendTextColor() = rememberUpdatedState(weekendTextColor)

    @Composable
    override fun borderColor() = rememberUpdatedState(borderColor)

    @Composable
    override fun borderHighlightColor() = rememberUpdatedState(borderHighlightColor)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DefaultWeekdaysPickerColors

        if (highlightColor != other.highlightColor) return false
        if (backgroundColor != other.backgroundColor) return false
        if (weekendColor != other.weekendColor) return false
        if (textColor != other.textColor) return false
        if (textUnselectedColor != other.textUnselectedColor) return false
        if (weekendTextColor != other.weekendTextColor) return false
        if (borderColor != other.borderColor) return false
        if (borderHighlightColor != other.borderHighlightColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = highlightColor.hashCode()
        result = 31 * result + backgroundColor.hashCode()
        result = 31 * result + weekendColor.hashCode()
        result = 31 * result + textColor.hashCode()
        result = 31 * result + textUnselectedColor.hashCode()
        result = 31 * result + weekendTextColor.hashCode()
        result = 31 * result + borderColor.hashCode()
        result = 31 * result + borderHighlightColor.hashCode()
        return result
    }
}
