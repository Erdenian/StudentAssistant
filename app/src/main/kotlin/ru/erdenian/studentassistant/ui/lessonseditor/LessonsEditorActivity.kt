package ru.erdenian.studentassistant.ui.lessonseditor

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.ViewFlipper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import androidx.viewpager.widget.PagerTabStrip
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.model.entity.Semester
import ru.erdenian.studentassistant.ui.lessoneditor.LessonEditorActivity
import ru.erdenian.studentassistant.ui.semestereditor.SemesterEditorActivity
import ru.erdenian.studentassistant.utils.getColorCompat
import ru.erdenian.studentassistant.utils.requireViewByIdCompat
import ru.erdenian.studentassistant.utils.setColor

class LessonsEditorActivity : AppCompatActivity(R.layout.activity_lessons_editor) {

    companion object {
        private const val SEMESTER_INTENT_KEY = "semester_intent_key"
        fun start(context: Context, semester: Semester) {
            context.startActivity<LessonsEditorActivity>(SEMESTER_INTENT_KEY to semester)
        }
    }

    private val viewModel by viewModels<LessonsEditorViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.init(checkNotNull(intent.getParcelableExtra(SEMESTER_INTENT_KEY)))

        setSupportActionBar(requireViewByIdCompat(R.id.alse_toolbar))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        requireViewByIdCompat<Spinner>(R.id.alse_spinner).apply {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                private val flipper = this@LessonsEditorActivity.requireViewByIdCompat<ViewFlipper>(
                    R.id.alse_flipper
                )

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    flipper.displayedChild = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        }

        val byWeekdaysPager = requireViewByIdCompat<ViewPager>(R.id.alse_by_weekdays_pager).apply {
            adapter = LessonsEditorPagerAdapter(supportFragmentManager)
        }
        requireViewByIdCompat<PagerTabStrip>(R.id.alse_by_weekdays_pager_tab_strip).apply {
            setTextColor(getColorCompat(R.color.primary))
            setTabIndicatorColorResource(R.color.primary)
        }

        // TODO: 13.11.2016 добавить заполнение списка пар по датам

        requireViewByIdCompat<FloatingActionButton>(R.id.alse_add_lesson).setOnClickListener {
            viewModel.viewModelScope.launch {
                val weekday = byWeekdaysPager.currentItem + 1
                LessonEditorActivity.start(
                    this@LessonsEditorActivity,
                    checkNotNull(viewModel.semester.value).id,
                    viewModel.getNextStartTime(weekday),
                    weekday
                )
            }
        }

        viewModel.semester.observe(this) { if (it == null) finish() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_lessons_editor, menu)
        menu.setColor(getColorCompat(R.color.menu))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.mlse_edit_semester -> {
            SemesterEditorActivity.start(this, viewModel.semester.value)
            true
        }
        R.id.mlse_delete_semester -> {
            alert(R.string.lsea_delete_message) {
                positiveButton(R.string.lsea_delete_yes) {
                    viewModel.viewModelScope.launch { viewModel.deleteSemester() }
                }
                negativeButton(R.string.lsea_delete_no) {}
            }.show()
            true
        }
        else -> false
    }
}
