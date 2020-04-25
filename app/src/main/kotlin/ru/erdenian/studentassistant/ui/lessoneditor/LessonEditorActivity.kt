package ru.erdenian.studentassistant.ui.lessoneditor

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.viewModelScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.joda.time.DateTimeConstants
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.ActivityLessonEditorBinding
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.ui.lessoneditor.LessonEditorViewModel.Error
import ru.erdenian.studentassistant.uikit.WeeksSelector
import ru.erdenian.studentassistant.utils.distinctUntilChanged
import ru.erdenian.studentassistant.utils.getColorCompat
import ru.erdenian.studentassistant.utils.setColor
import ru.erdenian.studentassistant.utils.showTimePicker
import ru.erdenian.studentassistant.utils.startActivity
import ru.erdenian.studentassistant.utils.toast
import java.util.Calendar

class LessonEditorActivity : AppCompatActivity() {

    companion object {
        private const val SEMESTER_ID_INTENT_KEY = "semester_id_intent_key"
        private const val START_TIME_INTENT_KEY = "start_time_intent_key"
        private const val WEEKDAY_INTENT_KEY = "weekday_intent_key"
        private const val LESSON_INTENT_KEY = "lesson_intent_key"
        private const val COPY_INTENT_KEY = "copy_intent_key"

        fun start(
            context: Context,
            semesterId: Long,
            startTime: LocalTime = LocalTime(9, 0),
            weekday: Int = DateTimeConstants.MONDAY
        ) {
            context.startActivity<LessonEditorActivity>(
                SEMESTER_ID_INTENT_KEY to semesterId,
                START_TIME_INTENT_KEY to startTime,
                WEEKDAY_INTENT_KEY to weekday
            )
        }

        fun start(context: Context, lesson: Lesson, copy: Boolean = false) {
            context.startActivity<LessonEditorActivity>(
                SEMESTER_ID_INTENT_KEY to lesson.semesterId,
                LESSON_INTENT_KEY to lesson,
                COPY_INTENT_KEY to copy
            )
        }

        private const val TIME_FORMAT = "HH:mm"
    }

    private val viewModel by viewModels<LessonEditorViewModel>()

    private val lesson by lazy { intent.getParcelableExtra<Lesson?>(LESSON_INTENT_KEY) }

    @Suppress("ComplexMethod", "LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLessonEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.apply {
            val l = lesson
            val semesterId = getLongExtra(SEMESTER_ID_INTENT_KEY, -1)
            @Suppress("UnsafeCast")
            if (l == null) viewModel.init(
                semesterId,
                getSerializableExtra(START_TIME_INTENT_KEY) as LocalTime,
                getIntExtra(WEEKDAY_INTENT_KEY, DateTimeConstants.MONDAY)
            ) else viewModel.init(
                semesterId,
                l,
                intent.getBooleanExtra(COPY_INTENT_KEY, false)
            )
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            if (lesson == null) {
                title = getString(R.string.lea_title_new)
            }
        }

        val owner = this

        binding.subjectNameLayout.apply {
            viewModel.error.observe(owner) { error ->
                if (error == Error.EMPTY_SUBJECT_NAME) {
                    this.error = getText(
                        R.string.lea_error_empty_subject_name
                    )
                } else isErrorEnabled = false
            }
        }

        binding.subjectName.apply {
            viewModel.subjectName.distinctUntilChanged { value ->
                value == text?.toString() ?: ""
            }.observe(owner) { setText(it) }
            addTextChangedListener { editable ->
                viewModel.subjectName.value = editable?.toString() ?: ""
            }
            viewModel.existingSubjects.observe(owner) { subjects ->
                setAdapter(
                    ArrayAdapter(
                        context,
                        android.R.layout.simple_dropdown_item_1line,
                        subjects.list
                    )
                )
            }
        }

        binding.lessonType.apply {
            viewModel.type.distinctUntilChanged { value ->
                value == text?.toString() ?: ""
            }.observe(owner) { setText(it) }
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
            viewModel.teachers.distinctUntilChanged { value ->
                value == text?.toString() ?: ""
            }.observe(owner) { setText(it) }
            addTextChangedListener { viewModel.teachers.value = it?.toString() ?: "" }
            viewModel.existingTeachers.observe(owner) { teachers ->
                setAdapter(
                    ArrayAdapter(
                        context,
                        android.R.layout.simple_dropdown_item_1line,
                        teachers.list
                    )
                )
            }
            setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        }

        binding.classrooms.apply {
            viewModel.classrooms.distinctUntilChanged { value ->
                value == text?.toString() ?: ""
            }.observe(owner) { setText(it) }
            addTextChangedListener { viewModel.classrooms.value = it?.toString() ?: "" }
            viewModel.existingClassrooms.observe(owner) { classrooms ->
                setAdapter(
                    ArrayAdapter(
                        context,
                        android.R.layout.simple_dropdown_item_1line,
                        classrooms.list
                    )
                )
            }
            setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        }

        binding.startTime.apply {
            viewModel.startTime.observe(owner) { text = it.toString(TIME_FORMAT) }
            setOnClickListener {
                showTimePicker(viewModel.startTime.value, viewModel.startTime::setValue)
            }
        }

        binding.endTime.apply {
            viewModel.endTime.observe(owner) { text = it.toString(TIME_FORMAT) }
            setOnClickListener {
                showTimePicker(viewModel.endTime.value, viewModel.endTime::setValue)
            }
        }

