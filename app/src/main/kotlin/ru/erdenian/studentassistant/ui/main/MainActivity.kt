package ru.erdenian.studentassistant.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.model.entity.Semester
import ru.erdenian.studentassistant.ui.adapter.SemestersSpinnerAdapter
import ru.erdenian.studentassistant.ui.help.HelpActivity
import ru.erdenian.studentassistant.ui.lessonseditor.LessonsEditorActivity
import ru.erdenian.studentassistant.ui.semestereditor.SemesterEditorActivity
import ru.erdenian.studentassistant.utils.getColorCompat
import ru.erdenian.studentassistant.utils.lazyViewModel
import ru.erdenian.studentassistant.utils.requireViewByIdCompat
import ru.erdenian.studentassistant.utils.setColor

class MainActivity : AppCompatActivity() {

    private val viewModel by lazyViewModel<MainViewModel>()

    private val drawer by lazy { requireViewByIdCompat<DrawerLayout>(R.id.am_drawer) }

    @Suppress("LongMethod", "ComplexMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val owner = this

        requireViewByIdCompat<Toolbar>(R.id.am_toolbar).also { toolbar ->
            setSupportActionBar(toolbar)

            val navigationView = requireViewByIdCompat<NavigationView>(R.id.am_navigation_view)
            val navController = findNavController(R.id.am_nav_host_fragment)

            ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
            ).apply {
                drawer.addDrawerListener(this)
                syncState()
            }

            viewModel.selectedSemester.observe(owner) { semester ->
                navigationView.menu.findItem(R.id.dm_homeworks).isEnabled = (semester != null)
                navigationView.menu.findItem(R.id.dm_alarm).isEnabled = (semester != null)
            }

            navigationView.setNavigationItemSelectedListener { menuItem ->
                drawer.closeDrawer(GravityCompat.START)
                when (menuItem.itemId) {
                    R.id.dm_schedule -> {
                        navController.navigate(
                            if (viewModel.selectedSemester.value != null)
                                R.id.action_global_scheduleFragment
                            else R.id.action_global_noScheduleFragment
                        )
                        true
                    }
                    R.id.dm_homeworks -> {
                        navController.navigate(
                            if (viewModel.hasLessons.value == true) {
                                R.id.action_global_homeworksFragment
                            } else R.id.action_global_noLessonsFragment
                        )
                        true
                    }
                    R.id.dm_alarm -> {
                        toast("AlarmEditorActivity")
                        true
                    }
                    R.id.dm_settings -> {
                        toast(R.string.dm_settings)
                        true
                    }
                    R.id.dm_help -> {
                        startActivity<HelpActivity>()
                        true
                    }
                    else -> false
                }
            }

            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.noScheduleFragment, R.id.scheduleFragment ->
                        navigationView.setCheckedItem(R.id.dm_schedule)
                    R.id.homeworksFragment -> navigationView.setCheckedItem(R.id.dm_homeworks)
                }
            }

            viewModel.selectedSemester.observe(owner) { selectedSemester ->
                val currentId = navController.currentDestination?.id
                if ((currentId == R.id.noScheduleFragment) && (selectedSemester != null)) {
                    navController.navigate(R.id.action_global_scheduleFragment)
                } else if ((currentId != R.id.noScheduleFragment) && (selectedSemester == null)) {
                    navController.navigate(R.id.noScheduleFragment)
                }
            }

            viewModel.hasLessons.observe(owner) { hasLessons ->
                val currentId = navController.currentDestination?.id
                if ((currentId == R.id.noLessonsFragment) && (hasLessons == true)) {
                    navController.navigate(R.id.action_global_homeworksFragment)
                } else if ((currentId == R.id.homeworksFragment) && (hasLessons == false)) {
                    navController.navigate(R.id.action_global_noLessonsFragment)
                }
            }
        }
        supportActionBar?.apply {
            viewModel.selectedSemester.observe(owner) { semester ->
                title = semester?.name ?: getString(R.string.nsf_title)
            }
            viewModel.allSemesters.observe(owner) { semesters ->
                setDisplayShowTitleEnabled(semesters.size <= 1)
            }
        }

        requireViewByIdCompat<Spinner>(R.id.am_toolbar_spinner).apply {
            viewModel.allSemesters.observe(owner) { semesters ->
                visibility = if (semesters.size > 1) View.VISIBLE else View.GONE
            }
            viewModel.selectedSemester.distinctUntilChanged().observe(owner) { semester ->
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
                    viewModel.selectedSemester.value = parent.adapter.getItem(position) as Semester
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        }

        viewModel.allSemesters.observe(this) { invalidateOptionsMenu() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.setColor(getColorCompat(R.color.action_bar_icons_color))
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val isNotEmpty = viewModel.allSemesters.safeValue?.isNotEmpty() ?: return false
        menu.findItem(R.id.mm_edit_schedule).isVisible = isNotEmpty
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.mm_add_schedule -> {
            startActivity<SemesterEditorActivity>()
            true
        }
        R.id.mm_edit_schedule -> {
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
