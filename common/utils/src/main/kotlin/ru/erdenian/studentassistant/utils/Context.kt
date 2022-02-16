package ru.erdenian.studentassistant.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

fun Context.toast(text: CharSequence, length: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, text, length).show()

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
        { _, year, month, dayOfMonth -> onDateSet.invoke(LocalDate.of(year, month + 1, dayOfMonth)) },
        preselected.year,
        preselected.monthValue - 1,
        preselected.dayOfMonth
    ).apply {
        minDate?.let { datePicker.minDate = it.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() }
        maxDate?.let { datePicker.maxDate = it.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() }
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
        { _, hourOfDay, minute -> onTimeSet.invoke(LocalTime.of(hourOfDay, minute)) },
        preselected.hour,
        preselected.minute,
        true
    ).show()
}
