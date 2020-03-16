package ru.erdenian.studentassistant.ui.main.nolessons

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.FragmentNoLessonsBinding
import ru.erdenian.studentassistant.ui.lessoneditor.LessonEditorActivity
import ru.erdenian.studentassistant.ui.main.MainViewModel

class NoLessonsFragment : Fragment(R.layout.fragment_no_lessons) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModel by activityViewModels<MainViewModel>()
        val binding = FragmentNoLessonsBinding.bind(view)
        binding.createLesson.setOnClickListener {
            LessonEditorActivity.start(
                requireContext(), checkNotNull(viewModel.selectedSemester.value).id
            )
        }
    }
}
