package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_schedule.content_schedule_add_schedule
import kotlinx.android.synthetic.main.activity_schedule.content_schedule_get_schedule_from_server
import kotlinx.android.synthetic.main.activity_schedule.drawer_layout
import kotlinx.android.synthetic.main.activity_schedule.schedule_flipper
import kotlinx.android.synthetic.main.toolbar_with_spinner.toolbar_with_spinner
import kotlinx.android.synthetic.main.toolbar_with_spinner.toolbar_with_spinner_spinner
import kotlinx.android.synthetic.main.view_pager.view_pager
import kotlinx.android.synthetic.main.view_pager.view_pager_pager_tab_strip
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.adapter.SchedulePagerAdapter
import ru.erdenian.studentassistant.adapter.SemestersAdapter
import ru.erdenian.studentassistant.extensions.defaultSemester
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.initializeDrawerAndNavigationView
import ru.erdenian.studentassistant.extensions.setColor
import ru.erdenian.studentassistant.extensions.showDatePicker
import ru.erdenian.studentassistant.repository.entity.SemesterNew
import ru.erdenian.studentassistant.schedule.Semester

class ScheduleActivity : AppCompatActivity() {

    companion object {
        private const val BUTTONS_INDEX = 0
        private const val SCHEDULE_INDEX = 1
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
    }

    private var selectedSemester: SemesterNew? = null
    private var isSemestersEmpty = false

    private var pagerAdapter: SchedulePagerAdapter? = null

    private val actionBar by lazy {
        supportActionBar ?: throw NullPointerException("supportActionBar == null")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        setSupportActionBar(toolbar_with_spinner)

        toolbar_with_spinner_spinner.onItemSelectedListener =
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

        viewModel.allSemesters.observe(this, Observer { semesters ->
            isSemestersEmpty = semesters.isEmpty()
            schedule_flipper.displayedChild = when (isSemestersEmpty) {
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

                    toolbar_with_spinner_spinner.adapter = SemestersAdapter(this, semesters)
                    toolbar_with_spinner_spinner.setSelection(selectedSemesterIndex)
                    toolbar_with_spinner_spinner.visibility = View.VISIBLE
                }
                (semesters.size == 1) -> {
                    actionBar.title = semesters[selectedSemesterIndex].name
                    actionBar.setDisplayShowTitleEnabled(true)

                    toolbar_with_spinner_spinner.visibility = View.GONE
                    toolbar_with_spinner_spinner.adapter = null

                    /*val adapter = SchedulePagerAdapter(supportFragmentManager, selectedSemester, false)
                    view_pager.adapter = adapter
                    view_pager.setCurrentItem(adapter.getPosition(LocalDate.now()), false)
                    pagerAdapter = adapter*/
                }
                else -> {
                    actionBar.setTitle(R.string.title_activity_schedule)
                    actionBar.setDisplayShowTitleEnabled(true)

                    toolbar_with_spinner_spinner.visibility = View.GONE
                    toolbar_with_spinner_spinner.adapter = null

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
