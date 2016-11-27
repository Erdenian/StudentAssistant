package ru.erdenian.studentassistant.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_semesters_editor.*
import kotlinx.android.synthetic.main.content_semesters_editor.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivity
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.scrollPosition
import ru.erdenian.studentassistant.schedule.OnScheduleUpdateListener
import ru.erdenian.studentassistant.schedule.ScheduleManager

class SemestersEditorActivity : AppCompatActivity(),
        AdapterView.OnItemClickListener,
        OnScheduleUpdateListener,
        View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_semesters_editor)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        content_semesters_editor_semesters_list.onItemClickListener = this
        activity_semesters_editor_add_semester.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        ScheduleManager.setOnScheduleUpdateListener(this)
        onScheduleUpdate()
    }

    override fun onScheduleUpdate() {
        val scrollPosition = content_semesters_editor_semesters_list.scrollPosition
        content_semesters_editor_semesters_list.adapter =
                ArrayAdapter(this, android.R.layout.simple_list_item_1, ScheduleManager.semestersNames)
        content_semesters_editor_semesters_list.scrollPosition = scrollPosition
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> throw IllegalArgumentException("Неизвестный id: ${item.itemId}")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        with(Intent(this, LessonsEditorActivity::class.java)) {
            putExtra(LessonsEditorActivity.SEMESTER_ID, ScheduleManager[position].id)
            startActivity(this)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.activity_semesters_editor_add_semester -> {
                startActivity<SemesterEditorActivity>()
            }
            else -> throw IllegalArgumentException("Неизвестный id: ${v.id}")
        }
    }
}
