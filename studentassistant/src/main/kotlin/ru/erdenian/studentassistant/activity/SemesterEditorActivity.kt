package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_semester_editor.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.toast
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.setColor
import ru.erdenian.studentassistant.extensions.showDatePicker
import ru.erdenian.studentassistant.extensions.toSingleLine
import ru.erdenian.studentassistant.localdata.ScheduleManager
import ru.erdenian.studentassistant.schedule.Semester


class SemesterEditorActivity : AppCompatActivity() {

  private companion object {

    const val FIRST_DAY = "first_day"
    const val LAST_DAY = "last_day"
  }

  private val semester: Semester? by lazy { ScheduleManager.getSemesterOrNull(intent.getLongExtra(SEMESTER_ID, -1L)) }

  private val semestersNames: List<String> by lazy { ScheduleManager.semestersNames.filter { it != semester?.name } }

  private var firstDay: LocalDate? = null
  private var lastDay: LocalDate? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_semester_editor)

    setSupportActionBar(toolbar)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    content_semester_editor_semester_name_edit_text.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) = Unit
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        with(content_semester_editor_semester_name) {
          isErrorEnabled = true

          if (semestersNames.contains(s.toString().trim().toSingleLine()))
            error = getString(R.string.activity_semester_editor_error_name_not_avaliable)
          else isErrorEnabled = false
        }
      }
    })
    content_semester_editor_first_day.setOnClickListener { _ ->
      showDatePicker { newDate ->
        firstDay = newDate
        content_semester_editor_first_day.text = newDate.toString("dd.MM.yyyy")
      }
    }
    content_semester_editor_last_day.setOnClickListener { _ ->
      showDatePicker { newDate ->
        lastDay = newDate
        content_semester_editor_last_day.text = newDate.toString("dd.MM.yyyy")
      }
    }

    if (savedInstanceState == null) {
      semester?.also { s ->
        content_semester_editor_semester_name_edit_text.setText(s.name)

        firstDay = s.firstDay
        content_semester_editor_first_day.text = s.firstDay.toString()
        lastDay = s.lastDay
        content_semester_editor_last_day.text = s.lastDay.toString()
      } ?: run {
        supportActionBar!!.title = getString(R.string.title_activity_semester_editor_new_semester)
      }
    } else {
      savedInstanceState.getString(FIRST_DAY)?.let { s ->
        firstDay = LocalDate.parse(s)
        content_semester_editor_first_day.text = firstDay.toString()
      }
      savedInstanceState.getString(LAST_DAY)?.let { s ->
        lastDay = LocalDate.parse(s)
        content_semester_editor_last_day.text = lastDay.toString()
      }
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putString(FIRST_DAY, firstDay?.toString())
    outState.putString(LAST_DAY, lastDay?.toString())
    super.onSaveInstanceState(outState)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_semester_editor, menu)
    menu.setColor(getCompatColor(R.color.action_bar_icons_color))
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> {
        finish()
        return true
      }
      R.id.menu_semester_editor_save -> {
        if (content_semester_editor_semester_name.isErrorEnabled) {
          toast(content_semester_editor_semester_name.error.toString())
          return true
        }

        val name = if (!content_semester_editor_semester_name_edit_text.text?.trim().isNullOrEmpty()) {
          content_semester_editor_semester_name_edit_text.text.toString().trim().toSingleLine()
        } else {
          toast(R.string.activity_semester_editor_incorrect_name_message)
          return true
        }

        val first = firstDay ?: run {
          toast(R.string.activity_semester_editor_incorrect_first_day)
          return true
        }

        val last = lastDay ?: run {
          toast(R.string.activity_semester_editor_incorrect_last_day)
          return true
        }

        if (last < first) {
          toast(R.string.activity_semester_editor_incorrect_dates)
          return true
        }

        semester?.let { s ->
          ScheduleManager.updateSemester(s.copy(name, first, last))
        } ?: run {
          ScheduleManager.addSemester(Semester(name, first, last))
        }

        finish()
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }
}
