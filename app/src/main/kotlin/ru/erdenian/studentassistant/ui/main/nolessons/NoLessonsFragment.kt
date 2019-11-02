package ru.erdenian.studentassistant.ui.main.nolessons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.ui.lessoneditor.LessonEditorActivity
import ru.erdenian.studentassistant.ui.main.MainViewModel
import ru.erdenian.studentassistant.utils.requireViewByIdCompat

class NoLessonsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_no_lessons, container, false).apply {
        val viewModel by activityViewModels<MainViewModel>()
        requireViewByIdCompat<Button>(R.id.fnl_create_lesson).setOnClickListener {
            LessonEditorActivity.start(
                requireContext(), checkNotNull(viewModel.selectedSemester.value).id
            )
        }
    }
}
