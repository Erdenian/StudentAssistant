package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.google.common.collect.ImmutableSortedSet
import kotlinx.android.synthetic.main.activity_homework_editor.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.setColor
import ru.erdenian.studentassistant.extensions.showDatePicker
import ru.erdenian.studentassistant.localdata.ScheduleManager
import ru.erdenian.studentassistant.schedule.Homework
import ru.erdenian.studentassistant.schedule.Lesson
import ru.erdenian.studentassistant.schedule.Semester

class HomeworkEditorActivity : AppCompatActivity() {

  private companion object {

    const val DEADLINE = "deadline"
  }

  private val semester: Semester by lazy { ScheduleManager.getSemester(intent.getLongExtra(SEMESTER_ID, -1L)) }
  private val lesson: Lesson? by lazy { ScheduleManager.getLessonOrNull(semester.id, intent.getLongExtra(LESSON_ID, -1L)) }
  private val homework: Homework? by lazy { ScheduleManager.getHomeworkOrNull(semester.id, intent.getLongExtra(HOMEWORK_ID, -1L)) }

  private val subjects: ImmutableSortedSet<String> by lazy { ScheduleManager.getSubjects(semester.id) }

  private var deadline: LocalDate? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_homework_editor)

    setSupportActionBar(toolbar)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    content_homework_editor_subject_name.adapter = ArrayAdapter<String>(
        this,
        android.R.layout.simple_spinner_item,
        subjects.toTypedArray()
    )

    lesson?.let { _ -> content_homework_editor_subject_name.setSelection(subjects.indexOfFirst { it == lesson!!.subjectName }) }

    homework?.let {
      content_homework_editor_subject_name.setSelection(subjects.indexOfFirst { subject -> subject == it.subjectName })
      content_homeworks_editor_description.setText(it.description)
      content_homework_editor_deadline.text = it.deadline.toString("dd.MM.yyyy")
    }

    deadline = homework?.deadline
    content_homework_editor_deadline.setOnClickListener { _ ->
      showDatePicker(deadline, LocalDate.now(), semester.lastDay) { newDate ->
        deadline = newDate
        content_homework_editor_deadline.text = newDate.toString("dd.MM.yyyy")
      }
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putString(deadline.toString(), DEADLINE)
    super.onSaveInstanceState(outState)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    deadline = LocalDate.parse(savedInstanceState.get(DEADLINE) as String)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_homework_editor, menu)
    menu.findItem(R.id.menu_homework_editor_delete_homework).isVisible = (homework != null)
    menu.setColor(getCompatColor(R.color.action_bar_icons_color))
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> {
        finish()
        return true
      }
      R.id.menu_lesson_editor_save -> {
        val subjectName = content_homework_editor_subject_name.selectedItem.toString()

        val description = if (content_homeworks_editor_description.text.isNotBlank()) {
          content_homeworks_editor_description.text.trim().toString()
        } else {
          toast(R.string.content_homework_editor_activity_null_description)
          return super.onOptionsItemSelected(item)
        }

        val deadline = deadline ?: run {
          toast(R.string.content_homework_editor_activity_null_deadline)
          return super.onOptionsItemSelected(item)
        }

        homework?.let { homework ->
          ScheduleManager.updateHomework(semester.id, homework.copy(subjectName, description, deadline))
        } ?: run {
          ScheduleManager.addHomework(semester.id, Homework(subjectName, description, deadline))
        }

        finish()
        return true
      }
      R.id.menu_homework_editor_delete_homework -> {
        fun remove() {
          ScheduleManager.removeHomework(semester.id, homework!!.id)
          finish()
        }

        alert(R.string.activity_homework_editor_alert_delete_message) {
          positiveButton(R.string.activity_homework_editor_alert_delete_yes) { remove() }
          negativeButton(R.string.activity_homework_editor_alert_delete_no) {}
        }.show()
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }
}
