package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import kotlinx.android.synthetic.main.card_lesson_information_main_info.*
import kotlinx.android.synthetic.main.content_lesson_information.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivity
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.setColor
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
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

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
                //Todo: открытие окна информации о дз
                //setOnClickListener {
                //   context.startActivity<HomeworkInformationActivity>(
                //      LessonInformationActivity.SEMESTER_ID to semester!!.id,
                //      LessonInformationActivity.LESSON_ID to lesson.id) не удалять
                //}
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_lesson_information, menu)
        menu.setColor(getCompatColor(R.color.action_bar_icons_color))
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_lesson_information_edit_button -> {
                startActivity<LessonEditorActivity>(
                        LessonEditorActivity.SEMESTER_ID to semester!!.id,
                        LessonEditorActivity.LESSON_ID to lesson.id)
            }
            else -> throw IllegalArgumentException("Неизвестный id: ${item.itemId}")
        }
        return super.onOptionsItemSelected(item)
    }

    //Todo: открытие окна добавления дз
    /*override fun onClick(v: View) {
        when (v.id) {
            R.id.lesson_information_add_homework_button -> {
                startActivity<HomeworkEditorActivity>("SEMESTER_ID" to SEMESTER_ID, "HOMEWORK_ID" to -1)
            }
            else -> throw IllegalArgumentException("Неизвестный id: ${v.id}")

        }
    }не удалять*/

}
