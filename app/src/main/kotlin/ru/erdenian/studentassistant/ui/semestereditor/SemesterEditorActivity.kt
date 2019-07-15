package ru.erdenian.studentassistant.ui.semestereditor

import android.content.Context
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
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.model.entity.Semester
import ru.erdenian.studentassistant.ui.semestereditor.SemesterEditorViewModel.Error
import ru.erdenian.studentassistant.utils.distinctUntilChanged
import ru.erdenian.studentassistant.utils.getColorCompat
import ru.erdenian.studentassistant.utils.lazyViewModel
import ru.erdenian.studentassistant.utils.requireViewByIdCompat
import ru.erdenian.studentassistant.utils.setColor
import ru.erdenian.studentassistant.utils.showDatePicker

class SemesterEditorActivity : AppCompatActivity() {

    companion object {
        private const val SEMESTER_INTENT_KEY = "semester_intent_key"
        fun start(context: Context, semester: Semester? = null) {
            context.startActivity<SemesterEditorActivity>(SEMESTER_INTENT_KEY to semester)
        }

        private const val DATE_FORMAT = "dd.MM.yyyy"
    }

    private val viewModel by lazyViewModel<SemesterEditorViewModel>()

    @Suppress("ComplexMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_semester_editor)

        val semester = intent.getParcelableExtra<Semester?>(SEMESTER_INTENT_KEY)
        viewModel.init(semester)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            if (semester == null) title = getString(
                R.string.sea_title_new
            )
        }

        val owner = this

        requireViewByIdCompat<TextInputLayout>(R.id.ase_name).apply {
            viewModel.error.observe(owner) { error ->
                when (error) {
                    Error.EMPTY_NAME -> this.error = getString(
                        R.string.sea_error_empty_name
                    )
                    Error.SEMESTER_EXISTS -> this.error = getString(
                        R.string.sea_error_name_not_available
                    )
                    else -> isErrorEnabled = false
                }
            }
        }

        requireViewByIdCompat<TextInputEditText>(R.id.ase_name_edit_text).apply {
            viewModel.name.distinctUntilChanged { value ->
                value == text?.toString() ?: ""
            }.observe(owner) { setText(it) }
            addTextChangedListener { viewModel.name.value = it?.toString() ?: "" }
        }

        requireViewByIdCompat<Button>(R.id.ase_first_day).apply {
            viewModel.firstDay.observe(owner) { text = it.toString(DATE_FORMAT) }
            setOnClickListener {
                showDatePicker(viewModel.firstDay.value) { viewModel.firstDay.value = it }
            }
        }

        requireViewByIdCompat<Button>(R.id.ase_last_day).apply {
            viewModel.lastDay.observe(owner) { text = it.toString(DATE_FORMAT) }
            setOnClickListener {
                showDatePicker(viewModel.lastDay.value) { viewModel.lastDay.value = it }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_semester_editor, menu)
        menu.setColor(getColorCompat(R.color.action_bar_icons_color))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.mse_save -> {
            viewModel.error.value?.let { error ->
                toast(
                    when (error) {
                        Error.EMPTY_NAME -> R.string.sea_error_empty_name
                        Error.SEMESTER_EXISTS -> R.string.sea_error_name_not_available
                        Error.WRONG_DATES -> R.string.sea_error_wrong_dates
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
