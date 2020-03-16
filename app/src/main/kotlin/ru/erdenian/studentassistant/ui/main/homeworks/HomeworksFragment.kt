package ru.erdenian.studentassistant.ui.main.homeworks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.FragmentHomeworksBinding
import ru.erdenian.studentassistant.ui.homeworkeditor.HomeworkEditorActivity
import ru.erdenian.studentassistant.ui.main.MainViewModel
import ru.erdenian.studentassistant.utils.colorAttr

class HomeworksFragment : Fragment(R.layout.fragment_homeworks) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModel by activityViewModels<MainViewModel>()
        val owner = viewLifecycleOwner
        val binding = FragmentHomeworksBinding.bind(view)

        binding.viewPager.apply {
            adapter = HomeworksPagerAdapter(context, childFragmentManager).apply {
                viewModel.selectedSemester.observe(owner) { semester = it }
            }
        }
        binding.pagerTabStrip.apply {
            val color = requireContext().colorAttr(R.attr.colorPrimary)
            setTextColor(color)
            tabIndicatorColor = color
        }

        binding.addHomework.setOnClickListener {
            HomeworkEditorActivity.start(
                requireContext(), checkNotNull(viewModel.selectedSemester.value).id
            )
        }
    }
}
