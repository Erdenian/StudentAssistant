package ru.erdenian.studentassistant.ui.semestereditor

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import org.joda.time.format.DateTimeFormat
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.ActivitySemesterEditorBinding
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.ui.semestereditor.SemesterEditorViewModel.Error
import ru.erdenian.studentassistant.utils.distinctUntilChanged
import ru.erdenian.studentassistant.utils.getColorCompat
import ru.erdenian.studentassistant.utils.setColor
import ru.erdenian.studentassistant.utils.showDatePicker
import ru.erdenian.studentassistant.utils.startActivity
import ru.erdenian.studentassistant.utils.toast

class SemesterEditorActivity : AppCompatActivity() {

    companion object {
        private const val SEMESTER_INTENT_KEY = "semester_intent_key"
        fun start(context: Context, semester: Semester? = null) {
            context.startActivity<SemesterEditorActivity>(SEMESTER_INTENT_KEY to semester)
        }

        private const val DATE_FORMAT = "dd.MM.yyyy"
    }

    private val viewModel by viewModels<SemesterEditorViewModel>()

    @Suppress("ComplexMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySemesterEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val semester = intent.getParcelableExtra<Semester?>(SEMESTER_INTENT_KEY)
        viewModel.init(semester)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            if (semester == null) title = getString(R.string.sea_title_new)
        }

        val owner = this

        binding.nameLayout.apply {
            viewModel.error.observe(owner) { error ->
                when (error) {
                    Error.EMPTY_NAME -> this.error = getString(R.string.sea_error_empty_name)
                    Error.SEMESTER_EXISTS -> this.error = getString(R.string.sea_error_name_not_available)
                    else -> isErrorEnabled = false
                }
            }
        }

        binding.name.apply {
            viewModel.name
                .distinctUntilChanged { it == text?.toString() ?: "" }
                .observe(owner) { setText(it) }
            addTextChangedListener { viewModel.name.value = it?.toString() ?: "" }
        }

        val dateFormatter = DateTimeFormat.forPattern(DATE_FORMAT)

        binding.firstDay.apply {
            viewModel.firstDay.observe(owner) { text = it.toString(dateFormatter) }
            setOnClickListener { showDatePicker(viewModel.firstDay.value) { viewModel.firstDay.value = it } }
        }

        binding.lastDay.apply {
            viewModel.lastDay.observe(owner) { text = it.toString(dateFormatter) }
            setOnClickListener { showDatePicker(viewModel.lastDay.value) { viewModel.lastDay.value = it } }
        }

        viewModel.saved.observe(owner) { if (it) finish() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_semester_editor, menu)
        menu.setColor(getColorCompat(R.color.menu))
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
            } ?: viewModel.save()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
