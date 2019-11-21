package ru.erdenian.studentassistant.ui.main.homeworks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.PagerTabStrip
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.jetbrains.anko.colorAttr
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.ui.homeworkeditor.HomeworkEditorActivity
import ru.erdenian.studentassistant.ui.main.MainViewModel
import ru.erdenian.studentassistant.utils.requireViewByIdCompat

class HomeworksFragment : Fragment(R.layout.fragment_homeworks) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModel by activityViewModels<MainViewModel>()
        val owner = viewLifecycleOwner

        view.requireViewByIdCompat<ViewPager>(R.id.fh_view_pager).apply {
            adapter = HomeworksPagerAdapter(context, childFragmentManager).apply {
                viewModel.selectedSemester.observe(owner) { semester = it }
            }
        }
        view.requireViewByIdCompat<PagerTabStrip>(R.id.fh_pager_tab_strip).apply {
            val color = colorAttr(R.attr.colorPrimary)
            setTextColor(color)
            tabIndicatorColor = color
        }

        view.requireViewByIdCompat<FloatingActionButton>(R.id.fh_add_homework)
            .setOnClickListener {
                HomeworkEditorActivity.start(
                    requireContext(), checkNotNull(viewModel.selectedSemester.value).id
                )
            }
    }
}
