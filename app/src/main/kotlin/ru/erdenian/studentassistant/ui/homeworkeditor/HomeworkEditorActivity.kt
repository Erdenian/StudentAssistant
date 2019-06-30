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
import ru.erdenian.studentassistant.repository.entity.Homework
import ru.erdenian.studentassistant.repository.entity.Lesson
import ru.erdenian.studentassistant.ui.homeworkeditor.HomeworkEditorViewModel.Error
import ru.erdenian.studentassistant.utils.distinctUntilChanged
import ru.erdenian.studentassistant.utils.getCompatColor
import ru.erdenian.studentassistant.utils.lazyViewModel
import ru.erdenian.studentassistant.utils.setColor
import ru.erdenian.studentassistant.utils.showDatePicker

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

    private val viewModel by lazyViewModel<HomeworkEditorViewModel>()

    private val homework by lazy { intent.getParcelableExtra<Homework>(HOMEWORK_INTENT_KEY) }

    @Suppress("ComplexMethod")
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

        val owner = this

        findViewById<Spinner>(R.id.ahe_subject_name).apply {
            viewModel.existingSubjects.observe(owner) { subjects ->
                val selection = selectedItem as String?
                adapter = ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_spinner_item,
                    subjects.toTypedArray()
                ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
                (selection ?: viewModel.subjectName.safeValue)?.let { subjectName ->
                    setSelection(subjects.list.indexOf(subjectName))
                }
            }

            viewModel.subjectName.distinctUntilChanged { value ->
                value == selectedItem as String?
            }.observe(owner) { subjectName ->
                viewModel.existingSubjects.safeValue?.run { setSelection(list.indexOf(subjectName)) }
            }

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.subjectName.value = selectedItem as String
                }
            }
        }

        findViewById<EditText>(R.id.ahe_description).apply {
            viewModel.description.distinctUntilChanged { value ->
                value == text?.toString() ?: ""
            }.observe(owner) { setText(it) }
            addTextChangedListener { viewModel.description.value = it?.toString() ?: "" }
        }

        findViewById<Button>(R.id.ahe_deadline).apply {
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
                            R.string.hea_error_empty_description
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
            alert(R.string.hea_delete_message) {
                positiveButton(R.string.hea_delete_yes) {
                    viewModel.viewModelScope.launch {
                        viewModel.delete()
                        finish()
                    }
                }
                negativeButton(R.string.hea_delete_no) {}
            }.show()
            true
        }
        else -> false
    }
}
