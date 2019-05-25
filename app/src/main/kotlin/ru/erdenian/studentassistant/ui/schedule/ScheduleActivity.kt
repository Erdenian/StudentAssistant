package ru.erdenian.studentassistant.ui.schedule

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.lifecycle.observe
import androidx.viewpager.widget.PagerTabStrip
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.view_pager.view_pager
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.activity.LessonsEditorActivity
import ru.erdenian.studentassistant.activity.SemesterEditorActivity
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.initializeDrawerAndNavigationView
import ru.erdenian.studentassistant.extensions.setColor
import ru.erdenian.studentassistant.extensions.showDatePicker
import ru.erdenian.studentassistant.repository.entity.SemesterNew
import ru.erdenian.studentassistant.ui.adapter.SemestersSpinnerAdapter

class ScheduleActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProviders.of(this).get<ScheduleViewModel>() }

    private val drawer by lazy { findViewById<DrawerLayout>(R.id.as_drawer) }
    private val pagerAdapter by lazy { SchedulePagerAdapter(supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        findViewById<Toolbar>(R.id.as_toolbar).let { toolbar ->
            setSupportActionBar(toolbar)
            initializeDrawerAndNavigationView(toolbar)
        }
        val actionBar = checkNotNull(supportActionBar)

        val spinner = findViewById<Spinner>(R.id.as_toolbar_spinner)
        val spinnerAdapter = SemestersSpinnerAdapter()
        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                viewModel.selectedSemester.apply {
                    val semester = parent.adapter.getItem(position) as SemesterNew
                    if (value != semester) value = semester
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        val flipper = findViewById<ViewFlipper>(R.id.as_flipper)
        val flipperButtonsIndex = 0
        val flipperScheduleIndex = 1

        findViewById<Button>(R.id.as_download_schedule).setOnClickListener {
            toast(R.string.content_schedule_get_schedule_from_server_button)
        }
        findViewById<Button>(R.id.as_create_schedule).setOnClickListener {
            startActivity<SemesterEditorActivity>()
        }

        findViewById<ViewPager>(R.id.as_view_pager).adapter = pagerAdapter
        findViewById<PagerTabStrip>(R.id.as_pager_tab_strip).apply {
            setTextColor(getCompatColor(R.color.colorPrimary))
            setTabIndicatorColorResource(R.color.colorPrimary)
        }

        viewModel.allSemesters.observe(this) { semesters ->
            when (semesters.size) {
                0 -> {
                    actionBar.setTitle(R.string.title_activity_schedule)
                    actionBar.setDisplayShowTitleEnabled(true)
                    spinner.visibility = View.GONE
                }
                1 -> {
                    actionBar.title = viewModel.selectedSemester.value?.name
                    actionBar.setDisplayShowTitleEnabled(true)
                    spinner.visibility = View.GONE
                }
                else -> {
                    actionBar.title = null
                    actionBar.setDisplayShowTitleEnabled(false)
                    spinner.visibility = View.VISIBLE
                }
            }
            spinnerAdapter.semesters = semesters.list
            flipper.displayedChild =
                if (semesters.isNotEmpty()) flipperScheduleIndex else flipperButtonsIndex
            invalidateOptionsMenu()
        }

        viewModel.selectedSemester.observe(this) { semester ->
            spinner.setSelection(viewModel.allSemesters.value.indexOf(semester))
            pagerAdapter.semester = semester
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_schedule, menu)
        menu.setColor(getCompatColor(R.color.action_bar_icons_color))
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val isNotEmpty = viewModel.allSemesters.value.isNotEmpty()
        menu.findItem(R.id.menu_schedule_calendar).isVisible = isNotEmpty
        menu.findItem(R.id.menu_schedule_edit_schedule).isVisible = isNotEmpty
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.menu_schedule_calendar -> {
            viewModel.selectedSemester.value?.run {
                showDatePicker(
                    pagerAdapter.getDate(view_pager.currentItem), firstDay, lastDay
                ) { pagerAdapter.run { view_pager.currentItem = getPosition(it) } }
            }
            true
        }
        R.id.menu_schedule_add_schedule -> {
            startActivity<SemesterEditorActivity>(
                SemesterEditorActivity.SEMESTER_INTENT_KEY to viewModel.selectedSemester.value
            )
            true
        }
        R.id.menu_schedule_edit_schedule -> {
            startActivity<LessonsEditorActivity>(
                LessonsEditorActivity.SEMESTER_INTENT_KEY to viewModel.selectedSemester.value
            )
            true
        }
        else -> false
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }
}
