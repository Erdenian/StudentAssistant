package ru.erdenian.studentassistant.ui.lessoneditor

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.MultiAutoCompleteTextView
import android.widget.Spinner
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.observe
import androidx.lifecycle.viewModelScope
import com.dpro.widgets.WeekdaysPicker
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.joda.time.DateTimeConstants
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.customviews.WeeksSelector
import ru.erdenian.studentassistant.repository.entity.Lesson
import ru.erdenian.studentassistant.repository.entity.LessonRepeat
import ru.erdenian.studentassistant.ui.lessoneditor.LessonEditorViewModel.Error
import ru.erdenian.studentassistant.utils.distinctUntilChanged
import ru.erdenian.studentassistant.utils.getCompatColor
import ru.erdenian.studentassistant.utils.lazyViewModel
import ru.erdenian.studentassistant.utils.setColor
import ru.erdenian.studentassistant.utils.showTimePicker
import java.util.Calendar

class LessonEditorActivity : AppCompatActivity() {

    companion object {
        private const val SEMESTER_ID_INTENT_KEY = "semester_id_intent_key"
        private const val WEEKDAY_INTENT_KEY = "weekday_intent_key"
        private const val LESSON_INTENT_KEY = "lesson_intent_key"
        private const val COPY_INTENT_KEY = "copy_intent_key"

        fun start(context: Context, semesterId: Long, weekday: Int) {
            context.startActivity<LessonEditorActivity>(
                SEMESTER_ID_INTENT_KEY to semesterId,
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

    private val viewModel by lazyViewModel<LessonEditorViewModel>()

    private val lesson by lazy { intent.getParcelableExtra<Lesson?>(LESSON_INTENT_KEY) }

    @Suppress("ComplexMethod", "LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_editor)

        intent.apply {
            val l = lesson
            val semesterId = getLongExtra(SEMESTER_ID_INTENT_KEY, -1)
            if (l == null) viewModel.init(
                semesterId,
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

        findViewById<TextInputLayout>(R.id.ale_subject_name).apply {
            viewModel.error.observe(owner) { error ->
                if (error == Error.EMPTY_SUBJECT_NAME) {
                    this.error = getText(
                        R.string.lea_error_empty_subject_name
                    )
                } else isErrorEnabled = false
            }
        }

        findViewById<AutoCompleteTextView>(R.id.ale_subject_name_edit_text).apply {
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

        findViewById<AutoCompleteTextView>(R.id.ale_lesson_type_edit_text).apply {
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
                        predefinedTypes + types.list
                    )
                )
            }
        }

        findViewById<MultiAutoCompleteTextView>(R.id.ale_teachers_edit_text).apply {
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

        findViewById<MultiAutoCompleteTextView>(R.id.ale_classrooms_edit_text).apply {
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

        findViewById<Button>(R.id.ale_start_time).apply {
            viewModel.startTime.observe(owner) { text = it.toString(TIME_FORMAT) }
            setOnClickListener {
                showTimePicker(viewModel.startTime.value, viewModel.startTime::setValue)
            }
        }

        findViewById<Button>(R.id.ale_end_time).apply {
            viewModel.endTime.observe(owner) { text = it.toString(TIME_FORMAT) }
            setOnClickListener {
                showTimePicker(viewModel.endTime.value, viewModel.endTime::setValue)
            }
        }

        findViewById<Spinner>(R.id.ale_repeat_type).apply {
            val byWeekdayIndex = 0
            val byDatesIndex = 1
            viewModel.lessonRepeat.distinctUntilChanged { value ->
                value == when (selectedItemPosition) {
                    byWeekdayIndex -> LessonRepeat.ByWeekday::class
                    byDatesIndex -> LessonRepeat.ByDates::class
                    else -> throw IllegalStateException("Неизвестный тип повторения")
                }
            }.observe(owner) { lessonRepeat ->
                setSelection(
                    when (lessonRepeat) {
                        LessonRepeat.ByWeekday::class -> byWeekdayIndex
                        LessonRepeat.ByDates::class -> byDatesIndex
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
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    viewModel.lessonRepeat.value = when (position) {
                        byWeekdayIndex -> LessonRepeat.ByWeekday::class
                        byDatesIndex -> LessonRepeat.ByDates::class
                        else -> throw IllegalStateException("Неизвестный тип повторения")
                    }
                }
            }
        }

        findViewById<ViewFlipper>(R.id.ale_repeat_type_flipper).apply {
            val byWeekdayIndex = 0
            val byDatesIndex = 1

            viewModel.lessonRepeat.observe(owner) { lessonRepeat ->
                displayedChild = when (lessonRepeat) {
                    LessonRepeat.ByWeekday::class -> byWeekdayIndex
                    LessonRepeat.ByDates::class -> byDatesIndex
                    else -> throw IllegalStateException(
                        "Неизвестный тип повторений: $lessonRepeat"
                    )
                }
            }
        }

        findViewById<WeekdaysPicker>(R.id.ale_weekday).apply {
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
                viewModel.weekday.value = checkNotNull(
                    isoToUs.entries.find { it.value == weekdays.single() }?.key
                )
            }
        }

        findViewById<WeeksSelector>(R.id.ale_weeks_selector).apply {
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
        menu.findItem(R.id.menu_lesson_editor_delete_lesson).isVisible = (lesson != null)
        menu.setColor(getCompatColor(R.color.action_bar_icons_color))
        return true
    }

    @Suppress("ComplexMethod", "LongMethod")
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.menu_lesson_editor_save -> {
            viewModel.error.value?.let { error ->
                toast(
                    when (error) {
                        Error.EMPTY_SUBJECT_NAME -> {
                            R.string.lea_error_empty_subject_name
                        }
                        Error.WRONG_TIMES -> {
                            R.string.lea_error_wrong_time
                        }
                    }
                )
            } ?: run {
                viewModel.viewModelScope.launch {
                    if (viewModel.isSubjectNameChangedAndNotLast()) {
                        alert(
                            R.string.lea_rename_others_message,
                            R.string.lea_rename_others_title
                        ) {
                            positiveButton(R.string.lea_rename_others_yes) {
                                viewModel.viewModelScope.launch {
                                    viewModel.save(true)
                                    finish()
                                }
                            }
                            negativeButton(R.string.lea_rename_others_no) {
                                viewModel.viewModelScope.launch {
                                    viewModel.save(false)
                                    finish()
                                }
                            }
                            neutralPressed(R.string.lea_rename_others_cancel) {}
                        }.show()
                    } else {
                        viewModel.save()
                        finish()
                    }
                }
            }
            true
        }
        R.id.menu_lesson_editor_delete_lesson -> {
            viewModel.viewModelScope.launch {
                if (viewModel.isLastLessonOfSubjectsAndHasHomeworks()) {
                    alert(
                        R.string.lea_delete_homeworks_message,
                        R.string.lea_delete_homeworks_title
                    ) {
                        positiveButton(R.string.lea_delete_homeworks_yes) {
                            viewModel.viewModelScope.launch {
                                viewModel.delete()
                                finish()
                            }
                        }
                        negativeButton(R.string.lea_delete_homeworks_cancel) {}
                    }.show()
                } else {
                    alert(R.string.lea_delete_message) {
                        positiveButton(R.string.lea_delete_yes) {
                            viewModel.viewModelScope.launch {
                                viewModel.delete()
                                finish()
                            }
                        }
                        negativeButton(R.string.lea_delete_no) {}
                    }.show()
                }
            }
            true
        }
        else -> false
    }
}
