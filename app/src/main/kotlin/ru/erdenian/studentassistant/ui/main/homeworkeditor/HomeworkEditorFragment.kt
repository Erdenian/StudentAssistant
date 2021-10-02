package ru.erdenian.studentassistant.ui.main.homeworkeditor

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.FragmentHomeworkEditorBinding
import ru.erdenian.studentassistant.ui.main.homeworkeditor.HomeworkEditorViewModel.Error
import ru.erdenian.studentassistant.uikit.views.ExposedDropdownMenu
import ru.erdenian.studentassistant.utils.distinctUntilChanged
import ru.erdenian.studentassistant.utils.getColorCompat
import ru.erdenian.studentassistant.utils.navArgsFactory
import ru.erdenian.studentassistant.utils.setColor
import ru.erdenian.studentassistant.utils.showDatePicker
import ru.erdenian.studentassistant.utils.toast

class HomeworkEditorFragment : Fragment(R.layout.fragment_homework_editor) {

    companion object {
        @Deprecated("")
        private const val DATE_FORMAT = "dd.MM.yyyy"
    }

    private val viewModel by viewModels<HomeworkEditorViewModel> {
        navArgsFactory<HomeworkEditorFragmentArgs> { application ->
            when {
                (semesterId >= 0) -> HomeworkEditorViewModel(application, semesterId)
                (lesson != null) -> HomeworkEditorViewModel(application, lesson)
                (homework != null) -> HomeworkEditorViewModel(application, homework)
                else -> throw IllegalArgumentException("Wrong fragment arguments: $this")
            }
        }
    }

    private val backObserver = Observer<Boolean> { if (it) findNavController().popBackStack() }

    @Suppress("ComplexMethod")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentHomeworkEditorBinding.bind(view)
        val owner = viewLifecycleOwner
        setHasOptionsMenu(true)

        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            checkNotNull(supportActionBar).setDisplayHomeAsUpEnabled(true)
        }

        binding.subjectName.apply {
            setAdapter(
                ExposedDropdownMenu.createAdapter(context).apply {
                    viewModel.existingSubjects.observe(owner) { items = it.list }
                }
            )

            viewModel.subjectName
                .distinctUntilChanged { it == text?.toString() ?: "" }
                .observe(owner) { text = it }
            onTextChangedListener = { text, _ -> viewModel.subjectName.value = text }
        }

        binding.description.apply {
            viewModel.description
                .distinctUntilChanged { it == text?.toString() ?: "" }
                .observe(owner) { setText(it) }
            addTextChangedListener { viewModel.description.value = it?.toString() ?: "" }
        }

        binding.deadline.apply {
            val dateFormatter = DateTimeFormat.forPattern(DATE_FORMAT)
            viewModel.deadline.observe(owner) { text = it.toString(dateFormatter) }
            setOnClickListener {
                requireContext().showDatePicker(
                    viewModel.deadline.value,
                    LocalDate.now(),
                    viewModel.semesterLastDay.value
                ) { viewModel.deadline.value = it }
            }
        }

        viewModel.error.observe(owner) {}
        viewModel.done.observe(owner, backObserver)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_homework_editor, menu)
        menu.findItem(R.id.mhe_delete).isVisible = viewModel.isEditing
        menu.setColor(requireContext().getColorCompat(R.color.menu))
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            findNavController().popBackStack()
            true
        }
        R.id.mhe_save -> {
            viewModel.error.value?.let { error ->
                requireContext().toast(
                    when (error) {
                        Error.EMPTY_SUBJECT -> R.string.hef_error_empty_subject_name
                        Error.EMPTY_DESCRIPTION -> R.string.hef_error_empty_description
                    }
                )
            } ?: run {
                if (viewModel.lessonExists) {
                    viewModel.save()
                } else {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.hef_unknown_lesson)
                        .setMessage(R.string.hef_unknown_lesson_message)
                        .setPositiveButton(R.string.hef_unknown_lesson_yes) { _, _ -> viewModel.save() }
                        .setNegativeButton(R.string.hef_unknown_lesson_no, null)
                        .setNeutralButton(R.string.hef_unknown_lesson_yes_and_create) { _, _ ->
                            viewModel.run {
                                done.removeObserver(backObserver)
                                save()
                                findNavController().navigate(
                                    HomeworkEditorFragmentDirections.addLesson(semesterId, checkNotNull(subjectName.value))
                                )
                            }
                        }
                        .show()
                }
            }
            true
        }
        R.id.mhe_delete -> {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.hef_delete_message)
                .setPositiveButton(R.string.hef_delete_yes) { _, _ -> viewModel.delete() }
                .setNegativeButton(R.string.hef_delete_no, null)
                .show()
            true
        }
        else -> false
    }
}
