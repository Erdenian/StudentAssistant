package ru.erdenian.studentassistant.ui.homeworks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ViewFlipper
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.dimen
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.getViewModel
import ru.erdenian.studentassistant.repository.entity.Homework
import ru.erdenian.studentassistant.ui.adapter.HomeworksListAdapter
import ru.erdenian.studentassistant.ui.adapter.SpacingItemDecoration
import ru.erdenian.studentassistant.ui.homeworkeditor.HomeworkEditorActivity

class HomeworksPageFragment : Fragment() {

    companion object {
        private const val IS_ACTUAL = "is_actual"

        fun newInstance(isActual: Boolean) = HomeworksPageFragment().apply {
            arguments = bundleOf(IS_ACTUAL to isActual)
        }
    }

    private val adapter = HomeworksListAdapter().apply {
        onHomeworkClickListener = object : HomeworksListAdapter.OnHomeworkClickListener {
            override fun onHomeworkClick(homework: Homework) {
                HomeworkEditorActivity.start(requireContext(), homework)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_homeworks_page, container, false).apply {
        with(findViewById<RecyclerView>(R.id.fhp_homeworks)) {
            adapter = this@HomeworksPageFragment.adapter
            layoutManager = LinearLayoutManager(inflater.context)
            addItemDecoration(SpacingItemDecoration(dimen(R.dimen.cards_spacing)))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val isActual = requireArguments().getBoolean(IS_ACTUAL)
        val homeworks = requireActivity().getViewModel<HomeworksViewModel>().run {
            if (isActual) getActualHomeworks() else getPastHomeworks()
        }

        requireView().findViewById<ViewFlipper>(R.id.fhp_flipper).apply {
            val homeworksIndex = 0
            val noHomeworksIndex = 1
            homeworks.observe(this@HomeworksPageFragment) { value ->
                displayedChild = if (value.isNotEmpty()) homeworksIndex else noHomeworksIndex
            }
        }

        homeworks.observe(this) { adapter.homeworks = it.list }
    }
}
