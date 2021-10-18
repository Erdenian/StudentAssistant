package ru.erdenian.studentassistant.ui.main.lessoneditor

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import java.util.Calendar
import org.joda.time.DateTimeConstants

@Composable
internal fun WeekdaysPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    editable: Boolean = true,
    sundayFirstDay: Boolean = true,
    showWeekend: Boolean = true,
    recurrence: Boolean = false,
    fullSize: Boolean = false,
    colors: WeekdaysPickerColors = WeekdaysPickerDefaults.weekdaysPickerColors(),
    borderThickness: Dp = Dp.Unspecified,
    borderHighlightThickness: Dp = Dp.Unspecified
) {
    val jodaToCalendar = remember {
        mapOf(
            DateTimeConstants.MONDAY to Calendar.MONDAY,
            DateTimeConstants.TUESDAY to Calendar.TUESDAY,
            DateTimeConstants.WEDNESDAY to Calendar.WEDNESDAY,
            DateTimeConstants.THURSDAY to Calendar.THURSDAY,
            DateTimeConstants.FRIDAY to Calendar.FRIDAY,
            DateTimeConstants.SATURDAY to Calendar.SATURDAY,
            DateTimeConstants.SUNDAY to Calendar.SUNDAY
        )
    }

    val currentValue by rememberUpdatedState(value)
    val currentOnValueChange by rememberUpdatedState(onValueChange)

    val highlightColor = colors.highlightColor().value
    val backgroundColor = colors.backgroundColor().value
    val weekendColor = colors.weekendColor().value
    val textColor = colors.textColor().value
    val textUnselectedColor = colors.textUnselectedColor().value
    val weekendTextColor = colors.weekendTextColor().value
    val borderColor = colors.borderColor().value
    val borderHighlightColor = colors.borderHighlightColor().value

    val density = LocalDensity.current

    AndroidView(
        factory = { context ->
            com.dpro.widgets.WeekdaysPicker(context).apply {
                setSelectOnlyOne(true)
                weekendDarker = true

                val calendarToJoda = jodaToCalendar.entries.associate { (k, v) -> v to k }
                setOnWeekdaysChangeListener { _, _, weekdays ->
                    val weekday = weekdays.singleOrNull()
                        ?.let(calendarToJoda::getValue)
                        ?: run {
                            selectDay(jodaToCalendar.getValue(currentValue))
                            return@setOnWeekdaysChangeListener
                        }
                    if (weekday != currentValue) currentOnValueChange(weekday)
                }
            }
        },
        update = { view ->
            view.isEnabled = enabled
            view.setEditable(editable)

            view.sundayFirstDay = sundayFirstDay
            view.showWeekend = showWeekend
            view.recurrence = recurrence
            view.fullSize = fullSize

            view.highlightColor = highlightColor.toArgb()
            view.backgroundColor = backgroundColor.toArgb()
            view.weekendColor = weekendColor.toArgb()

            view.textColor = textColor.toArgb()
            view.textUnselectedColor = textUnselectedColor.toArgb()
            view.weekendTextColor = weekendTextColor.toArgb()

            view.borderColor = borderColor.takeIf { it.isSpecified }?.toArgb() ?: -1
            view.borderHighlightColor = borderHighlightColor.takeIf { it.isSpecified }?.toArgb() ?: -1

            with(density) {
                view.borderThickness = borderThickness.toPx().toInt()
                view.borderHighlightThickness = borderHighlightThickness.toPx().toInt()
            }

            val calendarValue = jodaToCalendar.getValue(value)
            if (view.selectedDays.single() != calendarValue) view.selectDay(calendarValue)

            view.redrawDays()
        },
        modifier = modifier
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
