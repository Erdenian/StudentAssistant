package ru.erdenian.studentassistant.ui.lessonseditor

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
import ru.erdenian.studentassistant.R

class LessonsEditorPageFragment : Fragment() {

    companion object {
        private const val PAGE_WEEKDAY = "page_weekday"

        fun newInstance(weekday: Int) = LessonsEditorPageFragment().apply {
            arguments = bundleOf(PAGE_WEEKDAY to weekday)
        }
    }

    private val adapter = LessonsEditorListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_lessons_editor_page, container, false).apply {
        with(findViewById<RecyclerView>(R.id.flep_lessons)) {
            adapter = this.adapter
            layoutManager = LinearLayoutManager(inflater.context)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val view = requireView()
        val flipper = view.findViewById<ViewFlipper>(R.id.flep_flipper)
        val flipperProgressIndex = 0
        val flipperLessonsIndex = 1
        val flipperFreeDayIndex = 2

        val viewModel = ViewModelProviders.of(requireActivity()).get<LessonsEditorViewModel>()
        val weekday = requireArguments().getInt(PAGE_WEEKDAY)
        viewModel.getLessons(weekday).observe(this) { value ->
            adapter.lessons = value.list
            flipper.displayedChild =
                if (value.isNotEmpty()) flipperLessonsIndex else flipperFreeDayIndex
        }
    }
}
