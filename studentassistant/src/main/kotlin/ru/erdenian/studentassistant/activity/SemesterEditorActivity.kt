package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment
import kotlinx.android.synthetic.main.content_semester_editor.*
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

class SemesterEditorActivity : AppCompatActivity(),
    View.OnClickListener,
    CalendarDatePickerDialogFragment.OnDateSetListener,
    TextWatcher {

  private companion object {

    const val FIRST_DAY = "first_day"
    const val LAST_DAY = "last_day"

    const val FIRST_DAY_TAG = "first_day_tag"
    const val LAST_DAY_TAG = "last_day_tag"
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

    content_semester_editor_semester_name_edit_text.addTextChangedListener(this)
    content_semester_editor_first_day.setOnClickListener(this)
    content_semester_editor_last_day.setOnClickListener(this)

    if (savedInstanceState == null) {
      with(semester) {
        if (this == null) {
          supportActionBar!!.title = getString(R.string.title_activity_semester_editor_new_semester)
        } else {
          content_semester_editor_semester_name_edit_text.setText(name)

          this@SemesterEditorActivity.firstDay = firstDay
          content_semester_editor_first_day.text = firstDay.toString()
          this@SemesterEditorActivity.lastDay = lastDay
          content_semester_editor_last_day.text = lastDay.toString()
        }
      }
    } else {
      val firstDayString = savedInstanceState.getString(FIRST_DAY)
      if (firstDayString != "null") {
        firstDay = LocalDate.parse(firstDayString)
        content_semester_editor_first_day.text = firstDay.toString()
      }

      val lastDayString = savedInstanceState.getString(LAST_DAY)
      if (lastDayString != "null") {
        lastDay = LocalDate.parse(lastDayString)
        content_semester_editor_last_day.text = lastDay.toString()
      }
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putString(FIRST_DAY, firstDay.toString())
    outState.putString(LAST_DAY, lastDay.toString())
    super.onSaveInstanceState(outState)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_semester_editor, menu)
    menu.setColor(getCompatColor(R.color.action_bar_icons_color))
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> finish()
      R.id.menu_semester_editor_save -> {
        if (content_semester_editor_semester_name.isErrorEnabled) {
          toast(content_semester_editor_semester_name.error.toString())
          return super.onOptionsItemSelected(item)
        }

        val name = if (!content_semester_editor_semester_name_edit_text.text?.trim().isNullOrEmpty()) {
          content_semester_editor_semester_name_edit_text.text.toString().trim().toSingleLine()
        } else {
          toast(R.string.activity_semester_editor_incorrect_name_message)
          return super.onOptionsItemSelected(item)
        }

        if (firstDay == null) {
          toast(R.string.activity_semester_editor_incorrect_first_day)
          return super.onOptionsItemSelected(item)
        }

        if (lastDay == null) {
          toast(R.string.activity_semester_editor_incorrect_last_day)
          return super.onOptionsItemSelected(item)
        }

        if (!firstDay!!.isBefore(lastDay!!)) {
          toast(R.string.activity_semester_editor_incorrect_dates)
          return super.onOptionsItemSelected(item)
        }

        if (semester == null)
          ScheduleManager.addSemester(Semester(name, firstDay!!, lastDay!!))
        else
          ScheduleManager.updateSemester(semester!!.copy(name, firstDay!!, lastDay!!))

        finish()
      }
      else -> throw IllegalArgumentException("Неизвестный id: ${item.itemId}")
    }
    return super.onOptionsItemSelected(item)
  }

  override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit

  override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit

  override fun afterTextChanged(s: Editable) {
    with(content_semester_editor_semester_name) {
      isErrorEnabled = true

      if (semestersNames.contains(s.toString().trim().toSingleLine()))
        error = getString(R.string.activity_semester_editor_error_name_not_avaliable)
      else isErrorEnabled = false
    }
  }

  override fun onClick(v: View) {
    when (v.id) {
      R.id.content_semester_editor_first_day -> showDatePicker(this, preselectedDate = firstDay, tag = FIRST_DAY_TAG)
      R.id.content_semester_editor_last_day -> showDatePicker(this, preselectedDate = lastDay, tag = LAST_DAY_TAG)
      else -> throw IllegalArgumentException("Неизвестный id: ${v.id}")
    }
  }

  override fun onDateSet(dialog: CalendarDatePickerDialogFragment, year: Int, monthOfYear: Int, dayOfMonth: Int) {
    val newDate = LocalDate(year, monthOfYear + 1, dayOfMonth)
    when (dialog.tag) {
      FIRST_DAY_TAG -> {
        firstDay = newDate
        content_semester_editor_first_day.text = firstDay!!.toString("dd.MM.yyyy")
      }
      LAST_DAY_TAG -> {
        lastDay = newDate
        content_semester_editor_last_day.text = lastDay!!.toString("dd.MM.yyyy")
      }
      else -> throw IllegalArgumentException("Неизвестный тэг: ${dialog.tag}")
    }
  }
}
