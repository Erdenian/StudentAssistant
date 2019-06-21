package ru.erdenian.studentassistant.ui.homeworks

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.observe
import androidx.viewpager.widget.PagerTabStrip
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.distinctUntilChanged
import ru.erdenian.studentassistant.extensions.getViewModel
import ru.erdenian.studentassistant.extensions.initializeDrawerAndNavigationView
import ru.erdenian.studentassistant.repository.entity.Semester
import ru.erdenian.studentassistant.ui.adapter.SemestersSpinnerAdapter
import ru.erdenian.studentassistant.ui.homeworkeditor.HomeworkEditorActivity
import ru.erdenian.studentassistant.utils.getCompatColor

class HomeworksActivity : AppCompatActivity() {

    private val drawer by lazy { findViewById<DrawerLayout>(R.id.ah_drawer) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homeworks)

        val viewModel = getViewModel<HomeworksViewModel>()
        val owner = this

        findViewById<Toolbar>(R.id.ah_toolbar).let { toolbar ->
            setSupportActionBar(toolbar)
            initializeDrawerAndNavigationView(toolbar, drawer)
        }
        supportActionBar?.apply {
            viewModel.selectedSemester.observe(owner) { title = it?.name }
            viewModel.allSemesters.observe(owner) { semesters ->
                setDisplayShowTitleEnabled(semesters.size <= 1)
            }
        }

        findViewById<Spinner>(R.id.ah_toolbar_spinner).apply {
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

        findViewById<ViewPager>(R.id.ah_view_pager).apply {
            adapter = HomeworksPagerAdapter(context, supportFragmentManager).apply {
                viewModel.selectedSemester.observe(owner) { semester = it }
            }
        }

        findViewById<PagerTabStrip>(R.id.ah_pager_tab_strip).apply {
            setTextColor(getCompatColor(R.color.colorPrimary))
            setTabIndicatorColorResource(R.color.colorPrimary)
        }

        findViewById<FloatingActionButton>(R.id.ah_add_homework).setOnClickListener {
            HomeworkEditorActivity.start(
                this,
                checkNotNull(viewModel.selectedSemester.value).id
            )
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }
}
