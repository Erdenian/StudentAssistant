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
import ru.erdenian.studentassistant.extensions.initializeDrawerAndNavigationView
import ru.erdenian.studentassistant.repository.entity.Semester
import ru.erdenian.studentassistant.ui.adapter.SemestersSpinnerAdapter
import ru.erdenian.studentassistant.ui.lessonseditor.LessonsEditorActivity
import ru.erdenian.studentassistant.ui.semestereditor.SemesterEditorActivity
import ru.erdenian.studentassistant.utils.distinctUntilChanged
import ru.erdenian.studentassistant.utils.getCompatColor
import ru.erdenian.studentassistant.utils.lazyViewModel
import ru.erdenian.studentassistant.utils.setColor
import ru.erdenian.studentassistant.utils.showDatePicker

class ScheduleActivity : AppCompatActivity() {

    private val viewModel by lazyViewModel<ScheduleViewModel>()

    private val drawer by lazy { findViewById<DrawerLayout>(R.id.as_drawer) }
    private val pager by lazy { findViewById<ViewPager>(R.id.as_view_pager) }
    private val pagerAdapter by lazy {
        SchedulePagerAdapter(supportFragmentManager).apply {
            viewModel.selectedSemester.observe(this@ScheduleActivity) { semester = it }
        }
    }

    @Suppress("ComplexMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        val owner = this

        findViewById<Toolbar>(R.id.as_toolbar).let { toolbar ->
            setSupportActionBar(toolbar)
            initializeDrawerAndNavigationView(toolbar, drawer)
        }
        supportActionBar?.apply {
            viewModel.selectedSemester.observe(owner) { semester ->
                title = semester?.name ?: getString(R.string.sa_title)
            }
            viewModel.allSemesters.observe(owner) { semesters ->
                setDisplayShowTitleEnabled(semesters.size <= 1)
            }
        }

        findViewById<Spinner>(R.id.as_toolbar_spinner).apply {
            viewModel.allSemesters.observe(owner) { semesters ->
                visibility = if (semesters.size > 1) View.VISIBLE else View.GONE
            }
            viewModel.selectedSemester.distinctUntilChanged { value ->
                value == selectedItem as Semester?
            }.observe(owner) { semester ->
                setSelection(viewModel.allSemesters.value.indexOf(semester))
            }

            adapter = SemestersSpinnerAdapter().apply {
                viewModel.allSemesters.observe(owner) { semesters = it.list }
            }
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    viewModel.selectedSemester.value =
                        parent.adapter.getItem(position) as Semester
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        }

        findViewById<ViewFlipper>(R.id.as_flipper).apply {
            val buttonsIndex = 0
            val scheduleIndex = 1
            viewModel.allSemesters.observe(owner) { semesters ->
                displayedChild = if (semesters.isNotEmpty()) scheduleIndex else buttonsIndex
            }
        }

        findViewById<Button>(R.id.as_download_schedule).setOnClickListener {
            toast(R.string.sa_download)
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
        val isNotEmpty = viewModel.allSemesters.safeValue?.isNotEmpty() ?: return false
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
            LessonsEditorActivity.start(this, checkNotNull(viewModel.selectedSemester.value))
            true
        }
        else -> false
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }
}
