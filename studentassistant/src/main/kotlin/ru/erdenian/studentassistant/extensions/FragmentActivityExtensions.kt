package ru.erdenian.studentassistant.extensions

import android.support.v4.app.FragmentActivity
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.R
import java.util.*

/**
 * Отображает date picker.
 *
 * Выбор даты будет возможен в заданном промежутке дат ([firstDay] - [lastDay]).
 *
 * @author Ilya Solovyev
 * @param onDateSetListener обработчик результата выбора
 * @param firstDay первый день промежутка (если null, используется 1 января 1900)
 * @param lastDay последний день промежутка (если null, используется 31 декабря 2100)
 * @param preselected изначально выбранный день (если null, используется текущая дата)
 * @param tag тэг, который будет передан в [onDateSetListener]
 * @since 0.0.0
 */
fun FragmentActivity.showDatePicker(onDateSetListener: CalendarDatePickerDialogFragment.OnDateSetListener,
                                    firstDay: LocalDate? = null, lastDay: LocalDate? = null,
                                    preselected: LocalDate? = LocalDate.now(), tag: String = "date_picker") {
  CalendarDatePickerDialogFragment().apply {
    firstDayOfWeek = Calendar.MONDAY
    setThemeCustom(R.style.DatePicker)
    setDateRange(firstDay?.toCalendarDay(), lastDay?.toCalendarDay())
    setOnDateSetListener(onDateSetListener)
    preselected?.run { setPreselectedDate(year, monthOfYear - 1, dayOfMonth) }
  }.show(supportFragmentManager, tag)
}

/**
 * Отображает time picker.
 *
 * @author Ilya Solovyev
 * @param onTimeSetListener обработчик результата выбора
 * @param preselected изначально выбранное время (если null, используется текущее время)
 * @param tag тэг, который будет передан в [onTimeSetListener]
 * @since 0.0.0
 */
fun FragmentActivity.showTimePicker(onTimeSetListener: RadialTimePickerDialogFragment.OnTimeSetListener,
                                    preselected: LocalTime? = null, tag: String = "time_picker") {
  RadialTimePickerDialogFragment().apply {
    setOnTimeSetListener(onTimeSetListener)
    preselected?.run { setStartTime(hourOfDay, minuteOfHour) }
  }.show(supportFragmentManager, tag)
}