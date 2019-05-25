package ru.erdenian.studentassistant.ui.schedule

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import org.joda.time.Days
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.schedule.Semester

class SchedulePagerAdapter(
    fm: FragmentManager,
    private val semester: Semester
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        private const val TITLE_FORMAT = "EEEE, dd MMMM"
        private const val TITLE_FORMAT_FULL = "EEEE, dd MMMM yyyy"
    }

    override fun getCount(): Int = semester.length

    override fun getPageTitle(position: Int): CharSequence {
        val date = semester.firstDay.plusDays(position)
        return date.toString(
            if (date.year == LocalDate.now().year) TITLE_FORMAT
            else TITLE_FORMAT_FULL
        )
    }

    override fun getItem(position: Int) = SchedulePageFragment.newInstance(getDate(position))

    fun getPosition(date: LocalDate): Int = Days.daysBetween(semester.firstDay, date).days
    fun getDate(position: Int): LocalDate = semester.firstDay.plusDays(position)
}
