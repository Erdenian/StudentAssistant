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
import ru.erdenian.studentassistant.extensions.compareAndSet
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.lazyViewModel
import ru.erdenian.studentassistant.extensions.setColor
import ru.erdenian.studentassistant.extensions.showTimePicker
import ru.erdenian.studentassistant.extensions.toSingleLine
import ru.erdenian.studentassistant.repository.entity.LessonNew
import ru.erdenian.studentassistant.repository.entity.LessonRepeatNew
import ru.erdenian.studentassistant.repository.toImmutableSortedSet
import ru.erdenian.studentassistant.ui.lessoneditor.LessonEditorViewModel.Error
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

        fun start(context: Context, lesson: LessonNew, copy: Boolean = false) {
            context.startActivity<LessonEditorActivity>(
                SEMESTER_ID_INTENT_KEY to lesson.semesterId,
                LESSON_INTENT_KEY to lesson,
                COPY_INTENT_KEY to copy
            )
        }

        private const val TIME_FORMAT = "HH:mm"
    }

    private val viewModel by lazyViewModel<LessonEditorViewModel>()

    private val lesson by lazy { intent.getParcelableExtra<LessonNew?>(LESSON_INTENT_KEY) }

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
                title = getString(R.string.title_activity_lesson_editor_new_lesson)
            }
        }

        findViewById<TextInputLayout>(R.id.ale_subject_name).apply {
            viewModel.error.observe(this@LessonEditorActivity) { error ->
                if (error == Error.EMPTY_SUBJECT_NAME) {
                    this.error = getText(
                        R.string.activity_lesson_editor_incorrect_subject_name_message
                    )
                } else isErrorEnabled = false
            }
        }

        findViewById<AutoCompleteTextView>(R.id.ale_subject_name_edit_text).apply {
            viewModel.subjectName.observe(this@LessonEditorActivity) { setText(it) }
            addTextChangedListener { editable ->
                viewModel.subjectName.compareAndSet(editable?.toString() ?: "")
            }
            viewModel.existingSubjects.observe(this@LessonEditorActivity) { subjects ->
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
            viewModel.type.observe(this@LessonEditorActivity) { setText(it) }
            addTextChangedListener { editable ->
                viewModel.type.compareAndSet(editable?.toString() ?: "")
            }
            viewModel.existingTypes.observe(this@LessonEditorActivity) { types ->
                setAdapter(
                    ArrayAdapter(
                        context,
                        android.R.layout.simple_dropdown_item_1line,
                        types.list
                    )
                )
            }
        }

        findViewById<MultiAutoCompleteTextView>(R.id.ale_teachers_edit_text).apply {
            viewModel.teachers.observe(this@LessonEditorActivity) { setText(it.joinToString()) }
            addTextChangedListener { editable ->
                viewModel.teachers.compareAndSet(
                    (editable?.toString() ?: "")
                        .toSingleLine()
                        .split(',')
                        .asSequence()
                        .map(String::trim)
                        .filter(String::isNotBlank)
                        .toImmutableSortedSet()
                )
            }
            viewModel.existingTeachers.observe(this@LessonEditorActivity) { teachers ->
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
            viewModel.classrooms.observe(this@LessonEditorActivity) { setText(it.joinToString()) }
            addTextChangedListener { editable ->
                viewModel.classrooms.compareAndSet(
                    (editable?.toString() ?: "")
                        .toSingleLine()
                        .split(',')
                        .asSequence()
                        .map(String::trim)
                        .filter(String::isNotBlank)
                        .toImmutableSortedSet()
                )
            }
            viewModel.existingClassrooms.observe(this@LessonEditorActivity) { classrooms ->
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
            viewModel.startTime.observe(this@LessonEditorActivity) { startTime ->
                text = startTime.toString(TIME_FORMAT)
            }
            setOnClickListener {
                showTimePicker(viewModel.startTime.value, viewModel.startTime::compareAndSet)
            }
        }

        findViewById<Button>(R.id.ale_end_time).apply {
            viewModel.endTime.observe(this@LessonEditorActivity) { endTime ->
                text = endTime.toString(TIME_FORMAT)
            }
            setOnClickListener {
                showTimePicker(viewModel.endTime.value, viewModel.endTime::compareAndSet)
            }
        }

        findViewById<Spinner>(R.id.ale_repeat_type).apply {
            val byWeekdayIndex = 0
            val byDatesIndex = 1

            adapter = ArrayAdapter(
                context,
                android.R.layout.simple_dropdown_item_1line,
                resources.getStringArray(R.array.lesson_repeat_types)
            )
            viewModel.lessonRepeat.observe(this@LessonEditorActivity) { lessonRepeat ->
                setSelection(
                    when (lessonRepeat.value) {
                        is LessonRepeatNew.ByWeekday -> byWeekdayIndex
                        is LessonRepeatNew.ByDates -> byDatesIndex
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
                    viewModel.lessonRepeat.compareAndSet(
                        when (position) {
                            byWeekdayIndex -> viewModel.byWeekday
                            byDatesIndex -> viewModel.byDates
                            else -> throw IllegalStateException("Неизвестный тип повторения")
                        }
                    )
                }
            }
        }

        findViewById<ViewFlipper>(R.id.ale_repeat_type_flipper).apply {
            val byWeekdayIndex = 0
            val byDatesIndex = 1

            viewModel.lessonRepeat.observe(this@LessonEditorActivity) { lessonRepeat ->
                displayedChild = when (lessonRepeat.value) {
                    is LessonRepeatNew.ByWeekday -> byWeekdayIndex
                    is LessonRepeatNew.ByDates -> byDatesIndex
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

            viewModel.byWeekday.observe(this@LessonEditorActivity) { byWeekday ->
                selectDay(checkNotNull(isoToUs[byWeekday.weekday]))
            }
            setOnWeekdaysChangeListener { _, _, weekdays ->
                val newWeekday = checkNotNull(
                    isoToUs.entries.find { it.value == weekdays.single() }?.key
                )
                val oldRepeat = viewModel.byWeekday.value
                if (oldRepeat.weekday == newWeekday) return@setOnWeekdaysChangeListener
                viewModel.byWeekday.compareAndSet(oldRepeat.copy(weekday = newWeekday))
            }
        }

        findViewById<WeeksSelector>(R.id.ale_weeks_selector).apply {
            viewModel.byWeekday.observe(this@LessonEditorActivity) { byWeekday ->
                weeks = byWeekday.weeks.toBooleanArray()
            }
            // Todo: обработчик изменения выбранных недель
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_lesson_editor, menu)
        menu.findItem(R.id.menu_lesson_editor_delete_lesson).isVisible = (lesson != null)
        menu.setColor(getCompatColor(R.color.action_bar_icons_color))
        return true
    }

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
                            R.string.activity_lesson_editor_incorrect_subject_name_message
                        }
                        Error.WRONG_TIMES -> {
                            R.string.activity_lesson_editor_incorrect_time_message
                        }
                    }
                )
            } ?: run {
                viewModel.viewModelScope.launch {
                    if (viewModel.isSubjectNameChangedAndNotLast()) {
                        alert(
                            R.string.activity_lesson_editor_alert_rename_lessons_message,
                            R.string.activity_lesson_editor_alert_rename_lessons_title
                        ) {
                            positiveButton(R.string.activity_lesson_editor_alert_rename_lessons_yes) {
                                viewModel.viewModelScope.launch {
                                    viewModel.save(true)
                                    finish()
                                }
                            }
                            negativeButton(R.string.activity_lesson_editor_alert_rename_lessons_no) {
                                viewModel.viewModelScope.launch {
                                    viewModel.save(false)
                                    finish()
                                }
                            }
                            neutralPressed(R.string.activity_lesson_editor_alert_rename_lessons_cancel) {}
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
                        R.string.activity_lesson_editor_alert_delete_homeworks_message,
                        R.string.activity_lesson_editor_alert_delete_homeworks_title
                    ) {
                        positiveButton(R.string.activity_lesson_editor_alert_delete_homeworks_yes) {
                            viewModel.viewModelScope.launch {
                                viewModel.delete()
                                finish()
                            }
                        }
                        negativeButton(R.string.activity_lesson_editor_alert_delete_homeworks_cancel) {}
                    }.show()
                } else {
                    alert(R.string.activity_lesson_editor_alert_delete_message) {
                        positiveButton(R.string.activity_lesson_editor_alert_delete_yes) {
                            viewModel.viewModelScope.launch {
                                viewModel.delete()
                                finish()
                            }
                        }
                        negativeButton(R.string.activity_lesson_editor_alert_delete_no) {}
                    }.show()
                }
            }
            true
        }
        else -> false
    }
}
