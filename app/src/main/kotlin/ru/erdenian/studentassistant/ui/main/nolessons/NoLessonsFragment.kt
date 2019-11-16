package ru.erdenian.studentassistant.ui.main.nolessons

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.ui.lessoneditor.LessonEditorActivity
import ru.erdenian.studentassistant.ui.main.MainViewModel
import ru.erdenian.studentassistant.utils.requireViewByIdCompat

class NoLessonsFragment : Fragment(R.layout.fragment_no_lessons) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModel by activityViewModels<MainViewModel>()
        view.requireViewByIdCompat<Button>(R.id.fnl_create_lesson).setOnClickListener {
            LessonEditorActivity.start(
                requireContext(), checkNotNull(viewModel.selectedSemester.value).id
            )
        }
    }
}
