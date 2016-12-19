package ru.erdenian.studentassistant.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import org.joda.time.Days
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.fragment.SchedulePageFragment
import ru.erdenian.studentassistant.schedule.Semester

class SchedulePagerAdapter(fm: FragmentManager, private val semester: Semester, val showWeeksAndDates: Boolean) :
        FragmentStatePagerAdapter(fm) {

    companion object {
        private const val TITLE_FORMAT = "EEEE, dd MMMM"
        private const val TITLE_FORMAT_FULL = "EEEE, dd MMMM yyyy"
    }

    override fun getPageTitle(position: Int): CharSequence {
        if (showWeeksAndDates) return LocalDate().withDayOfWeek(position + 1).dayOfWeek().asText

        val day = semester.firstDay.plusDays(position)
        val title = StringBuffer()
        if (day.year == LocalDate.now().year) {
            title.append(day.toString(TITLE_FORMAT))
        } else {
            title.append(day.toString(TITLE_FORMAT_FULL))
        }

        return title
    }

    override fun getItem(position: Int): Fragment {
        return if (showWeeksAndDates) SchedulePageFragment.newInstance(semester.id, position + 1)
        else SchedulePageFragment.newInstance(semester.id, getDate(position))
    }

    override fun getCount(): Int {
        return if (showWeeksAndDates) 7 else semester.length
    }

    fun getPosition(date: LocalDate): Int {
        if (showWeeksAndDates) throw UnsupportedOperationException("showWeeksAndDates = $showWeeksAndDates")
        return Days.daysBetween(semester.firstDay, date).days
    }

    fun getDate(position: Int): LocalDate {
        if (showWeeksAndDates) throw UnsupportedOperationException("showWeeksAndDates = $showWeeksAndDates")
        return semester.firstDay.plusDays(position)
    }
}
