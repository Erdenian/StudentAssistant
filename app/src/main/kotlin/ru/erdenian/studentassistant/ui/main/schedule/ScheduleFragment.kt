package ru.erdenian.studentassistant.ui.main.schedule

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.FragmentScheduleBinding
import ru.erdenian.studentassistant.ui.lessonseditor.LessonsEditorActivity
import ru.erdenian.studentassistant.ui.semestereditor.SemesterEditorActivity
import ru.erdenian.studentassistant.utils.binding
import ru.erdenian.studentassistant.utils.colorAttr
import ru.erdenian.studentassistant.utils.getColorCompat
import ru.erdenian.studentassistant.utils.setColor
import ru.erdenian.studentassistant.utils.showDatePicker

class ScheduleFragment : Fragment(R.layout.fragment_schedule) {

    companion object {
        private const val PAGE_DATE = "page_date"
    }

    private val viewModel by viewModels<ScheduleViewModel>()

    private val binding by binding { FragmentScheduleBinding.bind(requireView()) }

    private var selectedDate: LocalDate? = LocalDate.now()
    private val pagerAdapter by lazy {
        SchedulePagerAdapter(childFragmentManager).apply {
            viewModel.selectedSemester.observe(this@ScheduleFragment) { semester ->
                if (count > 0) selectedDate = getDate(binding.viewPager.currentItem)
                this.semester = semester
                if (count > 0) binding.viewPager.setCurrentItem(
                    getPosition(selectedDate ?: LocalDate.now()), false
                )
                selectedDate = null
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.selectedSemester.observe(viewLifecycleOwner) { setHasOptionsMenu(true) }

        binding.viewPager.adapter = pagerAdapter
        binding.pagerTabStrip.apply {
            val color = requireContext().colorAttr(R.attr.colorPrimary)
            setTextColor(color)
            tabIndicatorColor = color
        }

        binding.flipper.apply {
            val scheduleIndex = 0
            val noScheduleIndex = 1
            viewModel.selectedSemester.observe(viewLifecycleOwner) { semester ->
                displayedChild = if (semester != null) scheduleIndex else noScheduleIndex
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if ((view != null) && (pagerAdapter.semester != null)) {
            outState.putSerializable(PAGE_DATE, pagerAdapter.getDate(binding.viewPager.currentItem))
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        selectedDate = savedInstanceState?.getSerializable(PAGE_DATE) as? LocalDate
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_schedule, menu)
        menu.setColor(requireContext().getColorCompat(R.color.menu))
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val hasSchedule = (viewModel.selectedSemester.value != null)
        menu.findItem(R.id.ms_calendar).isVisible = hasSchedule
        menu.findItem(R.id.ms_add_schedule).setShowAsAction(
            if (hasSchedule) MenuItem.SHOW_AS_ACTION_NEVER else MenuItem.SHOW_AS_ACTION_IF_ROOM
        )
        menu.findItem(R.id.ms_edit_schedule).isVisible = hasSchedule
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.ms_calendar -> {
            viewModel.selectedSemester.value?.run {
                requireContext().showDatePicker(pagerAdapter.getDate(binding.viewPager.currentItem), firstDay, lastDay) {
                    binding.viewPager.currentItem = pagerAdapter.getPosition(it)
                }
            }
            true
        }
        R.id.ms_add_schedule -> {
            SemesterEditorActivity.start(requireContext())
            true
        }
        R.id.ms_edit_schedule -> {
            LessonsEditorActivity.start(requireContext(), checkNotNull(viewModel.selectedSemester.value))
            true
        }
        else -> false
    }
}
