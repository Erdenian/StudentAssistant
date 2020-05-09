package ru.erdenian.studentassistant.ui.main.lessoninformation

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.FragmentLessonInformationBinding
import ru.erdenian.studentassistant.ui.adapter.HomeworksListAdapter
import ru.erdenian.studentassistant.ui.adapter.SpacingItemDecoration
import ru.erdenian.studentassistant.utils.getColorCompat
import ru.erdenian.studentassistant.utils.navArgsFactory
import ru.erdenian.studentassistant.utils.setColor

class LessonInformationFragment : Fragment(R.layout.fragment_lesson_information) {

    companion object {
        private const val TIME_FORMAT = "HH:mm"
    }

    private val viewModel by viewModels<LessonInformationViewModel> {
        navArgsFactory<LessonInformationFragmentArgs> { LessonInformationViewModel(it, lesson) }
    }
    private val homeworksAdapter by lazy {
        HomeworksListAdapter().apply {
            onHomeworkClickListener = { findNavController().navigate(LessonInformationFragmentDirections.editHomework(it)) }
            viewModel.homeworks.observe(this@LessonInformationFragment) { homeworks = it.list }
        }
    }

    @Suppress("ComplexMethod")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentLessonInformationBinding.bind(view)
        val owner = viewLifecycleOwner
        setHasOptionsMenu(true)

        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            checkNotNull(supportActionBar).setDisplayHomeAsUpEnabled(true)
        }

        binding.content.subjectName.apply {
            viewModel.lesson.observe(owner) { text = it?.subjectName }
        }

        binding.content.startTime.apply {
            viewModel.lesson.observe(owner) { text = it?.startTime?.toString(TIME_FORMAT) }
        }

        binding.content.endTime.apply {
            viewModel.lesson.observe(owner) { text = it?.endTime?.toString(TIME_FORMAT) }
        }

        binding.content.type.apply {
            viewModel.lesson.observe(owner) { text = it?.type }
        }

        binding.content.homeworksFlipper.apply {
            val containsHomeworksIndex = 0
            val noHomeworksIndex = 1
            viewModel.homeworks.observe(owner) { homeworks ->
                displayedChild = if (homeworks.isEmpty()) noHomeworksIndex else containsHomeworksIndex
            }
        }

        binding.content.homeworks.apply {
            adapter = homeworksAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(SpacingItemDecoration(context.resources.getDimensionPixelSize(R.dimen.cards_spacing)))
            registerForContextMenu(this)
        }

        binding.addHomework.setOnClickListener {
            findNavController().navigate(
                LessonInformationFragmentDirections.createHomework(checkNotNull(viewModel.lesson.value))
            )
        }

        viewModel.lesson.observe(owner) { if (it == null) findNavController().popBackStack() }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_lesson_information, menu)
        menu.setColor(requireContext().getColorCompat(R.color.menu))
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        requireActivity().menuInflater.inflate(R.menu.context_homeworks, menu)
        @Suppress("UnsafeCast")
        (menuInfo as AdapterView.AdapterContextMenuInfo?)?.run {
            menu.setHeaderTitle(homeworksAdapter.homeworks[position].subjectName)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        @Suppress("UnsafeCast")
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val homework = homeworksAdapter.homeworks[info.position]
        return when (item.itemId) {
            R.id.ch_delete -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(R.string.lif_delete_message)
                    .setPositiveButton(R.string.lif_delete_yes) { _, _ -> viewModel.deleteHomework(homework.id) }
                    .setNegativeButton(R.string.lif_delete_no, null)
                    .show()
                true
            }
            else -> false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            findNavController().popBackStack()
            true
        }
        R.id.mli_edit -> {
            findNavController().navigate(LessonInformationFragmentDirections.editLesson(checkNotNull(viewModel.lesson.value)))
            true
        }
        else -> false
    }
}
