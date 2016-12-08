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
    private val lessons: ImmutableSortedSet<Lesson> = semester.lessons
    //вернуть
    //private var homework: Homework? by lazy { semester.homeworks.get(intent.getLongExtra(HOMEWORK_ID, -1)) }
    //
    private var homework: Homework? = semester.homeworks.elementAt(intent.getLongExtra(HOMEWORK_ID, -1) as Int)
    private var subjectName: String = ""
    private var deadlineDay: LocalDate = LocalDate(0, 0, 0)
    private var deadlineTime: LocalTime = LocalTime(0, 0)
    private var flag: Boolean = false;
    private var description = ""
    private var array = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homework_editor)

        setSupportActionBar(toolbar)


        content_homeworks_editor_description.text = homework?.description as Editable? ?: R.string.content_homework_editor_description as Editable?


        for (lesson in lessons) {
            if (!array.contains(lesson.name)) {
                array.add(lesson.name)
            }
        }

        var adapter = ArrayAdapter<String>(this, -1, array)
        content_homeworks_editor_subject.adapter = adapter
        content_homeworks_editor_subject.onItemSelectedListener = this

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.content_homework_editor_date -> {
                var data = LocalDate.now()

                if (flag) {
                    showDatePicker(this, data, semester!!.lastDay, deadlineDay)
                } else {
                    showDatePicker(this, data, semester!!.lastDay)
                }
            }
            R.id.content_homework_editor_save -> {
                if (subjectName.isNullOrEmpty()) {
                    toast(R.string.content_homework_editor_activity_null_subject)
                    return
                }
                description = if (content_homeworks_editor_description.text.trim().isNullOrEmpty()) {
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

    override fun onStart() {
        super.onStart()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        subjectName = (view as Spinner).getChildAt(position) as String
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putString(HomeworkEditorActivity.HOMEWORK_ID, "homework_id")
        outState.putString(HomeworkEditorActivity.SEMESTER_ID, "semester_id")
        outState.putString(subjectName, "subject_name")
        outState.putString(description, "description")
        outState.putString(deadlineDay.toString(), "deadline_day")
        outState.putString(deadlineTime.toString(), "deadline_time")
        outState.putBoolean("flag", flag)
        outState.putStringArray("array", array as Array<String>)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        subjectName = savedInstanceState.getString("subject_name")
        deadlineDay = LocalDate.parse(savedInstanceState.get("deadline_day") as String) as LocalDate
        deadlineTime = LocalTime.parse(savedInstanceState.get("deadline_time") as String) as LocalTime
        flag = savedInstanceState.getBoolean("flag")
        description = savedInstanceState.getString("description")
        array = savedInstanceState.getStringArray("array") as MutableList<String>

    }
}