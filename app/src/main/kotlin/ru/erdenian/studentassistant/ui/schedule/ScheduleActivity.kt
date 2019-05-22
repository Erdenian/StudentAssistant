package ru.erdenian.studentassistant.ui.schedule

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import kotlinx.android.synthetic.main.activity_schedule.drawer_layout
import kotlinx.android.synthetic.main.view_pager.view_pager
import kotlinx.android.synthetic.main.view_pager.view_pager_pager_tab_strip
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.activity.LessonsEditorActivity
import ru.erdenian.studentassistant.activity.SEMESTER_ID
import ru.erdenian.studentassistant.activity.SemesterEditorActivity
import ru.erdenian.studentassistant.adapter.SchedulePagerAdapter
import ru.erdenian.studentassistant.adapter.SemestersAdapter
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.initializeDrawerAndNavigationView
import ru.erdenian.studentassistant.extensions.setColor
import ru.erdenian.studentassistant.extensions.showDatePicker
import ru.erdenian.studentassistant.schedule.Semester

class ScheduleActivity : AppCompatActivity() {

    companion object {
        private const val BUTTONS_INDEX = 0
        private const val SCHEDULE_INDEX = 1
    }

    private val viewModel by lazy { ViewModelProviders.of(this).get<ScheduleViewModel>() }


    private val spinner by lazy { findViewById<Spinner>(R.id.as_toolbar_spinner) }


    private var pagerAdapter: SchedulePagerAdapter? = null

    private val actionBar by lazy {
        supportActionBar ?: throw NullPointerException("supportActionBar == null")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        setSupportActionBar(findViewById(R.id.as_toolbar))

        spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    val semester = parent.adapter.getItem(position) as Semester
                    val adapter = SchedulePagerAdapter(supportFragmentManager, semester, false)
                    view_pager.adapter = adapter
                    view_pager.setCurrentItem(adapter.getPosition(LocalDate.now()), false)

                    //selectedSemester = semester
                    pagerAdapter = adapter
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }

        content_schedule_get_schedule_from_server.setOnClickListener {
            toast(R.string.content_schedule_get_schedule_from_server_button)
        }
        content_schedule_add_schedule.setOnClickListener {
            startActivity<SemesterEditorActivity>()
        }

        view_pager_pager_tab_strip.setTextColor(getCompatColor(R.color.colorPrimary))
        view_pager_pager_tab_strip.setTabIndicatorColorResource(R.color.colorPrimary)

        val flipper = findViewById<ViewFlipper>(R.id.as_flipper)
        viewModel.allSemesters.observe(this, Observer { semesters ->
            isSemestersEmpty = semesters.isEmpty()
            flipper.displayedChild = when (isSemestersEmpty) {
                true -> BUTTONS_INDEX
                false -> SCHEDULE_INDEX
            }

            val selectedSemesterIndex = semesters.run {
                val index = semesters.indexOfFirst { it.id == selectedSemester?.id }
                if (index == -1) {
                    selectedSemester = semesters.defaultSemester
                    semesters.indexOf(selectedSemester)
                } else index
            }

            when {
                (semesters.size > 1) -> {
                    actionBar.setDisplayShowTitleEnabled(false)
                    actionBar.title = null

                    spinner.adapter = SemestersAdapter(this, semesters)
                    spinner.setSelection(selectedSemesterIndex)
                    spinner.visibility = View.VISIBLE
                }
                (semesters.size == 1) -> {
                    actionBar.title = semesters[selectedSemesterIndex].name
                    actionBar.setDisplayShowTitleEnabled(true)

                    spinner.visibility = View.GONE
                    spinner.adapter = null

                    /*val adapter = SchedulePagerAdapter(supportFragmentManager, selectedSemester, false)
                    view_pager.adapter = adapter
                    view_pager.setCurrentItem(adapter.getPosition(LocalDate.now()), false)
                    pagerAdapter = adapter*/
                }
                else -> {
                    actionBar.setTitle(R.string.title_activity_schedule)
                    actionBar.setDisplayShowTitleEnabled(true)

                    spinner.visibility = View.GONE
                    spinner.adapter = null

                    pagerAdapter = null
                    view_pager.adapter = null
                }
            }

            invalidateOptionsMenu()
            initializeDrawerAndNavigationView(toolbar_with_spinner)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_schedule, menu)
        menu.setColor(getCompatColor(R.color.action_bar_icons_color))
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.menu_schedule_calendar).isVisible = !isSemestersEmpty
        menu.findItem(R.id.menu_schedule_edit_schedule).isVisible = !isSemestersEmpty
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_schedule_calendar -> selectedSemester?.let {
                showDatePicker(
                    pagerAdapter?.getDate(view_pager.currentItem),
                    it.firstDay,
                    it.lastDay
                ) { newDate ->
                    pagerAdapter?.run { view_pager.currentItem = getPosition(newDate) }
                }
            }
            R.id.menu_schedule_add_schedule -> startActivity<SemesterEditorActivity>(
                SemesterEditorActivity.SEMESTERS_NAMES_INTENT_KEY to viewModel.semestersNames.toTypedArray()
            )
            R.id.menu_schedule_edit_schedule -> startActivity<LessonsEditorActivity>(
                SEMESTER_ID to selectedSemester?.id
            )
            else -> throw IllegalArgumentException("Неизвестный id: ${item.itemId}")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) drawer_layout.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }
}
