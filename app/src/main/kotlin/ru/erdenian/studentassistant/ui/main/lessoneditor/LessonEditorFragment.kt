package ru.erdenian.studentassistant.ui.main.lessoneditor

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Calendar
import kotlinx.coroutines.launch
import org.joda.time.DateTimeConstants
import org.joda.time.format.DateTimeFormat
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.FragmentLessonEditorBinding
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.ui.main.lessoneditor.LessonEditorViewModel.Error
import ru.erdenian.studentassistant.uikit.style.AppTheme
import ru.erdenian.studentassistant.uikit.views.WeeksSelector
import ru.erdenian.studentassistant.utils.distinctUntilChanged
import ru.erdenian.studentassistant.utils.getColorCompat
import ru.erdenian.studentassistant.utils.navArgsFactory
import ru.erdenian.studentassistant.utils.setColor
import ru.erdenian.studentassistant.utils.showTimePicker
import ru.erdenian.studentassistant.utils.toast

class LessonEditorFragment : Fragment(R.layout.fragment_lesson_editor) {

    companion object {
        private const val TIME_FORMAT = "HH:mm"
    }

    private val viewModel by viewModels<LessonEditorViewModel> {
        navArgsFactory<LessonEditorFragmentArgs> { application ->
            when {
                (semesterId >= 0) && (weekday >= 0) -> LessonEditorViewModel(application, semesterId, weekday)
                (semesterId >= 0) && (subjectName != null) -> LessonEditorViewModel(application, semesterId, subjectName)
                (lesson != null) -> LessonEditorViewModel(application, lesson, copy)
                else -> throw IllegalArgumentException("Wrong fragment arguments: $this")
            }
        }
    }

