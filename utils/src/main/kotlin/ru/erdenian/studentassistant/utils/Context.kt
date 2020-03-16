package ru.erdenian.studentassistant.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import org.joda.time.LocalDate
import org.joda.time.LocalTime

/**
 * Обертка над [ContextCompat.getColor].
 *
 * @param id id цвета
 * @return цвет
 * @author Ilya Solovyov
 * @since 0.0.0
 */
fun Context.getColorCompat(@ColorRes id: Int) = ContextCompat.getColor(this, id)

fun Context.attr(@AttrRes attribute: Int) = TypedValue().also { outValue ->
    require(theme.resolveAttribute(attribute, outValue, true)) {
        "Failed to resolve attribute: $attribute"
    }
}

@ColorInt
fun Context.colorAttr(@AttrRes attribute: Int) = attr(attribute).apply {
    require(type in TypedValue.TYPE_FIRST_COLOR_INT..TypedValue.TYPE_LAST_COLOR_INT) {
        "Attribute value type is not color: $attribute"
    }
}.data

/**
 * Отображает [DatePickerDialog].
 *
 * Выбор даты будет возможен в заданном промежутке дат ([minDate] - [maxDate]).
 *
 * @param preselectedDate изначально выбранный день (если null, используется текущая дата)
 * @param minDate первый день промежутка (если null, используется 1 января 1900)
 * @param maxDate последний день промежутка (если null, используется 31 декабря 2100)
 * @param onDateSet обработчик результата выбора
 * @author Ilya Solovyov
 * @since 0.0.0
 */
fun Context.showDatePicker(
    preselectedDate: LocalDate? = null,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    onDateSet: (selected: LocalDate) -> Unit
) {
    val preselected = preselectedDate ?: LocalDate.now()

    DatePickerDialog(
        this,
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            onDateSet.invoke(LocalDate(year, month + 1, dayOfMonth))
        },
        preselected.year,
        preselected.monthOfYear - 1,
        preselected.dayOfMonth
    ).apply {
        minDate?.let { datePicker.minDate = it.toDate().time }
        maxDate?.let { datePicker.maxDate = it.toDate().time }
    }.show()
}

/**
 * Отображает [TimePickerDialog].
 *
 * @param preselectedTime изначально выбранное время (если null, используется текущее время)
 * @param onTimeSet обработчик результата выбора
 * @author Ilya Solovyov
 * @since 0.0.0
 */
fun Context.showTimePicker(
    preselectedTime: LocalTime? = null,
    onTimeSet: (selected: LocalTime) -> Unit
) {
    val preselected = preselectedTime ?: LocalTime.now()

    TimePickerDialog(
        this,
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            onTimeSet.invoke(LocalTime(hourOfDay, minute))
        },
        preselected.hourOfDay,
        preselected.minuteOfHour,
        true
    ).show()
}
