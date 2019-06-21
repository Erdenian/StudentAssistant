package ru.erdenian.studentassistant.ui.lessonseditor

import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ViewFlipper
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.getViewModel
import ru.erdenian.studentassistant.repository.entity.Lesson
import ru.erdenian.studentassistant.ui.adapter.LessonsListAdapter
import ru.erdenian.studentassistant.ui.lessoneditor.LessonEditorActivity

class LessonsEditorPageFragment : Fragment() {

    companion object {
        private const val PAGE_WEEKDAY = "page_weekday"

        fun newInstance(weekday: Int) = LessonsEditorPageFragment().apply {
            arguments = bundleOf(PAGE_WEEKDAY to weekday)
        }
    }

    private val viewModel by lazy { requireActivity().getViewModel<LessonsEditorViewModel>() }
    private val adapter = LessonsListAdapter(true).apply {
        onLessonClickListener = object : LessonsListAdapter.OnLessonClickListener {
            override fun onLessonClick(lesson: Lesson) {
                LessonEditorActivity.start(requireContext(), lesson)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_lessons_editor_page, container, false).apply {
        with(findViewById<RecyclerView>(R.id.flep_lessons)) {
            adapter = this@LessonsEditorPageFragment.adapter
            layoutManager = LinearLayoutManager(inflater.context)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val weekday = requireArguments().getInt(PAGE_WEEKDAY)
        val lessons = viewModel.getLessons(weekday)

        requireView().findViewById<ViewFlipper>(R.id.flep_flipper).apply {
            val lessonsIndex = 0
            val freeDayIndex = 1
            lessons.observe(this@LessonsEditorPageFragment) { value ->
                displayedChild = if (value.isNotEmpty()) lessonsIndex else freeDayIndex
            }
        }

        lessons.observe(this) { value ->
            adapter.lessons = value.list
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        requireActivity().menuInflater.inflate(R.menu.context_menu_schedule_page_fragment, menu)
        (menuInfo as AdapterView.AdapterContextMenuInfo?)?.run {
            menu.setHeaderTitle(adapter.lessons[position].subjectName)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val lesson = adapter.lessons[info.position]
        return when (item.itemId) {
            R.id.context_menu_schedule_page_fragment_copy -> {
                LessonEditorActivity.start(requireContext(), lesson, true)
                true
            }
            R.id.context_menu_schedule_page_fragment_delete -> {
                viewModel.viewModelScope.launch {
                    if (viewModel.isLastLessonOfSubjectsAndHasHomeworks(lesson)) {
                        requireContext().alert(
                            R.string.lea_delete_homeworks_message,
                            R.string.lea_delete_homeworks_title
                        ) {
                            positiveButton(R.string.lea_delete_homeworks_yes) {
                                viewModel.viewModelScope.launch { viewModel.delete(lesson) }
                            }
                            negativeButton(R.string.lea_delete_homeworks_cancel) {}
                        }.show()
                    } else {
                        requireContext().alert(R.string.lea_delete_message) {
                            positiveButton(R.string.lea_delete_yes) {
                                viewModel.viewModelScope.launch { viewModel.delete(lesson) }
                            }
                            negativeButton(R.string.lea_delete_no) {}
                        }.show()
                    }
                }
                true
            }
            else -> false
        }
    }
}
