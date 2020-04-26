package ru.erdenian.studentassistant.ui.lessonseditor

import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.PageFragmentLessonsEditorBinding
import ru.erdenian.studentassistant.ui.adapter.LessonsListAdapter
import ru.erdenian.studentassistant.ui.adapter.SpacingItemDecoration
import ru.erdenian.studentassistant.ui.lessoneditor.LessonEditorActivity

class LessonsEditorPageFragment : Fragment(R.layout.page_fragment_lessons_editor) {

    companion object {
        private const val PAGE_WEEKDAY = "page_weekday"

        fun newInstance(weekday: Int) = LessonsEditorPageFragment().apply {
            arguments = bundleOf(PAGE_WEEKDAY to weekday)
        }
    }

    private val viewModel by activityViewModels<LessonsEditorViewModel>()
    private val adapter = LessonsListAdapter().apply {
        onLessonClickListener = { LessonEditorActivity.start(requireContext(), it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = PageFragmentLessonsEditorBinding.bind(view)

        binding.lessons.apply {
            adapter = this@LessonsEditorPageFragment.adapter
            layoutManager = LinearLayoutManager(view.context)
            addItemDecoration(SpacingItemDecoration(context.resources.getDimensionPixelSize(R.dimen.cards_spacing)))
            registerForContextMenu(this)
        }

        val weekday = requireArguments().getInt(PAGE_WEEKDAY)
        val lessons = viewModel.getLessons(weekday)

        binding.flipper.apply {
            val lessonsIndex = 0
            val freeDayIndex = 1
            lessons.observe(viewLifecycleOwner) { displayedChild = if (it.isNotEmpty()) lessonsIndex else freeDayIndex }
        }

        lessons.observe(viewLifecycleOwner) { adapter.lessons = it.list }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        requireActivity().menuInflater.inflate(R.menu.context_lessons_editor, menu)
        @Suppress("UnsafeCast")
        (menuInfo as AdapterView.AdapterContextMenuInfo?)?.run {
            menu.setHeaderTitle(adapter.lessons[position].subjectName)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        @Suppress("UnsafeCast")
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val lesson = adapter.lessons[info.position]
        return when (item.itemId) {
            R.id.cle_copy -> {
                LessonEditorActivity.start(requireContext(), lesson, true)
                true
            }
            R.id.cle_delete -> {
                lifecycleScope.launch {
                    if (viewModel.isLastLessonOfSubjectsAndHasHomeworks(lesson)) {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(R.string.lea_delete_homeworks_title)
                            .setMessage(R.string.lea_delete_homeworks_message)
                            .setPositiveButton(R.string.lea_delete_homeworks_yes) { _, _ ->
                                viewModel.deleteLesson(lesson, true)
                            }
                            .setNegativeButton(R.string.lea_delete_homeworks_no) { _, _ ->
                                viewModel.deleteLesson(lesson, false)
                            }
                            .setNeutralButton(R.string.lea_delete_homeworks_cancel, null)
                            .show()
                    } else {
                        MaterialAlertDialogBuilder(requireContext())
                            .setMessage(R.string.lea_delete_message)
                            .setPositiveButton(R.string.lea_delete_yes) { _, _ ->
                                viewModel.viewModelScope.launch { viewModel.deleteLesson(lesson) }
                            }
                            .setNegativeButton(R.string.lea_delete_no, null)
                            .show()
                    }
                }
                true
            }
            else -> false
        }
    }
}
