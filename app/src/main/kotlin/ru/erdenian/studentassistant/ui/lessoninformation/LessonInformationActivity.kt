package ru.erdenian.studentassistant.ui.lessoninformation

import android.content.Context
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.ActivityLessonInformationBinding
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.ui.adapter.HomeworksListAdapter
import ru.erdenian.studentassistant.ui.adapter.SpacingItemDecoration
import ru.erdenian.studentassistant.ui.homeworkeditor.HomeworkEditorActivity
import ru.erdenian.studentassistant.ui.lessoneditor.LessonEditorActivity
import ru.erdenian.studentassistant.utils.getColorCompat
import ru.erdenian.studentassistant.utils.setColor
import ru.erdenian.studentassistant.utils.startActivity

class LessonInformationActivity : AppCompatActivity() {

    companion object {
        private const val LESSON_INTENT_KEY = "lesson_intent_key"
        fun start(context: Context, lesson: Lesson) {
            context.startActivity<LessonInformationActivity>(LESSON_INTENT_KEY to lesson)
        }

        private const val TIME_FORMAT = "HH:mm"
    }

    private val viewModel by viewModels<LessonInformationViewModel>()
    private val homeworksAdapter by lazy {
        HomeworksListAdapter().apply {
            onHomeworkClickListener = { HomeworkEditorActivity.start(this@LessonInformationActivity, it) }
            viewModel.homeworks.observe(this@LessonInformationActivity) { homeworks = it.list }
        }
    }

    @Suppress("ComplexMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLessonInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val owner = this

        viewModel.init(checkNotNull(intent.getParcelableExtra(LESSON_INTENT_KEY)))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.content.subjectName.apply {
            viewModel.lesson.observe(owner) { text = it?.subjectName }
        }

        binding.content.startTime.apply {
            viewModel.lesson.observe(owner) { text = it?.startTime?.toString(TIME_FORMAT) }
        }

        binding.content.endTime.apply {
            viewModel.lesson.observe(owner) { text = it?.endTime?.toString(TIME_FORMAT) }
        }

        binding.content.type.apply {
            viewModel.lesson.observe(owner) { text = it?.type }
        }

        binding.content.homeworksFlipper.apply {
            val noHomeworksIndex = 0
            val containsHomeworksIndex = 1
            viewModel.homeworks.observe(owner) { homeworks ->
                displayedChild = if (homeworks.isEmpty()) noHomeworksIndex else containsHomeworksIndex
            }
        }

        binding.content.homeworks.apply {
            adapter = homeworksAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(SpacingItemDecoration(context.resources.getDimensionPixelSize(R.dimen.cards_spacing)))
            registerForContextMenu(this)
        }

        binding.addHomework.setOnClickListener {
            HomeworkEditorActivity.start(this, checkNotNull(viewModel.lesson.value))
        }

        viewModel.lesson.observe(this) { if (it == null) finish() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_lesson_information, menu)
        menu.setColor(getColorCompat(R.color.menu))
        return true
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        menuInflater.inflate(R.menu.context_homeworks, menu)
        @Suppress("UnsafeCast")
        (menuInfo as AdapterView.AdapterContextMenuInfo?)?.run {
            menu.setHeaderTitle(homeworksAdapter.homeworks[position].subjectName)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        @Suppress("UnsafeCast")
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val homework = homeworksAdapter.homeworks[info.position]
        return when (item.itemId) {
            R.id.ch_delete -> {
                MaterialAlertDialogBuilder(this)
                    .setMessage(R.string.lia_delete_message)
                    .setPositiveButton(R.string.lia_delete_yes) { _, _ -> viewModel.deleteHomework(homework.id) }
                    .setNegativeButton(R.string.lia_delete_no, null)
                    .show()
                true
            }
            else -> false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.mli_edit -> {
            LessonEditorActivity.start(this, checkNotNull(viewModel.lesson.value))
            true
        }
        else -> false
    }
}