    @Suppress("ComplexMethod", "LongMethod")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentLessonEditorBinding.bind(view)
        setHasOptionsMenu(true)
        val owner = viewLifecycleOwner

        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            checkNotNull(supportActionBar).setDisplayHomeAsUpEnabled(true)
            if (viewModel.isEditing) title = getString(R.string.lef_title_new)
        }

        binding.subjectNameLayout.apply {
            viewModel.error.observe(owner) { error ->
                if (error == Error.EMPTY_SUBJECT_NAME) {
                    this.error = getText(R.string.lef_error_empty_subject_name)
                } else isErrorEnabled = false
            }
        }

        binding.subjectName.apply {
            viewModel.subjectName
                .distinctUntilChanged { it == text?.toString() ?: "" }
                .observe(owner) { setText(it) }
            addTextChangedListener { viewModel.subjectName.value = it?.toString() ?: "" }
            viewModel.existingSubjects.observe(owner) { subjects ->
                setAdapter(ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, subjects.list))
            }
        }

        binding.lessonType.apply {
            viewModel.type
                .distinctUntilChanged { it == text?.toString() ?: "" }
                .observe(owner) { setText(it) }
            addTextChangedListener { viewModel.type.value = it?.toString() ?: "" }
            val predefinedTypes = resources.getStringArray(R.array.lesson_types)
            viewModel.existingTypes.observe(owner) { types ->
                setAdapter(
                    ArrayAdapter(
                        context,
                        android.R.layout.simple_dropdown_item_1line,
                        (predefinedTypes + types.list).distinct()
                    )
                )
            }
        }

        binding.teachers.apply {
            viewModel.teachers
                .distinctUntilChanged { it == text?.toString() ?: "" }
                .observe(owner) { setText(it) }
            addTextChangedListener { viewModel.teachers.value = it?.toString() ?: "" }
            viewModel.existingTeachers.observe(owner) { teachers ->
                setAdapter(ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, teachers.list))
            }
            setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        }

        binding.classrooms.apply {
            viewModel.classrooms
                .distinctUntilChanged { it == text?.toString() ?: "" }
                .observe(owner) { setText(it) }
            addTextChangedListener { viewModel.classrooms.value = it?.toString() ?: "" }
            viewModel.existingClassrooms.observe(owner) { classrooms ->
                setAdapter(ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, classrooms.list))
            }
            setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        }

        val timeFormatter = DateTimeFormat.forPattern(TIME_FORMAT)

        binding.startTime.apply {
            viewModel.startTime.observe(owner) { text = it.toString(timeFormatter) }
            setOnClickListener { requireContext().showTimePicker(viewModel.startTime.value, viewModel.startTime::setValue) }
        }

        binding.endTime.apply {
            viewModel.endTime.observe(owner) { text = it.toString(timeFormatter) }
            setOnClickListener { requireContext().showTimePicker(viewModel.endTime.value, viewModel.endTime::setValue) }
        }

        binding.repeatType.apply {
            val byWeekdayIndex = 0
            val byDatesIndex = 1
            viewModel.lessonRepeat.distinctUntilChanged { value ->
                value == when (selectedItemPosition) {
                    byWeekdayIndex -> Lesson.Repeat.ByWeekday::class
                    byDatesIndex -> Lesson.Repeat.ByDates::class
                    else -> error("Неизвестный тип повторения")
                }
            }.observe(owner) { lessonRepeat ->
                setSelection(
                    when (lessonRepeat) {
                        Lesson.Repeat.ByWeekday::class -> byWeekdayIndex
                        Lesson.Repeat.ByDates::class -> byDatesIndex
                        else -> error("Неизвестный тип повторений: $lessonRepeat")
                    }
                )
            }
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    viewModel.lessonRepeat.value = when (position) {
                        byWeekdayIndex -> Lesson.Repeat.ByWeekday::class
                        byDatesIndex -> Lesson.Repeat.ByDates::class
                        else -> error("Неизвестный тип повторения")
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) = Unit
            }
        }

        binding.repeatTypeFlipper.apply {
            val byWeekdayIndex = 0
            val byDatesIndex = 1

            viewModel.lessonRepeat.observe(owner) { lessonRepeat ->
                displayedChild = when (lessonRepeat) {
                    Lesson.Repeat.ByWeekday::class -> byWeekdayIndex
                    Lesson.Repeat.ByDates::class -> byDatesIndex
                    else -> error("Неизвестный тип повторений: $lessonRepeat")
                }
            }
        }

        binding.weekday.apply {
            setSelectOnlyOne(true)
            val jodaToCalendar = mapOf(
                DateTimeConstants.MONDAY to Calendar.MONDAY,
                DateTimeConstants.TUESDAY to Calendar.TUESDAY,
                DateTimeConstants.WEDNESDAY to Calendar.WEDNESDAY,
                DateTimeConstants.THURSDAY to Calendar.THURSDAY,
                DateTimeConstants.FRIDAY to Calendar.FRIDAY,
                DateTimeConstants.SATURDAY to Calendar.SATURDAY,
                DateTimeConstants.SUNDAY to Calendar.SUNDAY
            )

            viewModel.weekday
                .distinctUntilChanged { jodaToCalendar[it] == selectedDays.single() }
                .observe(owner) { selectDay(checkNotNull(jodaToCalendar[it])) }
            setOnWeekdaysChangeListener { _, _, weekdays ->
                if (weekdays.isNotEmpty()) {
                    viewModel.weekday.value = checkNotNull(jodaToCalendar.entries.find { it.value == weekdays.single() }?.key)
                } else {
                    selectDay(checkNotNull(jodaToCalendar[viewModel.weekday.value]))
                }
            }
        }

        binding.weeksSelector.apply {
            setContent {
                @Composable
                fun <T : Any> LiveData<T>.observeAsStateNonNull(): State<T> = observeAsState(checkNotNull(value))

                val weeks by viewModel.weeks.observeAsStateNonNull()
                AppTheme {
                    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.onBackground) {
                        WeeksSelector(
                            weeks = weeks,
                            onWeeksChange = { viewModel.weeks.value = it },
                            modifier = Modifier.background(MaterialTheme.colors.background)
                        )
                    }
                }
            }
        }

        viewModel.done.observe(owner) { if (it) findNavController().popBackStack() }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_lesson_editor, menu)
        menu.findItem(R.id.mle_delete).isVisible = viewModel.isEditing
        menu.setColor(requireContext().getColorCompat(R.color.menu))
    }

    @Suppress("ComplexMethod", "LongMethod")
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            findNavController().popBackStack()
            true
        }
        R.id.mle_save -> {
            viewModel.error.value?.let { error ->
                requireContext().toast(
                    when (error) {
                        Error.EMPTY_SUBJECT_NAME -> R.string.lef_error_empty_subject_name
                        Error.WRONG_TIMES -> R.string.lef_error_wrong_time
                        Error.EMPTY_REPEAT -> R.string.lef_error_empty_repeat
                    }
                )
            } ?: run {
                lifecycleScope.launch {
                    if (viewModel.isSubjectNameChangedAndNotLast()) {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(R.string.lef_rename_others_title)
                            .setMessage(R.string.lef_rename_others_message)
                            .setPositiveButton(R.string.lef_rename_others_yes) { _, _ -> viewModel.save(true) }
                            .setNegativeButton(R.string.lef_rename_others_no) { _, _ -> viewModel.save(false) }
                            .setNeutralButton(R.string.lef_rename_others_cancel, null)
                            .show()
                    } else viewModel.save()
                }
            }
            true
        }
        R.id.mle_delete -> {
            lifecycleScope.launch {
                if (viewModel.isLastLessonOfSubjectsAndHasHomeworks()) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.lef_delete_homeworks_title)
                        .setMessage(R.string.lef_delete_homeworks_message)
                        .setPositiveButton(R.string.lef_delete_homeworks_yes) { _, _ -> viewModel.delete(true) }
                        .setNegativeButton(R.string.lef_delete_homeworks_no) { _, _ -> viewModel.delete(false) }
                        .setNeutralButton(R.string.lef_delete_homeworks_cancel, null)
                        .show()
                } else {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(R.string.lef_delete_message)
                        .setPositiveButton(R.string.lef_delete_yes) { _, _ -> viewModel.delete() }
                        .setNegativeButton(R.string.lef_delete_no, null)
                        .show()
                }
            }
            true
        }
        else -> false
    }
}
