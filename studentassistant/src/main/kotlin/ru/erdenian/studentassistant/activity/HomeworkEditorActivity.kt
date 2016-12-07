package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment
import com.google.common.collect.ImmutableSortedSet
import kotlinx.android.synthetic.main.content_homework_editor.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.toast
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.showDatePicker
import ru.erdenian.studentassistant.schedule.Homework
import ru.erdenian.studentassistant.schedule.Lesson
import ru.erdenian.studentassistant.schedule.ScheduleManager
import ru.erdenian.studentassistant.schedule.Semester


//37,97

class HomeworkEditorActivity : AppCompatActivity(),
        AdapterView.OnItemSelectedListener,
        View.OnClickListener,
        CalendarDatePickerDialogFragment.OnDateSetListener {


    companion object {
        const val SEMESTER_ID = "semester_id"
        const val HOMEWORK_ID = "homework_id"
    }

    private val semester: Semester by lazy { ScheduleManager[intent.getLongExtra(SEMESTER_ID, -1)]!! }
    private var lessons: ImmutableSortedSet<Lesson> = semester.lessons
    //вернуть
    //private var homework: Homework? by lazy { semester.homeworks.get(intent.getLongExtra(HOMEWORK_ID, -1)) }
    //
    private var homework: Homework? = semester.homeworks.elementAt(intent.getLongExtra(HOMEWORK_ID, -1) as Int)
    private var subjectName: String = ""
    private var deadlineDay: LocalDate = LocalDate(0, 0, 0)
    private var deadlineTime: LocalTime = LocalTime(0, 0)
    private var flag: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homework_editor)

        setSupportActionBar(toolbar)


        content_homeworks_editor_description.text = homework?.description as Editable? ?: R.string.content_homework_editor_description as Editable?

        val array = mutableListOf<String>()

        for (lesson in lessons) {
            if (!array.contains(lesson.name)) {
                array.add(lesson.name)
            }
        }

        val adapter = ArrayAdapter<String>(this, -1, array)
        content_homeworks_editor_subject.adapter = adapter
        content_homeworks_editor_subject.onItemSelectedListener = this

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.content_homework_editor_date -> {
                var data = LocalDate.now()
                showDatePicker(this, data, semester!!.lastDay)

            }
            R.id.content_homework_editor_save -> {
                if (subjectName.isNullOrEmpty()) {
                    toast(R.string.content_homework_editor_activity_null_subject)
                    return
                }
                var description = if (content_homeworks_editor_description.text.trim().isNullOrEmpty()) {
                    toast(R.string.content_homework_editor_activity_null_description)
                    return
                } else {
                    content_homeworks_editor_description.text.trim().toString()
                }
                if (!flag) {
                    toast(R.string.content_homework_editor_activity_null_time_or_day)
                    return
                }

                var newHomework = homework?.copy(subjectName, description, deadlineDay, deadlineTime) ?:
                        Homework(subjectName, description, deadlineDay, deadlineTime)

                ///////////////////////////
                //добавить пару в семестр//
                ///////////////////////////

                finish()
            }
            else -> throw IllegalArgumentException("Неизвестный id: ${v.id}")
        }
    }

    override fun onDateSet(dialog: CalendarDatePickerDialogFragment?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        var thisDay = LocalDate(year, monthOfYear, dayOfMonth)
        if (!(semester.getLessons(thisDay) as String).contains(subjectName)) {
            toast(R.string.content_homework_editor_activity_error_day)
        } else {
            deadlineDay = thisDay
            for (lesson in semester.getLessons(thisDay)) {
                if (lesson.name.equals(subjectName)) {
                    deadlineTime = lesson.startTime
                }
            }
            flag = true
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        subjectName = (view as Spinner).getChildAt(position) as String
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}