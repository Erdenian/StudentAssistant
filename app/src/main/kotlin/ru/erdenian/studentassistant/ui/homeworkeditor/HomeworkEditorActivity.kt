package ru.erdenian.studentassistant.ui.homeworkeditor

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.observe
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.compareAndSet
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.lazyViewModel
import ru.erdenian.studentassistant.extensions.setColor
import ru.erdenian.studentassistant.extensions.showDatePicker
import ru.erdenian.studentassistant.repository.entity.HomeworkNew
import ru.erdenian.studentassistant.repository.entity.LessonNew
import ru.erdenian.studentassistant.ui.homeworkeditor.HomeworkEditorViewModel.Error

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

        fun start(context: Context, lesson: LessonNew) {
            context.startActivity<HomeworkEditorActivity>(
                SEMESTER_ID_INTENT_KEY to lesson.semesterId,
                SUBJECT_NAME_INTENT_KEY to lesson.subjectName
            )
        }

        fun start(context: Context, homework: HomeworkNew) {
            context.startActivity<HomeworkEditorActivity>(
                HOMEWORK_INTENT_KEY to homework
            )
        }

        private const val DATE_FORMAT = "dd.MM.yyyy"
    }

    private val viewModel by lazyViewModel<HomeworkEditorViewModel>()

    private val homework by lazy { intent.getParcelableExtra<HomeworkNew>(HOMEWORK_INTENT_KEY) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homework_editor)

        intent.apply {
            val homework = homework
            val semesterId = getLongExtra(SEMESTER_ID_INTENT_KEY, -1)
            if (homework == null) viewModel.init(
                semesterId, intent.getStringExtra(SUBJECT_NAME_INTENT_KEY)
            ) else viewModel.init(semesterId, homework)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<Spinner>(R.id.ahe_subject_name).apply {
            viewModel.existingSubjects.observe(this@HomeworkEditorActivity) { subjects ->
                adapter = ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_spinner_item,
                    subjects.toTypedArray()
                ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
            }

            viewModel.subjectName.observe(this@HomeworkEditorActivity) { subjectName ->
                setSelection(viewModel.existingSubjects.value.list.indexOf(subjectName))
            }

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.subjectName.compareAndSet(
                        viewModel.existingSubjects.value.list[position]
                    )
                }
            }
        }

        findViewById<EditText>(R.id.ahe_description).apply {
            viewModel.description.observe(this@HomeworkEditorActivity) { setText(it) }
            addTextChangedListener { viewModel.description.compareAndSet(it?.toString() ?: "") }
        }

        findViewById<Button>(R.id.ahe_deadline).apply {
            viewModel.deadline.observe(this@HomeworkEditorActivity) { deadline ->
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
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_homework_editor, menu)
        menu.findItem(R.id.menu_homework_editor_delete_homework).isVisible = (homework != null)
        menu.setColor(getCompatColor(R.color.action_bar_icons_color))
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.menu_lesson_editor_save -> {
            viewModel.error.value?.let { error ->
                toast(
                    when (error) {
                        Error.EMPTY_DESCRIPTION -> {
                            R.string.content_homework_editor_activity_null_description
                        }
                    }
                )
            } ?: run {
                viewModel.viewModelScope.launch { viewModel.save() }
                finish()
            }
            true
        }
        R.id.menu_homework_editor_delete_homework -> {
            alert(R.string.activity_homework_editor_alert_delete_message) {
                positiveButton(R.string.activity_homework_editor_alert_delete_yes) {
                    viewModel.viewModelScope.launch {
                        viewModel.delete()
                        finish()
                    }
                }
                negativeButton(R.string.activity_homework_editor_alert_delete_no) {}
            }.show()
            true
        }
        else -> false
    }
}
