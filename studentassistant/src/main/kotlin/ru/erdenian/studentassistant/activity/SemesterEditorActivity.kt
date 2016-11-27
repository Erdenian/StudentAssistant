package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment
import kotlinx.android.synthetic.main.content_semester_editor.*
import kotlinx.android.synthetic.main.toolbar.*
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.getAnyExtra
import ru.erdenian.studentassistant.extensions.showDatePicker
import ru.erdenian.studentassistant.schedule.OnScheduleUpdateListener
import ru.erdenian.studentassistant.schedule.ScheduleManager
import ru.erdenian.studentassistant.schedule.Semester

class SemesterEditorActivity : AppCompatActivity(),
        OnScheduleUpdateListener,
        View.OnClickListener,
        CalendarDatePickerDialogFragment.OnDateSetListener,
        TextWatcher {

    companion object {
        val SEMESTER = "semester"
        private val FIRST_DAY_TAG = "first_day_tag"
        private val LAST_DAY_TAG = "last_day_tag"
    }

    private val semester: Semester by lazy { intent.getAnyExtra(LessonsEditorActivity.SEMESTER) as Semester }

    private lateinit var name: String
    private lateinit var firstDay: LocalDate
    private lateinit var lastDay: LocalDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_semester_editor)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        content_semester_editor_semester_name_edit_text.addTextChangedListener(this)
        content_semester_editor_first_day.setOnClickListener(this)
        content_semester_editor_last_day.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        ScheduleManager.setOnScheduleUpdateListener(this)
        onScheduleUpdate()
    }

    override fun onScheduleUpdate() {
        content_semester_editor_semester_name_edit_text.setText(semester.name)
        content_semester_editor_first_day.text = semester.firstDay.toString()
        content_semester_editor_last_day.text = semester.lastDay.toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> throw IllegalArgumentException("Неизвестный id: ${item.itemId}")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        content_semester_editor_semester_name.isErrorEnabled = true
        if (s!!.isEmpty()) content_semester_editor_semester_name.error = "Пустое имя"
        else content_semester_editor_semester_name.isErrorEnabled = false
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.content_semester_editor_first_day -> showDatePicker(this, preselected = semester.firstDay, tag = FIRST_DAY_TAG)
            R.id.content_semester_editor_last_day -> showDatePicker(this, preselected = semester.lastDay, tag = FIRST_DAY_TAG)
            R.id.content_semester_editor_save -> finish()
            else -> throw IllegalArgumentException("Неизвестный id: ${v.id}")
        }
    }

    override fun onDateSet(dialog: CalendarDatePickerDialogFragment, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val newDate = LocalDate(year, monthOfYear + 1, dayOfMonth)
        when (dialog.tag) {
            FIRST_DAY_TAG -> firstDay = newDate
            LAST_DAY_TAG -> lastDay = newDate
            else -> throw IllegalArgumentException("Неизвестный тэг: ${dialog.tag}")
        }
    }
}
