package ru.erdenian.studentassistant.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_lessons_editor.*
import kotlinx.android.synthetic.main.scroll_view.*
import kotlinx.android.synthetic.main.toolbar_with_spinner.*
import kotlinx.android.synthetic.main.view_pager.*
import org.jetbrains.anko.toast
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.adapter.SchedulePagerAdapter
import ru.erdenian.studentassistant.extensions.getAnyExtra
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.putExtra
import ru.erdenian.studentassistant.extensions.setColor
import ru.erdenian.studentassistant.schedule.OnScheduleUpdateListener
import ru.erdenian.studentassistant.schedule.ScheduleManager
import ru.erdenian.studentassistant.schedule.Semester

class LessonsEditorActivity : AppCompatActivity(),
        View.OnClickListener,
        AdapterView.OnItemSelectedListener,
        OnScheduleUpdateListener {

    companion object {
        const val SEMESTER = "semester"
    }

    private val semester: Semester by lazy { intent.getAnyExtra(SEMESTER) as Semester }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lessons_editor)

        setSupportActionBar(toolbar_with_spinner)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)


        val adapter = ArrayAdapter(this, R.layout.spinner_item_semesters,
                resources.getStringArray(R.array.activity_lessons_editor_edit_types))
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_semesters)
        toolbar_with_spinner_spinner.adapter = adapter
        toolbar_with_spinner_spinner.onItemSelectedListener = this

        view_pager_pager_tab_strip.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        view_pager_pager_tab_strip.setTabIndicatorColorResource(R.color.colorPrimary)

        activity_lessons_editor_add_lesson.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        ScheduleManager.setOnScheduleUpdateListener(this)
        onScheduleUpdate()
    }

    override fun onScheduleUpdate() {
        view_pager.adapter = SchedulePagerAdapter(supportFragmentManager, semester, true)

        // TODO: 13.11.2016 добавить заполнение списка пар по датам
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_lessons_editor, menu)
        menu.setColor(getCompatColor(R.color.action_bar_icons_color))
        return true
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
        view_pager.visibility = if (i == 0) View.VISIBLE else View.GONE
        scroll_view.visibility = if (i == 1) View.VISIBLE else View.GONE
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_lessons_editor_edit_semester -> {
                with(Intent(this, SemesterEditorActivity::class.java)) {
                    putExtra(SemesterEditorActivity.SEMESTER, semester)
                    startActivity(this)
                }
            }
            R.id.menu_lessons_editor_delete_semester -> ScheduleManager.removeSemester(semester.id)
            else -> throw IllegalArgumentException("Неизвестный id: ${item.itemId}")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.activity_lessons_editor_add_lesson -> toast("Добавить пару")
            else -> throw IllegalArgumentException("Неизвестный id: ${v.id}")
        }
    }
}
