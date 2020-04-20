package ru.erdenian.studentassistant.ui.lessonseditor

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.ActivityLessonsEditorBinding
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.ui.lessoneditor.LessonEditorActivity
import ru.erdenian.studentassistant.ui.semestereditor.SemesterEditorActivity
import ru.erdenian.studentassistant.utils.colorAttr
import ru.erdenian.studentassistant.utils.getColorCompat
import ru.erdenian.studentassistant.utils.setColor
import ru.erdenian.studentassistant.utils.startActivity

class LessonsEditorActivity : AppCompatActivity() {

    companion object {
        private const val SEMESTER_INTENT_KEY = "semester_intent_key"
        fun start(context: Context, semester: Semester) {
            context.startActivity<LessonsEditorActivity>(SEMESTER_INTENT_KEY to semester)
        }
    }

    private val viewModel by viewModels<LessonsEditorViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLessonsEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.init(checkNotNull(intent.getParcelableExtra(SEMESTER_INTENT_KEY)))

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        binding.spinner.apply {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    binding.flipper.displayedChild = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        }

        val byWeekdaysPager = binding.byWeekdaysPager.apply {
            adapter = LessonsEditorPagerAdapter(supportFragmentManager)
        }
        binding.byWeekdaysPagerTabStrip.apply {
            val color = colorAttr(R.attr.colorPrimary)
            setTextColor(color)
            tabIndicatorColor = color
        }

        // TODO: 13.11.2016 добавить заполнение списка пар по датам

        binding.addLesson.setOnClickListener {
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
            MaterialAlertDialogBuilder(this)
                .setMessage(R.string.lsea_delete_message)
                .setPositiveButton(R.string.lsea_delete_yes) { _, _ ->
                    viewModel.viewModelScope.launch { viewModel.deleteSemester() }
                }
                .setNegativeButton(R.string.lsea_delete_no, null)
                .show()
            true
        }
        else -> false
    }
}
