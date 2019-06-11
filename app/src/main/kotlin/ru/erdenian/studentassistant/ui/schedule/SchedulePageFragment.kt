package ru.erdenian.studentassistant.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ViewFlipper
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
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

        val view = requireView()
        val flipper = view.findViewById<ViewFlipper>(R.id.fsp_flipper)
        val flipperProgressIndex = 0
        val flipperLessonsIndex = 1
        val flipperFreeDayIndex = 2

        val viewModel = ViewModelProviders.of(requireActivity()).get<ScheduleViewModel>()
        val date = requireArguments().get(PAGE_DATE) as LocalDate
        viewModel.getLessons(date).observe(this) { value ->
            adapter.lessons = value.list
            flipper.displayedChild =
                if (value.isNotEmpty()) flipperLessonsIndex else flipperFreeDayIndex
        }
    }
}
