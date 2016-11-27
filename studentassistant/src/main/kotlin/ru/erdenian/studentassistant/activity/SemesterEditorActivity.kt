package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment
import kotlinx.android.synthetic.main.content_semester_editor.*
import kotlinx.android.synthetic.main.content_semester_editor.view.*
import kotlinx.android.synthetic.main.toolbar.*
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.getAnyExtra
import ru.erdenian.studentassistant.extensions.showDatePicker
import ru.erdenian.studentassistant.schedule.OnScheduleUpdateListener
import ru.erdenian.studentassistant.schedule.ScheduleManager
import ru.erdenian.studentassistant.schedule.Semester

class SemesterEditorActivity : AppCompatActivity(),
        View.OnClickListener,
        CalendarDatePickerDialogFragment.OnDateSetListener,
        TextWatcher,
        OnScheduleUpdateListener {

    companion object {
        val SEMESTER = "semester"
        private val FIRST_DAY_TAG = "first_day_tag"
        private val LAST_DAY_TAG = "last_day_tag"
    }

    private val semester: Semester? by lazy { intent.getAnyExtra(SEMESTER) as Semester? }

    private lateinit var semestersNames: List<String>

    private lateinit var name: String
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

        name = semester?.name ?: ""
        firstDay = semester?.firstDay
        lastDay = semester?.lastDay
    }

    override fun onStart() {
        super.onStart()
        ScheduleManager.setOnScheduleUpdateListener(this)
        onScheduleUpdate()
        invalidateViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!content_semester_editor_semester_name.isErrorEnabled && (firstDay != null) && (lastDay != null))
            ScheduleManager.addSemester(semester?.copy(name = name, firstDay = firstDay!!, lastDay = lastDay!!) ?:
                    Semester(name, firstDay!!, lastDay!!))
    }

    override fun onScheduleUpdate() {
        semestersNames = ScheduleManager.semestersNames.filter { it != semester?.name }
    }

    fun invalidateViews() {
        content_semester_editor_semester_name_edit_text.setText(name)
        content_semester_editor_first_day.text = firstDay?.toString() ?: getString(R.string.activity_semester_editor_first_day)
        content_semester_editor_last_day.text = lastDay?.toString() ?: getString(R.string.activity_semester_editor_last_day)
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
        with(content_semester_editor_semester_name) {
            isErrorEnabled = true

            if (s!!.isEmpty()) error = getString(R.string.activity_semester_editor_error_empty_name)
            else if (semestersNames.contains(s.toString())) error = getString(R.string.activity_semester_editor_error_name_not_avaliable)
            else {
                isErrorEnabled = false
                name = content_semester_editor_semester_name_edit_text.text.toString()
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.content_semester_editor_first_day -> showDatePicker(this,
                    lastDay = lastDay?.minusDays(1), preselected = firstDay, tag = FIRST_DAY_TAG)
            R.id.content_semester_editor_last_day -> showDatePicker(this,
                    firstDay = firstDay?.plusDays(1), preselected = lastDay, tag = LAST_DAY_TAG)
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
        invalidateViews()
    }
}
