package ru.erdenian.studentassistant.ui.main.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ViewFlipper
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.dimen
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.model.entity.Lesson
import ru.erdenian.studentassistant.ui.adapter.LessonsListAdapter
import ru.erdenian.studentassistant.ui.adapter.SpacingItemDecoration
import ru.erdenian.studentassistant.ui.lessoninformation.LessonInformationActivity
import ru.erdenian.studentassistant.ui.main.MainViewModel
import ru.erdenian.studentassistant.utils.getViewModel
import ru.erdenian.studentassistant.utils.requireViewByIdCompat

class SchedulePageFragment : Fragment() {

    companion object {
        private const val PAGE_DATE = "page_date"

        fun newInstance(date: LocalDate) = SchedulePageFragment().apply {
            arguments = bundleOf(PAGE_DATE to date)
        }
    }

    private val adapter = LessonsListAdapter().apply {
        onLessonClickListener = object : LessonsListAdapter.OnLessonClickListener {
            override fun onLessonClick(lesson: Lesson) {
                LessonInformationActivity.start(requireContext(), lesson)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.page_fragment_schedule, container, false).apply {
        with(requireViewByIdCompat<RecyclerView>(R.id.pfs_lessons)) {
            adapter = this@SchedulePageFragment.adapter
            layoutManager = LinearLayoutManager(inflater.context)
            addItemDecoration(SpacingItemDecoration(dimen(R.dimen.cards_spacing)))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        @Suppress("UnsafeCast")
        val date = requireArguments().get(PAGE_DATE) as LocalDate
        val lessons = requireActivity().getViewModel<MainViewModel>().getLessons(date)

        view.requireViewByIdCompat<ViewFlipper>(R.id.pfs_flipper).apply {
            val lessonsIndex = 0
            val freeDayIndex = 1
            lessons.observe(this@SchedulePageFragment) { value ->
                displayedChild = if (value.isNotEmpty()) lessonsIndex else freeDayIndex
            }
        }

        lessons.observe(this) {
            adapter.lessons = it.list
        }
    }
}
