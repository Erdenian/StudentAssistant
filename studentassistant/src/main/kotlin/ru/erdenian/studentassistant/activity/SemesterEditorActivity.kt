package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment
import kotlinx.android.synthetic.main.content_semester_editor.*
import kotlinx.android.synthetic.main.toolbar.*
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
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
        val SEMESTER_ID = "semester_id"
        private val FIRST_DAY_TAG = "first_day_tag"
        private val LAST_DAY_TAG = "last_day_tag"
    }

    private val semesterId: Long by lazy { intent.getLongExtra(LessonsEditorActivity.SEMESTER_ID, -1) }
    private val semester: Semester by lazy { ScheduleManager[semesterId]!! }

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
        val semester = ScheduleManager[semesterId]
        if (semester == null) {
            finish()
            return
        }

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
        if (s!!.isEmpty()) content_semester_editor_semester_name.error = "Пустое имя"
        else name = s.toString()
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
            else -> Log.wtf(this.javaClass.name, "Неизвестный тэг: " + dialog.tag)
        }
    }
}
