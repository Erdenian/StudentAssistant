package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_lesson_information.*
import kotlinx.android.synthetic.main.content_lesson_information.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivity
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.setColor
import ru.erdenian.studentassistant.localdata.ScheduleManager

class LessonInformationActivity : AppCompatActivity(),
    ScheduleManager.OnScheduleUpdateListener {

    private val semesterId: Long by lazy {
        intent.getLongExtra(SEMESTER_ID, -1L).takeIf { it != -1L }
            ?: throw IllegalStateException("Не передан id семестра")
    }
    private val lessonId: Long by lazy {
        intent.getLongExtra(LESSON_ID, -1L).takeIf { it != -1L }
            ?: throw IllegalStateException("Не передан id пары")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_information)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        activity_lesson_information_add_homework.setOnClickListener {
            startActivity<HomeworkEditorActivity>(SEMESTER_ID to semesterId, LESSON_ID to lessonId)
        }
    }

    override fun onStart() {
        super.onStart()
        ScheduleManager.addOnScheduleUpdateListener(this)
        onScheduleUpdate()
    }

    override fun onScheduleUpdate() {
        val lesson = ScheduleManager.getLesson(semesterId, lessonId)

        content_lesson_information_subject_name.text = lesson.subjectName
        content_lesson_information_start_time.text = lesson.startTime.toString("HH:mm")
        content_lesson_information_end_time.text = lesson.endTime.toString("HH:mm")
        content_lesson_information_type.text = lesson.type

        content_lesson_information_homeworks_parent.removeAllViews()

        ScheduleManager.getActualHomeworks(semesterId, lesson.subjectName).forEach {
            val card = layoutInflater.inflate(
                R.layout.card_homework,
                content_lesson_information_homeworks_parent,
                false
            )

            card.findViewById<TextView>(R.id.card_homework_subject_name).text = it.subjectName
            card.findViewById<TextView>(R.id.card_homework_description).text = it.description
            card.findViewById<TextView>(R.id.card_homework_deadline).text =
                    it.deadline.toString("dd.MM.yyyy")

            val homeworkId = it.id

            card.setOnClickListener { _ ->
                startActivity<HomeworkEditorActivity>(
                    SEMESTER_ID to semesterId,
                    LESSON_ID to lessonId,
                    HOMEWORK_ID to homeworkId
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
            R.id.menu_lesson_information_edit_button ->
                startActivity<LessonEditorActivity>(
                    SEMESTER_ID to semesterId,
                    LESSON_ID to lessonId
                )
            else -> throw IllegalArgumentException("Неизвестный id: ${item.itemId}")
        }
        return super.onOptionsItemSelected(item)
    }
}
