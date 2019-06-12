package ru.erdenian.studentassistant.ui.lessoninformation

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.jetbrains.anko.startActivity
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.lazyViewModel
import ru.erdenian.studentassistant.extensions.setColor
import ru.erdenian.studentassistant.ui.adapter.HomeworksListAdapter
import ru.erdenian.studentassistant.ui.homeworkeditor.HomeworkEditorActivity
import ru.erdenian.studentassistant.ui.lessoneditor.LessonEditorActivity

class LessonInformationActivity : AppCompatActivity() {

    companion object {
        const val LESSON_INTENT_KEY = "lesson_intent_key"

        private const val TIME_FORMAT = "HH:mm"
    }

    private val viewModel by lazyViewModel<LessonInformationViewModel>()

    @Suppress("ComplexMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_information)

        viewModel.init(intent.getParcelableExtra(LESSON_INTENT_KEY))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<TextView>(R.id.ali_subject_name).apply {
            viewModel.lesson.observe(this@LessonInformationActivity) { text = it?.subjectName }
        }

        findViewById<TextView>(R.id.ali_start_time).apply {
            viewModel.lesson.observe(this@LessonInformationActivity) { lesson ->
                text = lesson?.startTime?.toString(TIME_FORMAT)
            }
        }

        findViewById<TextView>(R.id.ali_end_time).apply {
            viewModel.lesson.observe(this@LessonInformationActivity) { lesson ->
                text = lesson?.endTime?.toString(TIME_FORMAT)
            }
        }

        findViewById<TextView>(R.id.ali_type).apply {
            viewModel.lesson.observe(this@LessonInformationActivity) { text = it?.type }
        }

        findViewById<ViewFlipper>(R.id.ali_homeworks_flipper).apply {
            val noHomeworksIndex = 0
            val containsHomeworksIndex = 1
            viewModel.homeworks.observe(this@LessonInformationActivity) { homeworks ->
                displayedChild =
                    if (homeworks.isEmpty()) noHomeworksIndex
                    else containsHomeworksIndex
            }
        }

        findViewById<RecyclerView>(R.id.ali_homeworks).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = HomeworksListAdapter().apply {
                viewModel.homeworks.observe(this@LessonInformationActivity) { homeworks ->
                    this.homeworks = homeworks.list
                }
            }
        }

        findViewById<FloatingActionButton>(R.id.ali_add_homework).setOnClickListener {
            startActivity<HomeworkEditorActivity>(
                HomeworkEditorActivity.SEMESTER_ID_INTENT_KEY to viewModel.lesson.value?.semesterId,
                HomeworkEditorActivity.SUBJECT_NAME_INTENT_KEY to viewModel.lesson.value?.subjectName
            )
        }

        viewModel.lesson.observe(this) { if (it == null) finish() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_lesson_information, menu)
        menu.setColor(getCompatColor(R.color.action_bar_icons_color))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.menu_lesson_information_edit_button -> {
            startActivity<LessonEditorActivity>(
                LessonEditorActivity.LESSON_INTENT_KEY to viewModel.lesson
            )
            true
        }
        else -> false
    }
}
