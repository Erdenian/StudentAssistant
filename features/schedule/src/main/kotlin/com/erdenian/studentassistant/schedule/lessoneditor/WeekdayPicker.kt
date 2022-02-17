package com.erdenian.studentassistant.schedule.lessoneditor

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
internal fun WeekdayPicker(
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
            // TextStyle.NARROW_STANDALONE returns number
            // https://stackoverflow.com/questions/63415047
            DayOfWeek.values().associateWith { it.getDisplayName(TextStyle.NARROW, locale).uppercase(locale) }
        }
    }

    val backgroundColor = colors.backgroundColor().value
    val selectedBackgroundColor = colors.selectedBackgroundColor().value
    val textColor = colors.textColor().value
    val selectedTextColor = colors.selectedTextColor().value

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
            val selectedPaint = remember(textSizePx, selectedTextColor) {
                Paint().apply {
                    this.textSize = textSizePx
                    color = selectedTextColor.toArgb()

                    isAntiAlias = true
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    textAlign = Paint.Align.CENTER
                }
            }
            val unselectedPaint = remember(textSizePx, textColor) {
                Paint().apply {
                    this.textSize = textSizePx
                    color = textColor.toArgb()

                    isAntiAlias = true
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    textAlign = Paint.Align.CENTER
                }
            }
            val bounds = remember { Rect() }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(enabled, editable) {
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
                        color = if (isSelected) selectedBackgroundColor else backgroundColor,
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
        backgroundColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.38f).compositeOver(MaterialTheme.colors.surface),
        selectedBackgroundColor: Color = MaterialTheme.colors.primary,
        textColor: Color = MaterialTheme.colors.onPrimary,
        selectedTextColor: Color = MaterialTheme.colors.onPrimary
    ): WeekdaysPickerColors = DefaultWeekdaysPickerColors(
        backgroundColor = backgroundColor,
        selectedBackgroundColor = selectedBackgroundColor,
        textColor = textColor,
        selectedTextColor = selectedTextColor
    )
}

@Stable
interface WeekdaysPickerColors {

    @Composable
    fun backgroundColor(): State<Color>

    @Composable
    fun selectedBackgroundColor(): State<Color>

    @Composable
    fun textColor(): State<Color>

    @Composable
    fun selectedTextColor(): State<Color>
}

@Immutable
private class DefaultWeekdaysPickerColors(
    private val backgroundColor: Color,
    private val selectedBackgroundColor: Color,
    private val textColor: Color,
    private val selectedTextColor: Color
) : WeekdaysPickerColors {

    @Composable
    override fun backgroundColor() = rememberUpdatedState(backgroundColor)

    @Composable
    override fun selectedBackgroundColor() = rememberUpdatedState(selectedBackgroundColor)

    @Composable
    override fun textColor() = rememberUpdatedState(textColor)

    @Composable
    override fun selectedTextColor() = rememberUpdatedState(selectedTextColor)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DefaultWeekdaysPickerColors

        if (backgroundColor != other.backgroundColor) return false
        if (selectedBackgroundColor != other.selectedBackgroundColor) return false
        if (textColor != other.textColor) return false
        if (selectedTextColor != other.selectedTextColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = backgroundColor.hashCode()
        result = 31 * result + selectedBackgroundColor.hashCode()
        result = 31 * result + textColor.hashCode()
        result = 31 * result + selectedTextColor.hashCode()
        return result
    }
}
