package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_lesson_information.*
import kotlinx.android.synthetic.main.content_lesson_information.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivity
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.setColor
import ru.erdenian.studentassistant.schedule.ScheduleManager

class LessonInformationActivity : AppCompatActivity() {

    companion object {
        const val SEMESTER_ID = "semester_id"
        const val LESSON_ID = "lesson_id"
        const val DATE = "date"
    }

    private val semesterId: Long by lazy { intent.getLongExtra(SEMESTER_ID, -1) }
    private val lessonId: Long by lazy { intent.getLongExtra(LESSON_ID, -1) }
    private val date: LocalDate by lazy { LocalDate(intent.getStringExtra(DATE)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_information)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        activity_lesson_information_add_homework.setOnClickListener {
            startActivity<HomeworkEditorActivity>(
                    HomeworkEditorActivity.SEMESTER_ID to semesterId,
                    HomeworkEditorActivity.LESSON_ID to lessonId)
        }
    }

    override fun onStart() {
        super.onStart()
        with(ScheduleManager.getLesson(semesterId, lessonId)) {
            if (this == null) {
                finish()
                return
            }

            content_lesson_information_subject_name.text = subjectName
            content_lesson_information_start_time.text = startTime.toString("HH:mm")
            content_lesson_information_end_time.text = endTime.toString("HH:mm")
            content_lesson_information_type.text = type
        }

        content_lesson_information_homeworks_parent.removeAllViews()

        ScheduleManager.getActualHomeworks(semesterId, lessonId).forEach {
            val card = layoutInflater.inflate(R.layout.card_homework, content_lesson_information_homeworks_parent, false)

            (card.findViewById(R.id.card_homework_subject_name) as TextView).text = it.subjectName
            (card.findViewById(R.id.card_homework_description) as TextView).text = it.description
            (card.findViewById(R.id.card_homework_deadline) as TextView).text = it.deadline.toString("dd.MM.yyyy")

            val homeworkId = it.id

            card.setOnClickListener {
                startActivity<HomeworkEditorActivity>(
                        HomeworkEditorActivity.SEMESTER_ID to semesterId,
                        HomeworkEditorActivity.LESSON_ID to lessonId,
                        HomeworkEditorActivity.HOMEWORK_ID to homeworkId
                )
            }

            content_lesson_information_homeworks_parent.addView(card)
        }

        content_lesson_information_no_homeworks.visibility =
                if (content_lesson_information_homeworks_parent.childCount > 0) View.GONE
                else View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_lesson_information, menu)
        menu.setColor(getCompatColor(R.color.action_bar_icons_color))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_lesson_information_edit_button -> {
                startActivity<LessonEditorActivity>(
                        LessonEditorActivity.SEMESTER_ID to semesterId,
                        LessonEditorActivity.LESSON_ID to lessonId)
            }
            else -> throw IllegalArgumentException("Неизвестный id: ${item.itemId}")
        }
        return super.onOptionsItemSelected(item)
    }
}
