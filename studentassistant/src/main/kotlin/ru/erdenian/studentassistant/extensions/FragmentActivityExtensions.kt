package ru.erdenian.studentassistant.extensions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import org.joda.time.LocalDate
import org.joda.time.LocalTime

/**
 * Отображает date picker.
 *
 * Выбор даты будет возможен в заданном промежутке дат ([minDate] - [maxDate]).
 *
 * @author Ilya Solovyev
 * @param preselectedDate изначально выбранный день (если null, используется текущая дата)
 * @param minDate первый день промежутка (если null, используется 1 января 1900)
 * @param maxDate последний день промежутка (если null, используется 31 декабря 2100)
 * @param onDateSet обработчик результата выбора
 * @since 0.0.0
 */
fun Context.showDatePicker(preselectedDate: LocalDate? = null,
                           minDate: LocalDate? = null, maxDate: LocalDate? = null,
                           onDateSet: (LocalDate) -> Unit) {
  val preselected = preselectedDate ?: LocalDate.now()

  DatePickerDialog(this,
      DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        onDateSet.invoke(LocalDate(year, month + 1, dayOfMonth))
      },
      preselected.year,
      preselected.monthOfYear,
      preselected.dayOfMonth
  ).apply {
    minDate?.let { datePicker.minDate = it.toDate().time }
    maxDate?.let { datePicker.maxDate = it.toDate().time }
  }.show()
}

/**
 * Отображает time picker.
 *
 * @author Ilya Solovyev
 * @param preselectedTime изначально выбранное время (если null, используется текущее время)
 * @param onTimeSet обработчик результата выбора
 * @since 0.0.0
 */
fun Context.showTimePicker(preselectedTime: LocalTime? = null, onTimeSet: (LocalTime) -> Unit) {
  val preselected = preselectedTime ?: LocalTime.now()

  TimePickerDialog(this,
      TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        onTimeSet.invoke(LocalTime(hourOfDay, minute))
      },
      preselected.hourOfDay,
      preselected.minuteOfHour,
      true
  ).show()
}
