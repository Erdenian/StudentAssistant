package ru.erdenian.studentassistant.ui.main.homeworks

import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.FragmentHomeworksBinding
import ru.erdenian.studentassistant.ui.adapter.HomeworksListAdapter
import ru.erdenian.studentassistant.ui.adapter.SemestersSpinnerAdapter
import ru.erdenian.studentassistant.ui.adapter.SpacingItemDecoration
import ru.erdenian.studentassistant.utils.getColorCompat
import ru.erdenian.studentassistant.utils.setColor

class HomeworksFragment : Fragment(R.layout.fragment_homeworks) {

    private val viewModel by viewModels<HomeworksViewModel>()

    private val adapter = HomeworksListAdapter().apply {
        onHomeworkClickListener = { findNavController().navigate(HomeworksFragmentDirections.editHomework(it)) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentHomeworksBinding.bind(view)
        val owner = viewLifecycleOwner
        setHasOptionsMenu(true)

        viewModel.selectedSemester.observe(owner) { requireActivity().invalidateOptionsMenu() }

        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            findNavController().addOnDestinationChangedListener { _, destination, _ -> title = destination.label }
            viewModel.allSemesters.observe(owner) { checkNotNull(supportActionBar).setDisplayShowTitleEnabled(it.size <= 1) }
        }

        binding.semesters.apply {
            viewModel.allSemesters.observe(owner) { visibility = if (it.size > 1) View.VISIBLE else View.GONE }
            val adapter = SemestersSpinnerAdapter().apply { viewModel.allSemesters.observe(owner) { semesters = it.list } }
            this.adapter = adapter
            viewModel.selectedSemester.distinctUntilChanged().observe(owner) { semester ->
                viewModel.allSemesters.value?.indexOf(semester)?.takeIf { it >= 0 }?.let { setSelection(it) }
            }
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) =
                    viewModel.selectSemester(adapter.getItem(position))

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        }

        binding.flipper.apply {
            val hasHomeworksIndex = 0
            val noScheduleIndex = 1
            val hoHomeworksIndex = 2
            viewModel.state.observe(owner) { state: HomeworksViewModel.State ->
                displayedChild = when (state) {
                    HomeworksViewModel.State.NO_SCHEDULE -> noScheduleIndex
                    HomeworksViewModel.State.NO_HOMEWORKS -> hoHomeworksIndex
                    HomeworksViewModel.State.HAS_HOMEWORKS -> hasHomeworksIndex
                }
            }
        }

        binding.homeworks.apply {
            adapter = this@HomeworksFragment.adapter.apply {
                viewModel.actual.observe(viewLifecycleOwner) { homeworks = it.list }
            }
            layoutManager = LinearLayoutManager(view.context)
            addItemDecoration(SpacingItemDecoration(context.resources.getDimensionPixelSize(R.dimen.cards_spacing)))
            registerForContextMenu(this)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        requireActivity().menuInflater.inflate(R.menu.context_homeworks, menu)
        @Suppress("UnsafeCast")
        (menuInfo as AdapterView.AdapterContextMenuInfo).run {
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
                    .setPositiveButton(R.string.hf_delete_yes) { _, _ -> viewModel.deleteHomework(homework.id) }
                    .setNegativeButton(R.string.hf_delete_no, null)
                    .show()
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_homeworks, menu)
        menu.setColor(requireContext().getColorCompat(R.color.menu))
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val hasSchedule = (viewModel.selectedSemester.value != null)
        menu.findItem(R.id.mh_add_homework).isVisible = hasSchedule
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.mh_add_homework -> {
            findNavController().navigate(
                HomeworksFragmentDirections.createHomework(checkNotNull(viewModel.selectedSemester.value).id)
            )
            true
        }
        else -> false
    }
}
