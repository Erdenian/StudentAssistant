package ru.erdenian.studentassistant.ui.main.schedule

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.PageFragmentScheduleBinding
import ru.erdenian.studentassistant.ui.adapter.LessonsListAdapter
import ru.erdenian.studentassistant.ui.adapter.SpacingItemDecoration

class SchedulePageFragment : Fragment(R.layout.page_fragment_schedule) {

    companion object {
        private const val PAGE_DATE = "page_date"

        fun newInstance(date: LocalDate) = SchedulePageFragment().apply {
            arguments = bundleOf(PAGE_DATE to date)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        @Suppress("UnsafeCast")
        val date = requireArguments().get(PAGE_DATE) as LocalDate
        val binding = PageFragmentScheduleBinding.bind(view)
        val viewModel by requireParentFragment().viewModels<ScheduleViewModel>()
        val lessons = viewModel.getLessons(date)

        binding.lessons.apply {
            adapter = LessonsListAdapter().apply {
                onLessonClickListener = { findNavController().navigate(ScheduleFragmentDirections.showLessonInformation(it)) }
                lessons.observe(viewLifecycleOwner) { this.lessons = it.list }
            }
            layoutManager = LinearLayoutManager(view.context)
            addItemDecoration(SpacingItemDecoration(requireContext().resources.getDimensionPixelSize(R.dimen.cards_spacing)))
        }

        binding.flipper.apply {
            val lessonsIndex = 0
            val freeDayIndex = 1
            lessons.observe(viewLifecycleOwner) { displayedChild = if (it.isNotEmpty()) lessonsIndex else freeDayIndex }
        }
    }
}
