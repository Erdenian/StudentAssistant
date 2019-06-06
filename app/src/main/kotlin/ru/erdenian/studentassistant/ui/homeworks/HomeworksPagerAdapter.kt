package ru.erdenian.studentassistant.ui.homeworks

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.repository.entity.SemesterNew

class HomeworksPagerAdapter(
    context: Context,
    fm: FragmentManager
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    var semester: SemesterNew? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val titles = context.resources.getStringArray(R.array.homeworks_pages)

    override fun getCount() = titles.size

    override fun getPageTitle(position: Int): CharSequence = titles[position]

    override fun getItem(position: Int) = HomeworksPageFragment.newInstance(position == 0)
}
