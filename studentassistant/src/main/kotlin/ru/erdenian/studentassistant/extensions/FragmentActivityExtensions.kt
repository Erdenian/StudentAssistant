package ru.erdenian.studentassistant.extensions

import android.support.v4.app.FragmentActivity
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import java.util.*

fun FragmentActivity.showDatePicker(onDateSetListener: CalendarDatePickerDialogFragment.OnDateSetListener,
                                    firstDay: LocalDate? = null, lastDay: LocalDate? = null,
                                    preselected: LocalDate? = LocalDate.now(), tag: String = "date_picker") {
    var dialog = CalendarDatePickerDialogFragment()
            .setFirstDayOfWeek(Calendar.MONDAY)
            .setThemeCustom(R.style.DatePicker)
            .setDateRange(firstDay?.toCalendarDay(), lastDay?.toCalendarDay())
            .setOnDateSetListener(onDateSetListener)

    if (preselected != null)
        dialog = dialog.setPreselectedDate(preselected.year, preselected.monthOfYear - 1, preselected.dayOfMonth)

    dialog.show(supportFragmentManager, tag)
}