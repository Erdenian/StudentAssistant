package ru.erdenian.studentassistant.ui.lessoninformation

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.jetbrains.anko.dimen
import org.jetbrains.anko.startActivity
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.model.entity.Homework
import ru.erdenian.studentassistant.model.entity.Lesson
import ru.erdenian.studentassistant.ui.adapter.HomeworksListAdapter
import ru.erdenian.studentassistant.ui.adapter.SpacingItemDecoration
import ru.erdenian.studentassistant.ui.homeworkeditor.HomeworkEditorActivity
import ru.erdenian.studentassistant.ui.lessoneditor.LessonEditorActivity
import ru.erdenian.studentassistant.utils.getColorCompat
import ru.erdenian.studentassistant.utils.lazyViewModel
import ru.erdenian.studentassistant.utils.requireViewByIdCompat
import ru.erdenian.studentassistant.utils.setColor

class LessonInformationActivity : AppCompatActivity() {

    companion object {
        private const val LESSON_INTENT_KEY = "lesson_intent_key"
        fun start(context: Context, lesson: Lesson) {
            context.startActivity<LessonInformationActivity>(LESSON_INTENT_KEY to lesson)
        }

        private const val TIME_FORMAT = "HH:mm"
    }

    private val viewModel by lazyViewModel<LessonInformationViewModel>()

    @Suppress("ComplexMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_information)

        val owner = this

        viewModel.init(intent.getParcelableExtra(LESSON_INTENT_KEY))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        requireViewByIdCompat<TextView>(R.id.ali_subject_name).apply {
            viewModel.lesson.observe(owner) { text = it?.subjectName }
        }

        requireViewByIdCompat<TextView>(R.id.ali_start_time).apply {
            viewModel.lesson.observe(owner) { lesson ->
                text = lesson?.startTime?.toString(TIME_FORMAT)
            }
        }

        requireViewByIdCompat<TextView>(R.id.ali_end_time).apply {
            viewModel.lesson.observe(owner) { lesson ->
                text = lesson?.endTime?.toString(TIME_FORMAT)
            }
        }

        requireViewByIdCompat<TextView>(R.id.ali_type).apply {
            viewModel.lesson.observe(owner) { text = it?.type }
        }

        requireViewByIdCompat<ViewFlipper>(R.id.ali_homeworks_flipper).apply {
            val noHomeworksIndex = 0
            val containsHomeworksIndex = 1
            viewModel.homeworks.observe(owner) { homeworks ->
                displayedChild =
                    if (homeworks.isEmpty()) noHomeworksIndex
                    else containsHomeworksIndex
            }
        }

        requireViewByIdCompat<RecyclerView>(R.id.ali_homeworks).apply {
            adapter = HomeworksListAdapter().apply {
                onHomeworkClickListener = object : HomeworksListAdapter.OnHomeworkClickListener {
                    override fun onHomeworkClick(homework: Homework) {
                        HomeworkEditorActivity.start(context, homework)
                    }
                }
                viewModel.homeworks.observe(owner) { homeworks ->
                    this.homeworks = homeworks.list
                }
            }
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(SpacingItemDecoration(dimen(R.dimen.cards_spacing)))
        }

        requireViewByIdCompat<FloatingActionButton>(R.id.ali_add_homework).setOnClickListener {
            HomeworkEditorActivity.start(this, checkNotNull(viewModel.lesson.value))
        }

        viewModel.lesson.observe(this) { if (it == null) finish() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_lesson_information, menu)
        menu.setColor(getColorCompat(R.color.menu))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.mli_edit -> {
            LessonEditorActivity.start(
                this,
                checkNotNull(viewModel.lesson.value)
            )
            true
        }
        else -> false
    }
}
