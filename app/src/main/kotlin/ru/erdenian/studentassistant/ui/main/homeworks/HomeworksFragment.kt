package ru.erdenian.studentassistant.ui.main.homeworks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerTabStrip
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.ui.homeworkeditor.HomeworkEditorActivity
import ru.erdenian.studentassistant.ui.main.MainViewModel
import ru.erdenian.studentassistant.utils.getActivityViewModel
import ru.erdenian.studentassistant.utils.getColorCompat
import ru.erdenian.studentassistant.utils.requireViewByIdCompat

class HomeworksFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_homeworks, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModel = getActivityViewModel<MainViewModel>()
        val owner = viewLifecycleOwner

        view.requireViewByIdCompat<ViewPager>(R.id.fh_view_pager).apply {
            adapter = HomeworksPagerAdapter(context, childFragmentManager).apply {
                viewModel.selectedSemester.observe(owner) { semester = it }
            }
        }
        view.requireViewByIdCompat<PagerTabStrip>(R.id.fh_pager_tab_strip).apply {
            setTextColor(getColorCompat(R.color.primary))
            setTabIndicatorColorResource(R.color.primary)
        }

        view.requireViewByIdCompat<FloatingActionButton>(R.id.fh_add_homework)
            .setOnClickListener {
                HomeworkEditorActivity.start(
                    requireContext(), checkNotNull(viewModel.selectedSemester.value).id
                )
            }
    }
}
