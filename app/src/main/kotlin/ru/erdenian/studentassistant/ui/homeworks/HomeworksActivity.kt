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
import org.jetbrains.anko.startActivity
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.compareAndSet
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.getViewModel
import ru.erdenian.studentassistant.extensions.initializeDrawerAndNavigationView
import ru.erdenian.studentassistant.repository.entity.SemesterNew
import ru.erdenian.studentassistant.ui.adapter.SemestersSpinnerAdapter
import ru.erdenian.studentassistant.ui.homeworkeditor.HomeworkEditorActivity

class HomeworksActivity : AppCompatActivity() {

    private val drawer by lazy { findViewById<DrawerLayout>(R.id.ah_drawer) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homeworks)

        val viewModel = getViewModel<HomeworksViewModel>()

        findViewById<Toolbar>(R.id.ah_toolbar).let { toolbar ->
            setSupportActionBar(toolbar)
            initializeDrawerAndNavigationView(toolbar, drawer)
        }
        supportActionBar?.apply {
            viewModel.selectedSemester.observe(this@HomeworksActivity) { title = it?.name }
            viewModel.allSemesters.observe(this@HomeworksActivity) { semesters ->
                setDisplayShowTitleEnabled(semesters.size <= 1)
            }
        }

        findViewById<Spinner>(R.id.ah_toolbar_spinner).apply {
            viewModel.selectedSemester.observe(this@HomeworksActivity) { semester ->
                setSelection(viewModel.allSemesters.value.indexOf(semester))
            }

            adapter = SemestersSpinnerAdapter().apply {
                viewModel.allSemesters.observe(this@HomeworksActivity) { semesters = it.list }
            }
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    viewModel.selectedSemester.compareAndSet(
                        parent.adapter.getItem(position) as SemesterNew
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        }

        findViewById<ViewPager>(R.id.ah_view_pager).apply {
            adapter = HomeworksPagerAdapter(context, supportFragmentManager).apply {
                viewModel.selectedSemester.observe(this@HomeworksActivity) { semester = it }
            }
        }

        findViewById<PagerTabStrip>(R.id.ah_pager_tab_strip).apply {
            setTextColor(getCompatColor(R.color.colorPrimary))
            setTabIndicatorColorResource(R.color.colorPrimary)
        }

        findViewById<FloatingActionButton>(R.id.ah_add_homework).setOnClickListener {
            startActivity<HomeworkEditorActivity>(
                HomeworkEditorActivity.SEMESTER_ID_INTENT_KEY to viewModel.selectedSemester.value?.id
            )
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }
}
