package ru.erdenian.studentassistant.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import org.joda.time.Days
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.fragment.SchedulePageFragment
import ru.erdenian.studentassistant.schedule.Semester

/**
 * Todo: описание класса.
 *
 * @author Ilya Solovyev
 * @version 0.0.0
 * @since 0.0.0
 */
class SchedulePagerAdapter(fm: FragmentManager, private val semester: Semester) : FragmentStatePagerAdapter(fm) {

    companion object {
        private const val TITLE_FORMAT = "EEEE, dd MMMM"
        private const val TITLE_FORMAT_FULL = "EEEE, dd MMMM yyyy"
    }

    override fun getPageTitle(position: Int): CharSequence {
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
        return SchedulePageFragment.newInstance(semester.id, semester.firstDay.plusDays(position))
    }

    override fun getCount(): Int {
        return semester.length
    }

    fun getPosition(date: LocalDate): Int {
        return Days.daysBetween(semester.firstDay, date).days
    }

    fun getDate(position: Int): LocalDate {
        return semester.firstDay.plusDays(position)
    }
}
