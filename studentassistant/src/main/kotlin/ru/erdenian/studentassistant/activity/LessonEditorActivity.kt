package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.TextView
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment
import com.google.common.base.Joiner
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSortedSet
import kotlinx.android.synthetic.main.content_lesson_editor.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.toast
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.asSingleLine
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.setColor
import ru.erdenian.studentassistant.extensions.showTimePicker
import ru.erdenian.studentassistant.schedule.Lesson
import ru.erdenian.studentassistant.schedule.LessonRepeat
import ru.erdenian.studentassistant.schedule.ScheduleManager

class LessonEditorActivity : AppCompatActivity(),
        RadialTimePickerDialogFragment.OnTimeSetListener {

    companion object {
        const val SEMESTER_ID = "semester_id"
        const val LESSON_ID = "lesson_id"

        private const val START_TIME = "start_time"
        private const val END_TIME = "end_time"
        private const val WEEKDAY = "weekday"
        private const val WEEKS = "weeks"

        private const val START_TIME_TAG = "first_day_tag"
        private const val END_TIME_TAG = "last_day_tag"

        private const val TIME_FORMAT = "HH:mm"
    }

    private val semesterId: Long by lazy { intent.getLongExtra(SEMESTER_ID, -1) }
    private val lesson: Lesson? by lazy { ScheduleManager.getLesson(semesterId, intent.getLongExtra(LESSON_ID, -1)) }

    private var startTime: LocalTime? = null
    private var endTime: LocalTime? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_editor)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        content_lesson_editor_start_time.setOnClickListener { showTimePicker(this, startTime, START_TIME_TAG) }
        content_lesson_editor_end_time.setOnClickListener { showTimePicker(this, endTime, END_TIME_TAG) }

        content_lesson_editor_remove_week.setOnClickListener {
            with(content_lesson_editor_weeks_parent) {
                removeViewAt(childCount - 1)
                if (childCount <= 1) content_lesson_editor_remove_week.isEnabled = false
            }
        }

        content_lesson_editor_add_week.setOnClickListener {
            with(content_lesson_editor_weeks_parent) {
                val checkbox = layoutInflater.inflate(R.layout.content_lesson_editor_week_checkbox, this, false)
                (checkbox.findViewById(R.id.content_lesson_editor_week_number) as TextView).text = (childCount + 1).toString()
                addView(checkbox)
                content_lesson_editor_remove_week.isEnabled = true
            }
        }

        var weeks = listOf(true)
        if (savedInstanceState == null) {
            with(lesson) {
                if (this == null) {
                    supportActionBar!!.title = getString(R.string.title_activity_lesson_editor_new_lesson)
                } else {
                    content_lesson_editor_subject_name_edit_text.setText(subjectName)
                    content_lesson_editor_lesson_type_edit_text.setText(type)
                    content_lesson_editor_teachers_edit_text.setText(Joiner.on(", ").join(teachers))
                    content_lesson_editor_classrooms_edit_text.setText(Joiner.on(", ").join(classrooms))

                    this@LessonEditorActivity.startTime = startTime
                    content_lesson_editor_start_time.text = startTime.toString(TIME_FORMAT)
                    this@LessonEditorActivity.endTime = endTime
                    content_lesson_editor_end_time.text = endTime.toString(TIME_FORMAT)


                    when (lessonRepeat) {
                        is LessonRepeat.ByWeekday -> {
                            content_lesson_editor_weekdays.setPosition(lessonRepeat.weekday - 1, false)
                            weeks = lessonRepeat.weeks
                        }
                        is LessonRepeat.ByDates -> TODO()
                        else -> throw IllegalStateException("Неизвестный тип повторения: $lessonRepeat")
                    }
                }
            }
        } else {
            val startTimeString = savedInstanceState.getString(START_TIME)
            if (startTimeString != "null") {
                startTime = LocalTime.parse(startTimeString)
                content_lesson_editor_start_time.text = startTime!!.toString(TIME_FORMAT)
            }

            val endTimeString = savedInstanceState.getString(END_TIME, null)
            if (endTimeString != "null") {
                endTime = LocalTime.parse(endTimeString)
                content_lesson_editor_end_time.text = endTime!!.toString(TIME_FORMAT)
            }

            content_lesson_editor_weekdays.setPosition(savedInstanceState.getInt(WEEKDAY), false)

            weeks = ImmutableList.copyOf(savedInstanceState.getBooleanArray(WEEKS).toList())
        }

        weeks.forEach {
            layoutInflater.inflate(R.layout.content_lesson_editor_week_checkbox,
                    content_lesson_editor_weeks_parent)
        }
        if (weeks.size <= 1) content_lesson_editor_remove_week.isEnabled = false

        for ((i, w) in weeks.withIndex()) {
            val checkbox = content_lesson_editor_weeks_parent.getChildAt(i)
            (checkbox.findViewById(R.id.content_lesson_editor_week_checkbox) as CheckBox).isChecked = w
            (checkbox.findViewById(R.id.content_lesson_editor_week_number) as TextView).text = (i + 1).toString()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(START_TIME, startTime.toString())
        outState.putString(END_TIME, endTime.toString())
        outState.putInt(WEEKDAY, content_lesson_editor_weekdays.position)

        val weeks = BooleanArray(content_lesson_editor_weeks_parent.childCount)
        for (i in 0 until content_lesson_editor_weeks_parent.childCount) {
            weeks[i] = (content_lesson_editor_weeks_parent.getChildAt(i)
                    .findViewById(R.id.content_lesson_editor_week_checkbox) as CheckBox).isChecked
        }
        outState.putBooleanArray(WEEKS, weeks)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val weeks = savedInstanceState.getBooleanArray(WEEKS)
        for ((i, w) in weeks.withIndex()) {
            val checkbox = content_lesson_editor_weeks_parent.getChildAt(i)
            (checkbox.findViewById(R.id.content_lesson_editor_week_checkbox) as CheckBox).isChecked = w
            (checkbox.findViewById(R.id.content_lesson_editor_week_number) as TextView).text = (i + 1).toString()
        }
        if (weeks.size <= 1) content_lesson_editor_remove_week.isEnabled = false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_lesson_editor, menu)
        menu.findItem(R.id.menu_lesson_editor_delete_lesson).isVisible = (lesson != null)
        menu.setColor(getCompatColor(R.color.action_bar_icons_color))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_lesson_editor_save -> {
                val name = if (content_lesson_editor_subject_name_edit_text.text.trim().isNotBlank()) {
                    content_lesson_editor_subject_name_edit_text.text.toString().asSingleLine.trim()
                } else {
                    toast(R.string.activity_lesson_editor_incorrect_subject_name_message)
                    return super.onOptionsItemSelected(item)
                }

                val type = content_lesson_editor_lesson_type_edit_text.text.toString().asSingleLine.trim()

                val teachers = content_lesson_editor_teachers_edit_text.text.toString().
                        asSingleLine.split(",").map(String::trim).filter(String::isNotBlank)

                val classrooms = content_lesson_editor_classrooms_edit_text.text.toString().
                        asSingleLine.split(",").map(String::trim).filter(String::isNotBlank)

                if (startTime == null) {
                    toast(R.string.activity_lesson_editor_incorrect_start_time_message)
                    return super.onOptionsItemSelected(item)
                }

                if (endTime == null) {
                    toast(R.string.activity_lesson_editor_incorrect_end_time_message)
                    return super.onOptionsItemSelected(item)
                }

                if (!startTime!!.isBefore(endTime)) {
                    toast(R.string.activity_lesson_editor_incorrect_time_message)
                    return super.onOptionsItemSelected(item)
                }

                val weekday = content_lesson_editor_weekdays.position + 1

                val weeks = BooleanArray(content_lesson_editor_weeks_parent.childCount)
                for (i in 0 until content_lesson_editor_weeks_parent.childCount) {
                    weeks[i] = (content_lesson_editor_weeks_parent.getChildAt(i)
                            .findViewById(R.id.content_lesson_editor_week_checkbox) as CheckBox).isChecked
                }

                if (!weeks.contains(true)) {
                    toast(R.string.activity_lesson_editor_no_weeks_checked_message)
                    return super.onOptionsItemSelected(item)
                }

                if (lesson == null) {
                    ScheduleManager.addLesson(semesterId, Lesson(name, type, ImmutableSortedSet.copyOf(teachers),
                            ImmutableSortedSet.copyOf(classrooms), startTime!!, endTime!!,
                            LessonRepeat.ByWeekday(weekday, ImmutableList.copyOf(weeks.toList()))))
                } else {
                    ScheduleManager.updateLesson(semesterId, lesson!!.copy(name, type, ImmutableSortedSet.copyOf(teachers),
                            ImmutableSortedSet.copyOf(classrooms), startTime!!, endTime!!,
                            LessonRepeat.ByWeekday(weekday, ImmutableList.copyOf(weeks.toList()))))
                }
                finish()
            }
            R.id.menu_lesson_editor_delete_lesson -> {
                ScheduleManager.removeLesson(semesterId, lesson!!.id)
                finish()
            }
            else -> throw IllegalArgumentException("Неизвестный id: ${item.itemId}")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onTimeSet(dialog: RadialTimePickerDialogFragment, hourOfDay: Int, minute: Int) {
        val newTime = LocalTime(hourOfDay, minute)
        when (dialog.tag) {
            START_TIME_TAG -> {
                startTime = newTime
                content_lesson_editor_start_time.text = newTime.toString(TIME_FORMAT)
            }
            END_TIME_TAG -> {
                endTime = newTime
                content_lesson_editor_end_time.text = newTime.toString(TIME_FORMAT)
            }
            else -> IllegalArgumentException("Неизвестный тег: ${dialog.tag}")
        }
    }
}
