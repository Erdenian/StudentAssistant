package ru.erdenian.studentassistant.ui.main.homeworks

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import ru.erdenian.studentassistant.R

class HomeworksPagerAdapter(
    context: Context,
    fm: FragmentManager
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val titles: Array<String> = context.resources.getStringArray(R.array.homeworks_pages)

    override fun getCount() = titles.size

    override fun getPageTitle(position: Int) = titles[position]

    override fun getItem(position: Int) = HomeworksPageFragment.newInstance(position == 0)
}
