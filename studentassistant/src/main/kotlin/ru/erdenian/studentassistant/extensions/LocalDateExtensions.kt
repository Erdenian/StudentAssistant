package ru.erdenian.studentassistant.extensions

import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter
import org.joda.time.LocalDate

fun LocalDate.toCalendarDay(): MonthAdapter.CalendarDay {
    return MonthAdapter.CalendarDay(year, monthOfYear - 1, dayOfMonth)
}