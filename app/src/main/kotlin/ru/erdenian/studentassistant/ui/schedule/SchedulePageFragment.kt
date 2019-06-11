package ru.erdenian.studentassistant.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ViewFlipper
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.getViewModel
import ru.erdenian.studentassistant.ui.adapter.LessonsListAdapter

class SchedulePageFragment : Fragment() {

    companion object {
        private const val PAGE_DATE = "page_date"

        fun newInstance(date: LocalDate) = SchedulePageFragment().apply {
            arguments = bundleOf(PAGE_DATE to date)
        }
    }

    private val adapter = LessonsListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_schedule_page, container, false).apply {
        with(findViewById<RecyclerView>(R.id.fsp_lessons)) {
            adapter = this.adapter
            layoutManager = LinearLayoutManager(inflater.context)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val date = requireArguments().get(PAGE_DATE) as LocalDate
        val lessons = requireActivity().getViewModel<ScheduleViewModel>().getLessons(date)

        requireView().findViewById<ViewFlipper>(R.id.fsp_flipper).apply {
            val lessonsIndex = 0
            val freeDayIndex = 1
            lessons.observe(this@SchedulePageFragment) { value ->
                displayedChild = if (value.isNotEmpty()) lessonsIndex else freeDayIndex
            }
        }

        lessons.observe(this) { adapter.lessons = it.list }
    }
}
