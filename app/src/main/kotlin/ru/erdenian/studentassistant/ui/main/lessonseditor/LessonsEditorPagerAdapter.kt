package ru.erdenian.studentassistant.ui.main.lessonseditor

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate

class LessonsEditorPagerAdapter(
    fm: FragmentManager
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val weekdays = List<String>(DateTimeConstants.DAYS_PER_WEEK) { i ->
        LocalDate().withDayOfWeek(i + 1).dayOfWeek().asText
    }

    override fun getCount() = weekdays.size

    override fun getPageTitle(position: Int) = weekdays[position]

    override fun getItem(position: Int) = LessonsEditorPageFragment.newInstance(position + 1)
}
