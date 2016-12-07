package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import kotlinx.android.synthetic.main.card_lesson_information_main_info.*
import kotlinx.android.synthetic.main.content_lesson_information.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.schedule.Lesson
import ru.erdenian.studentassistant.schedule.ScheduleManager
import ru.erdenian.studentassistant.schedule.Semester

class LessonInformationActivity : AppCompatActivity() {

    companion object {
        const val SEMESTER_ID = "semester_id"
        const val LESSON_ID = "lesson_id"
    }

    private val semester: Semester by lazy { ScheduleManager[intent.getLongExtra(SEMESTER_ID, -1)]!! }
    private val lesson: Lesson by lazy { semester.getLesson(intent.getLongExtra(LESSON_ID, -1))!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_information)

        setSupportActionBar(toolbar)

        with(lesson) {
            information_lesson_name_text.text = name
            lesson_information_start_time_text.text = startTime.toString("HH:mm")
            lesson_information_end_time_text.text = endTime.toString("HH:mm")
            lesson_information_type.text = type
        }

        for (homework in semester.homeworks) {
            with(layoutInflater.inflate(R.layout.card_lesson_information_homework, lesson_information_homework_layout, true)) {
                (findViewById(R.id.card_lesson_information_homework_info) as TextView).text = homework.description
                (findViewById(R.id.card_lesson_information_deadline_date) as TextView).text = homework.deadlineDay.toString()
            }
        }
    }
}
