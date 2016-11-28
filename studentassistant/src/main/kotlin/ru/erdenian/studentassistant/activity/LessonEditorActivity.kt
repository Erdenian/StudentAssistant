package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.common.base.Joiner
import kotlinx.android.synthetic.main.content_lesson_editor.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.setColor
import ru.erdenian.studentassistant.schedule.Lesson
import ru.erdenian.studentassistant.schedule.ScheduleManager
import ru.erdenian.studentassistant.schedule.Semester

class LessonEditorActivity : AppCompatActivity() {

    companion object {
        const val SEMESTER_ID = "semester_id"
        const val LESSON_ID = "lesson_id"
    }

    private val semester: Semester by lazy { ScheduleManager[intent.getLongExtra(SEMESTER_ID, -1)]!! }
    private val lesson: Lesson? by lazy { semester.getLesson(intent.getLongExtra(LESSON_ID, -1)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_editor)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (lesson == null) supportActionBar!!.title = getString(R.string.title_activity_lesson_editor_new_lesson)

        if (lesson != null) {
            content_lesson_editor_subject_name_edit_text.setText(lesson!!.name)
            if (lesson!!.type != null)
                content_lesson_editor_lesson_type_edit_text.setText(lesson!!.type)
            content_lesson_editor_teachers_edit_text.setText(Joiner.on(", ").join(lesson!!.teachers))
            content_lesson_editor_classrooms_edit_text.setText(Joiner.on(", ").join(lesson!!.classrooms))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //Todo: сохранение данных
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        //Todo: восстановление данных
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        menu.setColor(getCompatColor(R.color.action_bar_icons_color))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_editor_save -> {
                //Todo: сохранение пары
            }
            else -> throw IllegalArgumentException("Неизвестный id: ${item.itemId}")
        }
        return super.onOptionsItemSelected(item)
    }

}
