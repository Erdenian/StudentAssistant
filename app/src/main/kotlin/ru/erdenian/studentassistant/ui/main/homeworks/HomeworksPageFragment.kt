package ru.erdenian.studentassistant.ui.main.homeworks

import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.jetbrains.anko.dimen
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.PageFragmentHomeworksBinding
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.ui.adapter.HomeworksListAdapter
import ru.erdenian.studentassistant.ui.adapter.SpacingItemDecoration
import ru.erdenian.studentassistant.ui.homeworkeditor.HomeworkEditorActivity
import ru.erdenian.studentassistant.ui.main.MainViewModel

class HomeworksPageFragment : Fragment(R.layout.page_fragment_homeworks) {

    companion object {
        private const val IS_ACTUAL = "is_actual"

        fun newInstance(isActual: Boolean) = HomeworksPageFragment().apply {
            arguments = bundleOf(IS_ACTUAL to isActual)
        }
    }

    private val viewModel by activityViewModels<MainViewModel>()

    private val adapter = HomeworksListAdapter().apply {
        onHomeworkClickListener = object : HomeworksListAdapter.OnHomeworkClickListener {
            override fun onHomeworkClick(homework: Homework) {
                HomeworkEditorActivity.start(requireContext(), homework)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = PageFragmentHomeworksBinding.bind(view)

        binding.homeworks.apply {
            adapter = this@HomeworksPageFragment.adapter
            layoutManager = LinearLayoutManager(view.context)
            addItemDecoration(SpacingItemDecoration(dimen(R.dimen.cards_spacing)))
            registerForContextMenu(this)
        }

        val isActual = requireArguments().getBoolean(IS_ACTUAL)
        val homeworks = viewModel.run { if (isActual) getActualHomeworks() else getPastHomeworks() }

        binding.flipper.apply {
            val homeworksIndex = 0
            val noHomeworksIndex = 1
            homeworks.observe(viewLifecycleOwner) { value ->
                displayedChild = if (value.isNotEmpty()) homeworksIndex else noHomeworksIndex
            }
        }

        homeworks.observe(viewLifecycleOwner) { adapter.homeworks = it.list }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        requireActivity().menuInflater.inflate(R.menu.context_homeworks, menu)
        @Suppress("UnsafeCast")
        (menuInfo as AdapterView.AdapterContextMenuInfo?)?.run {
            menu.setHeaderTitle(adapter.homeworks[position].subjectName)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        @Suppress("UnsafeCast")
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val homework = adapter.homeworks[info.position]
        return when (item.itemId) {
            R.id.ch_delete -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(R.string.hf_delete_message)
                    .setPositiveButton(R.string.hf_delete_yes) { _, _ ->
                        viewModel.viewModelScope.launch { viewModel.delete(homework) }
                    }
                    .setNegativeButton(R.string.hf_delete_no, null)
                    .show()
                true
            }
            else -> false
        }
    }
}
