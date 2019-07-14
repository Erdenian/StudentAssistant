package ru.erdenian.studentassistant.ui.main.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.viewpager.widget.PagerTabStrip
import androidx.viewpager.widget.ViewPager
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.ui.main.MainViewModel
import ru.erdenian.studentassistant.utils.getCompatColor
import ru.erdenian.studentassistant.utils.lazyActivityViewModel
import ru.erdenian.studentassistant.utils.requireViewByIdCompat
import ru.erdenian.studentassistant.utils.setColor
import ru.erdenian.studentassistant.utils.showDatePicker

class ScheduleFragment : Fragment() {

    private val viewModel by lazyActivityViewModel<MainViewModel>()

    private val pager by lazy { requireView().requireViewByIdCompat<ViewPager>(R.id.fs_view_pager) }
    private val pagerAdapter by lazy {
        SchedulePagerAdapter(childFragmentManager).apply {
            var selectedDate = LocalDate.now()
            viewModel.selectedSemester.observe(this@ScheduleFragment) { semester ->
                if (count > 0) selectedDate = getDate(pager.currentItem)
                this.semester = semester
                if (count > 0) pager.setCurrentItem(
                    getPosition(selectedDate ?: LocalDate.now()), false
                )
                selectedDate = null
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_schedule, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        pager.adapter = pagerAdapter
        requireView().requireViewByIdCompat<PagerTabStrip>(R.id.fs_pager_tab_strip).apply {
            setTextColor(getCompatColor(R.color.colorPrimary))
            setTabIndicatorColorResource(R.color.colorPrimary)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_schedule, menu)
        menu.setColor(getCompatColor(R.color.action_bar_icons_color))
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val isNotEmpty = viewModel.allSemesters.safeValue?.isNotEmpty() ?: false
        menu.findItem(R.id.menu_schedule_calendar).isVisible = isNotEmpty
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.menu_schedule_calendar -> {
            viewModel.selectedSemester.value?.run {
                requireContext().showDatePicker(
                    pagerAdapter.getDate(pager.currentItem), firstDay, lastDay
                ) { pager.currentItem = pagerAdapter.getPosition(it) }
            }
            true
        }
        else -> false
    }
}
