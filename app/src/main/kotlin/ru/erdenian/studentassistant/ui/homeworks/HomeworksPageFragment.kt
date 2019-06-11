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
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.getViewModel
import ru.erdenian.studentassistant.ui.adapter.HomeworksListAdapter

class HomeworksPageFragment : Fragment() {

    companion object {
        private const val IS_ACTUAL = "is_actual"

        fun newInstance(isActual: Boolean) = HomeworksPageFragment().apply {
            arguments = bundleOf(IS_ACTUAL to isActual)
        }
    }

    private val adapter = HomeworksListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_homeworks_page, container, false).apply {
        with(findViewById<RecyclerView>(R.id.fhp_homeworks)) {
            adapter = this.adapter
            layoutManager = LinearLayoutManager(inflater.context)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val view = requireView()
        val flipper = view.findViewById<ViewFlipper>(R.id.fhp_flipper)
        val flipperProgressIndex = 0
        val flipperHomeworksIndex = 1
        val flipperNoHomeworksIndex = 2

        requireActivity().getViewModel<HomeworksViewModel>().run {
            if (requireArguments().getBoolean(IS_ACTUAL)) getActualHomeworks()
            else getPastHomeworks()
        }.observe(this) { value ->
            adapter.homeworks = value.list
            flipper.displayedChild =
                if (value.isNotEmpty()) flipperHomeworksIndex else flipperNoHomeworksIndex
        }
    }
}