        binding.repeatType.apply {
            val byWeekdayIndex = 0
            val byDatesIndex = 1
            viewModel.lessonRepeat.distinctUntilChanged { value ->
                value == when (selectedItemPosition) {
                    byWeekdayIndex -> Lesson.Repeat.ByWeekday::class
                    byDatesIndex -> Lesson.Repeat.ByDates::class
                    else -> throw IllegalStateException("Неизвестный тип повторения")
                }
            }.observe(owner) { lessonRepeat ->
                setSelection(
                    when (lessonRepeat) {
                        Lesson.Repeat.ByWeekday::class -> byWeekdayIndex
                        Lesson.Repeat.ByDates::class -> byDatesIndex
                        else -> throw IllegalStateException(
                            "Неизвестный тип повторений: $lessonRepeat"
                        )
                    }
                )
            }
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.lessonRepeat.value = when (position) {
                        byWeekdayIndex -> Lesson.Repeat.ByWeekday::class
                        byDatesIndex -> Lesson.Repeat.ByDates::class
                        else -> throw IllegalStateException("Неизвестный тип повторения")
                    }
                }
            }
        }

        binding.repeatTypeFlipper.apply {
            val byWeekdayIndex = 0
            val byDatesIndex = 1

            viewModel.lessonRepeat.observe(owner) { lessonRepeat ->
                displayedChild = when (lessonRepeat) {
                    Lesson.Repeat.ByWeekday::class -> byWeekdayIndex
                    Lesson.Repeat.ByDates::class -> byDatesIndex
                    else -> throw IllegalStateException(
                        "Неизвестный тип повторений: $lessonRepeat"
                    )
                }
            }
        }

        binding.weekday.apply {
            setSelectOnlyOne(true)
            val isoToUs = mapOf(
                DateTimeConstants.MONDAY to Calendar.MONDAY,
                DateTimeConstants.TUESDAY to Calendar.TUESDAY,
                DateTimeConstants.WEDNESDAY to Calendar.WEDNESDAY,
                DateTimeConstants.THURSDAY to Calendar.THURSDAY,
                DateTimeConstants.FRIDAY to Calendar.FRIDAY,
                DateTimeConstants.SATURDAY to Calendar.SATURDAY,
                DateTimeConstants.SUNDAY to Calendar.SUNDAY
            )

            viewModel.weekday.distinctUntilChanged { value ->
                value == selectedDays.single()
            }.observe(owner) { weekday ->
                selectDay(checkNotNull(isoToUs[weekday]))
            }
            setOnWeekdaysChangeListener { _, _, weekdays ->
                if (weekdays.isNotEmpty()) {
                    viewModel.weekday.value = checkNotNull(
                        isoToUs.entries.find { it.value == weekdays.single() }?.key
                    )
                } else {
                    selectDay(checkNotNull(isoToUs[viewModel.weekday.value]))
                }
            }
        }

        binding.weeksSelector.apply {
            viewModel.weeks.distinctUntilChanged { value ->
                value == weeks
            }.observe(owner) { weeks = it }
            onWeeksChangeListener = object : WeeksSelector.OnWeeksChangeListener {
                override fun onWeeksChange(weeks: List<Boolean>) {
                    viewModel.weeks.value = weeks
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_lesson_editor, menu)
        menu.findItem(R.id.mle_delete).isVisible = (lesson != null)
        menu.setColor(getColorCompat(R.color.menu))
        return true
    }

    @Suppress("ComplexMethod", "LongMethod")
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.mle_save -> {
            viewModel.error.value?.let { error ->
                toast(
                    when (error) {
                        Error.EMPTY_SUBJECT_NAME -> R.string.lea_error_empty_subject_name
                        Error.WRONG_TIMES -> R.string.lea_error_wrong_time
                        Error.EMPTY_REPEAT -> R.string.lea_error_empty_repeat
                    }
                )
            } ?: run {
                viewModel.viewModelScope.launch {
                    if (viewModel.isSubjectNameChangedAndNotLast()) {
                        MaterialAlertDialogBuilder(this@LessonEditorActivity)
                            .setTitle(R.string.lea_rename_others_title)
                            .setMessage(R.string.lea_rename_others_message)
                            .setPositiveButton(R.string.lea_rename_others_yes) { _, _ ->
                                viewModel.viewModelScope.launch {
                                    viewModel.save(true)
                                    finish()
                                }
                            }
                            .setNegativeButton(R.string.lea_rename_others_no) { _, _ ->
                                viewModel.viewModelScope.launch {
                                    viewModel.save(false)
                                    finish()
                                }
                            }
                            .setNeutralButton(R.string.lea_rename_others_cancel, null)
                            .show()
                    } else {
                        viewModel.save()
                        finish()
                    }
                }
            }
            true
        }
        R.id.mle_delete -> {
            viewModel.viewModelScope.launch {
                if (viewModel.isLastLessonOfSubjectsAndHasHomeworks()) {
                    MaterialAlertDialogBuilder(this@LessonEditorActivity)
                        .setTitle(R.string.lea_delete_homeworks_title)
                        .setMessage(R.string.lea_delete_homeworks_message)
                        .setPositiveButton(R.string.lea_delete_homeworks_yes) { _, _ ->
                            viewModel.viewModelScope.launch {
                                viewModel.delete()
                                finish()
                            }
                        }
                        .setNegativeButton(R.string.lea_delete_homeworks_cancel, null)
                        .show()
                } else {
                    MaterialAlertDialogBuilder(this@LessonEditorActivity)
                        .setMessage(R.string.lea_delete_message)
                        .setPositiveButton(R.string.lea_delete_yes) { _, _ ->
                            viewModel.viewModelScope.launch {
                                viewModel.delete()
                                finish()
                            }
                        }
                        .setNegativeButton(R.string.lea_delete_no, null)
                        .show()
                }
            }
            true
        }
        else -> false
    }
}
