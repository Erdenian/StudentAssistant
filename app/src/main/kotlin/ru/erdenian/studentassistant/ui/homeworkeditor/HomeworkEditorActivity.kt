package ru.erdenian.studentassistant.ui.homeworkeditor

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.viewModelScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.ActivityHomeworkEditorBinding
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.ui.homeworkeditor.HomeworkEditorViewModel.Error
import ru.erdenian.studentassistant.uikit.ExposedDropdownMenu
import ru.erdenian.studentassistant.utils.distinctUntilChanged
import ru.erdenian.studentassistant.utils.getColorCompat
import ru.erdenian.studentassistant.utils.setColor
import ru.erdenian.studentassistant.utils.showDatePicker
import ru.erdenian.studentassistant.utils.startActivity
import ru.erdenian.studentassistant.utils.toast

class HomeworkEditorActivity : AppCompatActivity() {

    companion object {
        private const val SEMESTER_ID_INTENT_KEY = "semester_id_intent_key"
        private const val SUBJECT_NAME_INTENT_KEY = "subject_name_intent_key"
        private const val HOMEWORK_INTENT_KEY = "homework_intent_key"

        fun start(context: Context, semesterId: Long) {
            context.startActivity<HomeworkEditorActivity>(
                SEMESTER_ID_INTENT_KEY to semesterId
            )
        }

        fun start(context: Context, lesson: Lesson) {
            context.startActivity<HomeworkEditorActivity>(
                SEMESTER_ID_INTENT_KEY to lesson.semesterId,
                SUBJECT_NAME_INTENT_KEY to lesson.subjectName
            )
        }

        fun start(context: Context, homework: Homework) {
            context.startActivity<HomeworkEditorActivity>(
                SEMESTER_ID_INTENT_KEY to homework.semesterId,
                HOMEWORK_INTENT_KEY to homework
            )
        }

        private const val DATE_FORMAT = "dd.MM.yyyy"
    }

    private val viewModel by viewModels<HomeworkEditorViewModel>()

    private val homework by lazy { intent.getParcelableExtra<Homework>(HOMEWORK_INTENT_KEY) }

    @Suppress("ComplexMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHomeworkEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.apply {
            val homework = homework
            val semesterId = getLongExtra(SEMESTER_ID_INTENT_KEY, -1)
            if (homework == null) viewModel.init(
                semesterId, intent.getStringExtra(SUBJECT_NAME_INTENT_KEY)
            ) else viewModel.init(semesterId, homework)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val owner = this

        binding.subjectName.apply {
            val adapter = ExposedDropdownMenu.createAdapter(context).apply {
                viewModel.existingSubjects.observe(owner) { items = it.list }
            }
            setAdapter(adapter)

            onTextChangedListener = { text, _ -> viewModel.subjectName.value = text }

            viewModel.subjectName.distinctUntilChanged { value ->
                value == text?.toString() ?: ""
            }.observe(owner) { text = it }
        }

        binding.description.apply {
            viewModel.description.distinctUntilChanged { value ->
                value == text?.toString() ?: ""
            }.observe(owner) { setText(it) }
            addTextChangedListener { viewModel.description.value = it?.toString() ?: "" }
        }

        binding.deadline.apply {
            viewModel.deadline.observe(owner) { deadline ->
                text = deadline.toString(DATE_FORMAT)
            }
            setOnClickListener {
                showDatePicker(
                    viewModel.deadline.value,
                    LocalDate.now(),
                    viewModel.semesterLastDay.value
                ) { viewModel.deadline.value = it }
            }
        }

        viewModel.error.observe(owner) {}
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_homework_editor, menu)
        menu.findItem(R.id.mhe_delete).isVisible = (homework != null)
        menu.setColor(getColorCompat(R.color.menu))
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.mhe_save -> {
            viewModel.error.value?.let { error ->
                toast(
                    when (error) {
                        Error.EMPTY_DESCRIPTION -> R.string.hea_error_empty_description
                    }
                )
            } ?: run {
                viewModel.viewModelScope.launch { viewModel.save() }
                finish()
            }
            true
        }
        R.id.mhe_delete -> {
            MaterialAlertDialogBuilder(this)
                .setMessage(R.string.hea_delete_message)
                .setPositiveButton(R.string.hea_delete_yes) { _, _ ->
                    viewModel.viewModelScope.launch {
                        viewModel.delete()
                        finish()
                    }
                }
                .setNegativeButton(R.string.hea_delete_no, null)
                .show()
            true
        }
        else -> false
    }
}
