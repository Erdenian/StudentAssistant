package ru.erdenian.studentassistant.ui.main.schedule

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import org.joda.time.Days
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.model.entity.Semester

class SchedulePagerAdapter(
    fm: FragmentManager
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        private const val TITLE_FORMAT = "EEEE, dd MMMM"
        private const val TITLE_FORMAT_FULL = "EEEE, dd MMMM yyyy"
    }

    var semester: Semester? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getCount(): Int = semester?.length ?: 0

    override fun getPageTitle(position: Int): CharSequence {
        val date = semester?.firstDay?.plusDays(position) ?: return ""
        return date.toString(
            if (date.year == LocalDate.now().year) TITLE_FORMAT
            else TITLE_FORMAT_FULL
        )
    }

    override fun getItem(position: Int) = SchedulePageFragment.newInstance(getDate(position))

    fun getPosition(date: LocalDate) = checkNotNull(semester).run {
        Days.daysBetween(firstDay, date.coerceIn(range)).days
    }

    fun getDate(position: Int): LocalDate =
        checkNotNull(semester).firstDay.plusDays(position)
}
