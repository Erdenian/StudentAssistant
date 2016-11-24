package ru.erdenian.studentassistant.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.common.collect.ImmutableSortedSet
import kotlinx.android.synthetic.main.activity_semesters_editor.*
import kotlinx.android.synthetic.main.content_semesters_editor.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivity
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.getAnyExtra
import ru.erdenian.studentassistant.extensions.putExtra
import ru.erdenian.studentassistant.extensions.scrollPosition
import ru.erdenian.studentassistant.schedule.*
import java.util.*

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
            putExtra(LessonsEditorActivity.SEMESTER, ScheduleManager[position])
            startActivity(this)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.activity_semesters_editor_add_semester -> {
                val semesters = ArrayList(ScheduleManager.semesters.asList())
                semesters.add(Semester("Семестр " + System.currentTimeMillis(), LocalDate(2017, 9, 1), LocalDate(2017, 12, 31),
                        ImmutableSortedSet.of<Lesson>(), ImmutableSortedSet.of<Homework>(), System.nanoTime()))
                ScheduleManager.semesters = ImmutableSortedSet.copyOf(semesters)
            }
            else -> throw IllegalArgumentException("Неизвестный id: ${v.id}")
        }
    }
}
