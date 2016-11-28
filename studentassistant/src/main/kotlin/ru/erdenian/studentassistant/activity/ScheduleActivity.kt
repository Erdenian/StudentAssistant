package ru.erdenian.studentassistant.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSortedSet
import kotlinx.android.synthetic.main.activity_schedule.*
import kotlinx.android.synthetic.main.content_schedule.*
import kotlinx.android.synthetic.main.toolbar_with_spinner.*
import kotlinx.android.synthetic.main.view_pager.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.adapter.SchedulePagerAdapter
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.initializeDrawerAndNavigationView
import ru.erdenian.studentassistant.extensions.setColor
import ru.erdenian.studentassistant.extensions.showDatePicker
import ru.erdenian.studentassistant.schedule.Lesson
import ru.erdenian.studentassistant.schedule.OnScheduleUpdateListener
import ru.erdenian.studentassistant.schedule.ScheduleManager
import ru.erdenian.studentassistant.schedule.Semester

class ScheduleActivity : AppCompatActivity(),
        AdapterView.OnItemSelectedListener,
        CalendarDatePickerDialogFragment.OnDateSetListener,
        View.OnClickListener,
        OnScheduleUpdateListener {

    companion object {
        private const val CURRENT_PAGE = "current_page"
    }

    private var savedPage = -1
    private var selectedSemester: Semester? = null

    private var pagerAdapter: SchedulePagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        setSupportActionBar(toolbar_with_spinner)
        initializeDrawerAndNavigationView(toolbar_with_spinner)

        toolbar_with_spinner_spinner.onItemSelectedListener = this

        content_schedule_get_schedule_from_server.setOnClickListener(this)
        content_schedule_add_schedule.setOnClickListener(this)

        view_pager_pager_tab_strip.setTextColor(getCompatColor(R.color.colorPrimary))
        view_pager_pager_tab_strip.setTabIndicatorColorResource(R.color.colorPrimary)
    }

    override fun onStart() {
        super.onStart()
        ScheduleManager.setOnScheduleUpdateListener(this)
        onScheduleUpdate()
    }

    override fun onScheduleUpdate() {
        supportActionBar!!.setDisplayShowTitleEnabled(ScheduleManager.semesters.size <= 1)
        toolbar_with_spinner_spinner.visibility = if (ScheduleManager.semesters.size > 1) View.VISIBLE else View.GONE

        view_pager.visibility = if (ScheduleManager.semesters.isNotEmpty()) View.VISIBLE else View.GONE
        content_schedule_add_buttons!!.visibility = if (ScheduleManager.semesters.isEmpty()) View.VISIBLE else View.GONE

        invalidateOptionsMenu()

        if ((pagerAdapter != null) && (selectedSemester!!.id == ScheduleManager.selectedSemester?.id)) {
            savedPage = view_pager.currentItem
        }
        selectedSemester = ScheduleManager.selectedSemester

        if (ScheduleManager.semesters.size > 1) {
            val adapter = ArrayAdapter(this, R.layout.spinner_item_semesters, ScheduleManager.semestersNames)
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_semesters)
            toolbar_with_spinner_spinner.adapter = adapter
            toolbar_with_spinner_spinner.setSelection(ScheduleManager.selectedSemesterIndex!!)
        } else if (ScheduleManager.semesters.size == 1) {
            supportActionBar!!.title = selectedSemester!!.name
            onItemSelected(null, null, 0, 0)
        } else {
            supportActionBar!!.setTitle(R.string.title_activity_schedule)
            pagerAdapter = null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(CURRENT_PAGE, view_pager.currentItem)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        savedPage = savedInstanceState.getInt(CURRENT_PAGE, -1)
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_schedule, menu)
        menu.findItem(R.id.menu_schedule_calendar).isVisible = !ScheduleManager.semesters.isEmpty()
        menu.findItem(R.id.menu_schedule_edit_schedule).isVisible = !ScheduleManager.semesters.isEmpty()
        menu.setColor(getCompatColor(R.color.action_bar_icons_color))
        return true
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        ScheduleManager.selectedSemesterIndex = position
        selectedSemester = ScheduleManager.selectedSemester

        pagerAdapter = SchedulePagerAdapter(supportFragmentManager, selectedSemester!!, false)
        view_pager.adapter = pagerAdapter
        view_pager.setCurrentItem(if (savedPage != -1) savedPage else pagerAdapter!!.getPosition(LocalDate.now()), false)
        savedPage = -1
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_schedule_calendar -> showDatePicker(this, selectedSemester!!.firstDay, selectedSemester!!.lastDay,
                    pagerAdapter!!.getDate(view_pager.currentItem))
            R.id.menu_schedule_add_schedule -> startActivity<SemesterEditorActivity>()
            R.id.menu_schedule_edit_schedule -> with(Intent(this, LessonsEditorActivity::class.java)) {
                putExtra(LessonsEditorActivity.SEMESTER_ID, selectedSemester!!.id)
                startActivity(this)
            }
            else -> throw IllegalArgumentException("Неизвестный id: ${item.itemId}")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDateSet(dialog: CalendarDatePickerDialogFragment, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        view_pager.currentItem = pagerAdapter!!.getPosition(LocalDate(year, monthOfYear + 1, dayOfMonth))
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.content_schedule_get_schedule_from_server -> toast(R.string.activity_schedule_get_schedule_from_server_button)
            R.id.content_schedule_add_schedule -> {
                val lessons = ImmutableSortedSet.of(
                        Lesson("Конструирование ПО", "Лабораторная работа",
                                ImmutableSortedSet.of("Федоров Алексей Роальдович", "Федоров Петр Алексеевич"),
                                ImmutableSortedSet.of("4212а"),
                                LocalTime(14, 20), LocalTime(15, 50),
                                Lesson.RepeatType.BY_WEEKDAY, 5, ImmutableList.of(false, true), null, System.nanoTime()),
                        Lesson("Конструирование ПО", "Лабораторная работа",
                                ImmutableSortedSet.of("Федоров Алексей Роальдович"), ImmutableSortedSet.of("4212а"),
                                LocalTime(18, 10), LocalTime(19, 40),
                                Lesson.RepeatType.BY_WEEKDAY, 5, ImmutableList.of(false, true), null, System.nanoTime()))

                ScheduleManager.addSemester(Semester("Семестр 5", LocalDate(2016, 9, 1), LocalDate(2016, 12, 31),
                        lessons))
            }
            else -> throw IllegalArgumentException("Неизвестный id: ${v.id}")
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
