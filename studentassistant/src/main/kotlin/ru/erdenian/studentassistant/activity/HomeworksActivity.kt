package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_homeworks.*
import kotlinx.android.synthetic.main.toolbar_with_spinner.*
import kotlinx.android.synthetic.main.view_pager.*
import org.jetbrains.anko.startActivity
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.adapter.HomeworksPagerAdapter
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.initializeDrawerAndNavigationView
import ru.erdenian.studentassistant.schedule.OnScheduleUpdateListener
import ru.erdenian.studentassistant.schedule.ScheduleManager

class HomeworksActivity : AppCompatActivity(),
        OnScheduleUpdateListener, AdapterView.OnItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homeworks)

        setSupportActionBar(toolbar_with_spinner)
        initializeDrawerAndNavigationView(toolbar_with_spinner)

        toolbar_with_spinner_spinner.onItemSelectedListener = this

        view_pager_pager_tab_strip.setTextColor(getCompatColor(R.color.colorPrimary))
        view_pager_pager_tab_strip.setTabIndicatorColorResource(R.color.colorPrimary)

        activity_homeworks_add_homework.setOnClickListener {
            startActivity<HomeworkEditorActivity>(SEMESTER_ID to ScheduleManager.selectedSemesterId!!)
        }
    }

    override fun onStart() {
        super.onStart()
        ScheduleManager.addOnScheduleUpdateListener(this)
        onScheduleUpdate()
    }

    override fun onScheduleUpdate() {
        supportActionBar!!.setDisplayShowTitleEnabled(ScheduleManager.semesters.size <= 1)
        toolbar_with_spinner_spinner.visibility = if (ScheduleManager.semesters.size > 1) View.VISIBLE else View.GONE

        if (ScheduleManager.semesters.size > 1) {
            val adapter = ArrayAdapter(this, R.layout.spinner_item_semesters, ScheduleManager.semestersNames)
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_semesters)
            toolbar_with_spinner_spinner.adapter = adapter
            toolbar_with_spinner_spinner.setSelection(ScheduleManager.selectedSemesterIndex)
        } else if (ScheduleManager.semesters.size == 1) {
            supportActionBar!!.title = ScheduleManager.selectedSemester!!.name
            onItemSelected(null, null, 0, 0)
        } else {
            supportActionBar!!.setTitle(R.string.title_activity_schedule)
            view_pager.adapter = null
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        ScheduleManager.selectedSemesterId = ScheduleManager.semesters.asList()[position].id

        view_pager.adapter = HomeworksPagerAdapter(supportFragmentManager, ScheduleManager.selectedSemesterId!!)
        view_pager.currentItem = 0
    }

    override fun onNothingSelected(parent: AdapterView<*>) = Unit
}
