package ru.erdenian.studentassistant.ui.lessonseditor

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.lifecycle.viewModelScope
import androidx.viewpager.widget.PagerTabStrip
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.lazyViewModel
import ru.erdenian.studentassistant.extensions.setColor
import ru.erdenian.studentassistant.repository.entity.SemesterNew
import ru.erdenian.studentassistant.ui.lessoneditor.LessonEditorActivity
import ru.erdenian.studentassistant.ui.semestereditor.SemesterEditorActivity

class LessonsEditorActivity : AppCompatActivity() {

    companion object {
        const val SEMESTER_INTENT_KEY = "semester_intent_key"
    }

    private val viewModel by lazyViewModel<LessonsEditorViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lessons_editor)

        viewModel.semesterId.value = intent.getParcelableExtra<SemesterNew>(SEMESTER_INTENT_KEY).id

        setSupportActionBar(findViewById(R.id.alse_toolbar))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        findViewById<Spinner>(R.id.alse_spinner).apply {
            adapter = ArrayAdapter(
                context, R.layout.spinner_item_semesters,
                resources.getStringArray(R.array.lesson_repeat_types)
            ).apply { setDropDownViewResource(R.layout.spinner_dropdown_item_semesters) }

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                private val flipper = this@LessonsEditorActivity.findViewById<ViewFlipper>(
                    R.id.alse_flipper
                )

                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    flipper.displayedChild = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        }

        val byWeekdaysPager = findViewById<ViewPager>(R.id.alse_by_weekdays_pager).apply {
            adapter = LessonsEditorPagerAdapter(supportFragmentManager).apply {
                viewModel.semester.observe(this@LessonsEditorActivity) { semester = it }
            }
        }
        findViewById<PagerTabStrip>(R.id.alse_by_weekdays_pager_tab_strip).apply {
            setTextColor(getCompatColor(R.color.colorPrimary))
            setTabIndicatorColorResource(R.color.colorPrimary)
        }

        // TODO: 13.11.2016 добавить заполнение списка пар по датам

        findViewById<FloatingActionButton>(R.id.alse_add_lesson).setOnClickListener {
            startActivity<LessonEditorActivity>(
                LessonEditorActivity.SEMESTER_ID_INTENT_KEY to viewModel.semester.value?.id,
                LessonEditorActivity.WEEKDAY_INTENT_KEY to byWeekdaysPager.currentItem + 1
            )
        }

        viewModel.semester.observe(this) { if (it == null) finish() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_lessons_editor, menu)
        menu.setColor(getCompatColor(R.color.action_bar_icons_color))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.menu_lessons_editor_edit_semester -> {
            startActivity<SemesterEditorActivity>(
                SemesterEditorActivity.SEMESTER_INTENT_KEY to viewModel.semester.value
            )
            true
        }
        R.id.menu_lessons_editor_delete_semester -> {
            alert(R.string.activity_lessons_editor_alert_delete_message) {
                positiveButton(R.string.activity_lessons_editor_alert_delete_yes) {
                    viewModel.viewModelScope.launch { viewModel.deleteSemester() }
                }
                negativeButton(R.string.activity_lessons_editor_alert_delete_no) {}
            }.show()
            true
        }
        else -> false
    }
}
