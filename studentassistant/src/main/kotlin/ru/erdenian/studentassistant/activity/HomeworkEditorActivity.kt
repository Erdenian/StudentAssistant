package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment
import com.google.common.collect.ImmutableSortedSet
import kotlinx.android.synthetic.main.content_homework_editor.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.toast
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.setColor
import ru.erdenian.studentassistant.extensions.showDatePicker
import ru.erdenian.studentassistant.schedule.Homework
import ru.erdenian.studentassistant.schedule.Lesson
import ru.erdenian.studentassistant.schedule.ScheduleManager
import ru.erdenian.studentassistant.schedule.Semester

class HomeworkEditorActivity : AppCompatActivity(),
        CalendarDatePickerDialogFragment.OnDateSetListener {

    companion object {
        const val SEMESTER_ID = "semester_id"
        const val LESSON_ID = "lesson_id"
        const val HOMEWORK_ID = "homework_id"
    }

    private val semester: Semester by lazy { ScheduleManager.getSemester(intent.getLongExtra(SEMESTER_ID, -1))!! }
    private val lesson: Lesson? by lazy { ScheduleManager.getLesson(semester.id, intent.getLongExtra(LESSON_ID, -1)) }
    private val homework: Homework? by lazy { ScheduleManager.getHomework(semester.id, intent.getLongExtra(HOMEWORK_ID, -1)) }

    val subjects: ImmutableSortedSet<String> by lazy { ScheduleManager.getSubjects(semester.id) }

    private var deadline: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homework_editor)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        content_homework_editor_subject_name.adapter =
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subjects.toTypedArray())

        lesson?.let { content_homework_editor_subject_name.setSelection(subjects.indexOfFirst { it == lesson!!.subjectName }) }

        homework?.let {
            content_homeworks_editor_description.setText(it.description)
            content_homework_editor_deadline.text = it.deadline.toString("dd.MM.yyyy")
        }

        content_homework_editor_deadline.setOnClickListener {
            showDatePicker(this, LocalDate.now(), semester.lastDay, deadline)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(deadline.toString(), "deadline_day")
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        deadline = LocalDate.parse(savedInstanceState.get("deadline_day") as String) as LocalDate

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_homework_editor, menu)
        menu.findItem(R.id.menu_homework_editor_delete_homework).isVisible = (homework != null)
        menu.setColor(getCompatColor(R.color.action_bar_icons_color))
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_lesson_editor_save -> {
                val subjectName = content_homework_editor_subject_name.selectedItem as String

                val description = if (content_homeworks_editor_description.text.isNotBlank()) {
                    content_homeworks_editor_description.text.trim().toString()
                } else {
                    toast(R.string.content_homework_editor_activity_null_description)
                    return super.onOptionsItemSelected(item)
                }

                if (deadline == null) {
                    toast(R.string.content_homework_editor_activity_null_deadline)
                    return super.onOptionsItemSelected(item)
                }

                if (homework == null) ScheduleManager.addHomework(semester.id, Homework(subjectName, description, deadline!!))
                else ScheduleManager.updateHomework(semester.id, homework!!.copy(subjectName, description, deadline!!))

                finish()
            }
            R.id.menu_homework_editor_delete_homework -> {
                ScheduleManager.removeHomework(semester.id, homework!!.id)
                finish()
            }
            else -> throw IllegalArgumentException("Неизвестный id: ${item.itemId}")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDateSet(dialog: CalendarDatePickerDialogFragment?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        deadline = LocalDate(year, monthOfYear + 1, dayOfMonth)
        content_homework_editor_deadline.text = deadline!!.toString("dd.MM.yyyy")
    }
}