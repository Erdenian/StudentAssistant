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
import androidx.lifecycle.observe
import androidx.viewpager.widget.PagerTabStrip
import androidx.viewpager.widget.ViewPager
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.compareAndSet
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.initializeDrawerAndNavigationView
import ru.erdenian.studentassistant.extensions.lazyViewModel
import ru.erdenian.studentassistant.extensions.setColor
import ru.erdenian.studentassistant.extensions.showDatePicker
import ru.erdenian.studentassistant.repository.entity.SemesterNew
import ru.erdenian.studentassistant.ui.adapter.SemestersSpinnerAdapter
import ru.erdenian.studentassistant.ui.lessonseditor.LessonsEditorActivity
import ru.erdenian.studentassistant.ui.semestereditor.SemesterEditorActivity

class ScheduleActivity : AppCompatActivity() {

    private val viewModel by lazyViewModel<ScheduleViewModel>()

    private val drawer by lazy { findViewById<DrawerLayout>(R.id.as_drawer) }
    private val pager by lazy { findViewById<ViewPager>(R.id.as_view_pager) }
    private val pagerAdapter by lazy {
        SchedulePagerAdapter(supportFragmentManager).apply {
            viewModel.selectedSemester.observe(this@ScheduleActivity) { semester = it }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        findViewById<Toolbar>(R.id.as_toolbar).let { toolbar ->
            setSupportActionBar(toolbar)
            initializeDrawerAndNavigationView(toolbar)
        }
        supportActionBar?.apply {
            viewModel.allSemesters.observe(this@ScheduleActivity) { semesters ->
                title = when (semesters.size) {
                    0 -> getText(R.string.title_activity_schedule)
                    1 -> viewModel.selectedSemester.value?.name
                    else -> null
                }
                setDisplayShowTitleEnabled(semesters.size <= 1)
            }
        }

        findViewById<Spinner>(R.id.as_toolbar_spinner).apply {
            viewModel.selectedSemester.observe(this@ScheduleActivity) { semester ->
                setSelection(viewModel.allSemesters.value.indexOf(semester))
            }

            adapter = SemestersSpinnerAdapter().apply {
                viewModel.allSemesters.observe(this@ScheduleActivity) { semesters = it.list }
            }
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    viewModel.selectedSemester.compareAndSet(
                        parent.adapter.getItem(position) as SemesterNew
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        }

        findViewById<ViewFlipper>(R.id.as_flipper).apply {
            val buttonsIndex = 0
            val scheduleIndex = 1
            viewModel.allSemesters.observe(this@ScheduleActivity) { semesters ->
                displayedChild = if (semesters.isNotEmpty()) buttonsIndex else scheduleIndex
            }
        }

        findViewById<Button>(R.id.as_download_schedule).setOnClickListener {
            toast(R.string.content_schedule_get_schedule_from_server_button)
        }
        findViewById<Button>(R.id.as_create_schedule).setOnClickListener {
            startActivity<SemesterEditorActivity>()
        }

        pager.adapter = pagerAdapter
        findViewById<PagerTabStrip>(R.id.as_pager_tab_strip).apply {
            setTextColor(getCompatColor(R.color.colorPrimary))
            setTabIndicatorColorResource(R.color.colorPrimary)
        }

        viewModel.allSemesters.observe(this) { invalidateOptionsMenu() }
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
                    pagerAdapter.getDate(pager.currentItem), firstDay, lastDay
                ) { pager.currentItem = pagerAdapter.getPosition(it) }
            }
            true
        }
        R.id.menu_schedule_add_schedule -> {
            startActivity<SemesterEditorActivity>()
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
