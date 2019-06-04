package ru.erdenian.studentassistant.ui.semestereditor

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.observe
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.compareAndSet
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.lazyViewModel
import ru.erdenian.studentassistant.extensions.setColor
import ru.erdenian.studentassistant.extensions.showDatePicker
import ru.erdenian.studentassistant.repository.entity.SemesterNew
import ru.erdenian.studentassistant.ui.semestereditor.SemesterEditorViewModel.Error

class SemesterEditorActivity : AppCompatActivity() {

    companion object {
        const val SEMESTER_INTENT_KEY = "semester"
        private const val DATE_FORMAT = "dd.MM.yyyy"
    }

    private val viewModel by lazyViewModel<SemesterEditorViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_semester_editor)

        val semester = intent.getParcelableExtra<SemesterNew?>(
            SEMESTER_INTENT_KEY
        )?.also { viewModel.setSemester(it) }

        setSupportActionBar(findViewById(R.id.ase_toolbar))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            if (semester == null) title = getString(
                R.string.title_activity_semester_editor_new_semester
            )
        }

        findViewById<TextInputLayout>(R.id.ase_name).apply {
            viewModel.error.observe(this@SemesterEditorActivity) { error ->
                when (error) {
                    Error.SEMESTER_EXISTS -> this.error = getString(
                        R.string.activity_semester_editor_error_name_not_avaliable
                    )
                    else -> isErrorEnabled = false
                }
            }
        }

        findViewById<TextInputEditText>(R.id.ase_name_edit_text).apply {
            viewModel.name.observe(this@SemesterEditorActivity) { setText(it) }
            addTextChangedListener { viewModel.name.compareAndSet(it.toString()) }
        }

        findViewById<Button>(R.id.ase_first_day).apply {
            viewModel.firstDay.observe(this@SemesterEditorActivity) { firstDay ->
                text = firstDay.toString(DATE_FORMAT)
            }
            setOnClickListener { showDatePicker { viewModel.firstDay.value = it } }
        }

        findViewById<Button>(R.id.ase_last_day).apply {
            viewModel.lastDay.observe(this@SemesterEditorActivity) { lastDay ->
                text = lastDay.toString(DATE_FORMAT)
            }
            setOnClickListener { showDatePicker { viewModel.lastDay.value = it } }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_semester_editor, menu)
        menu.setColor(getCompatColor(R.color.action_bar_icons_color))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.menu_semester_editor_save -> {
            viewModel.error.value?.let { error ->
                toast(
                    when (error) {
                        Error.SEMESTER_EXISTS -> R.string.activity_semester_editor_error_name_not_avaliable
                        Error.WRONG_DATES -> R.string.activity_semester_editor_incorrect_dates
                    }
                )
            } ?: run {
                viewModel.viewModelScope.launch {
                    viewModel.save()
                    finish()
                }
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
