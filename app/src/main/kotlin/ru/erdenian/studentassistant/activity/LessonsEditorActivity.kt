package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_lessons_editor.*
import kotlinx.android.synthetic.main.toolbar_with_spinner.*
import kotlinx.android.synthetic.main.view_pager.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.adapter.SchedulePagerAdapter
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.setColor
import ru.erdenian.studentassistant.localdata.ScheduleManager

class LessonsEditorActivity : AppCompatActivity(),
    ScheduleManager.OnScheduleUpdateListener {

    private val semesterId: Long by lazy {
        intent.getLongExtra(SEMESTER_ID, -1L).takeIf { it != -1L }
            ?: throw IllegalStateException("Не передан id семестра")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lessons_editor)

        setSupportActionBar(toolbar_with_spinner)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item_semesters,
            resources.getStringArray(R.array.lesson_repeat_types)
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_semesters)
        toolbar_with_spinner_spinner.adapter = adapter
        toolbar_with_spinner_spinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        activity_lessons_editor_flipper.displayedChild = position
                    }
                }

        view_pager_pager_tab_strip.setTextColor(getCompatColor(R.color.colorPrimary))
        view_pager_pager_tab_strip.setTabIndicatorColorResource(R.color.colorPrimary)

        activity_lessons_editor_add_lesson.setOnClickListener {
            startActivity<LessonEditorActivity>(
                SEMESTER_ID to semesterId,
                WEEKDAY to view_pager.currentItem + 1
            )
        }
    }

    override fun onStart() {
        super.onStart()
        ScheduleManager.addOnScheduleUpdateListener(this)
        onScheduleUpdate()
    }

    override fun onScheduleUpdate() {
        ScheduleManager.getSemesterOrNull(semesterId)?.let {
            val page = view_pager.currentItem
            view_pager.adapter = SchedulePagerAdapter(supportFragmentManager, it, true)
            view_pager.currentItem = page

            // TODO: 13.11.2016 добавить заполнение списка пар по датам
        } ?: finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_lessons_editor, menu)
        menu.setColor(getCompatColor(R.color.action_bar_icons_color))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_lessons_editor_edit_semester -> startActivity<SemesterEditorActivity>(
                SEMESTER_ID to semesterId
            )
            R.id.menu_lessons_editor_delete_semester -> {
                fun remove() {
                    ScheduleManager.removeSemester(semesterId)
                    finish()
                }

                alert(R.string.activity_lessons_editor_alert_delete_message) {
                    positiveButton(R.string.activity_lessons_editor_alert_delete_yes) { remove() }
                    negativeButton(R.string.activity_lessons_editor_alert_delete_no) {}
                }.show()
            }
            else -> throw IllegalArgumentException("Неизвестный id: ${item.itemId}")
        }
        return super.onOptionsItemSelected(item)
    }
}
