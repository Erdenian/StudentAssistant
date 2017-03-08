package ru.erdenian.studentassistant.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.Log
import android.view.ViewGroup
import ru.erdenian.studentassistant.fragment.HomeworksPageFragment

class HomeworksPagerAdapter(fm: FragmentManager, private val semesterId: Long) : FragmentStatePagerAdapter(fm) {

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return "Предстоящие"
            1 -> return "Завершенные"
            else -> throw IllegalArgumentException("Неизвестный номер страницы: $position")
        }
    }

    override fun getItem(position: Int): Fragment {
        return HomeworksPageFragment.newInstance(semesterId, position)
    }

    override fun getCount(): Int {
        return 2
    }

    override fun finishUpdate(container: ViewGroup?) {
        try {
            super.finishUpdate(container)
        } catch (npe: NullPointerException) {
            Log.w(javaClass.toString(), "NPE: Bug workaround")
        }
    }
}
