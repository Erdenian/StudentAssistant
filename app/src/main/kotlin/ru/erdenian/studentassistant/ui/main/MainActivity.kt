package ru.erdenian.studentassistant.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.ActivityMainBinding
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.ui.adapter.SemestersSpinnerAdapter
import ru.erdenian.studentassistant.ui.lessonseditor.LessonsEditorActivity
import ru.erdenian.studentassistant.ui.semestereditor.SemesterEditorActivity
import ru.erdenian.studentassistant.utils.getColorCompat
import ru.erdenian.studentassistant.utils.setColor

class MainActivity : AppCompatActivity() {

    companion object {
        private const val SEMESTER_EDITOR_REQUEST_CODE = 1
    }

    private val viewModel by viewModels<MainViewModel>()

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    @Suppress("LongMethod", "ComplexMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val owner = this
        val navController = findNavController(R.id.nav_host_fragment)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            val observer = Observer<Semester?> { title = it?.name ?: navController.currentDestination?.label }
            viewModel.selectedSemester.observe(owner, observer)
            navController.addOnDestinationChangedListener { _, _, _ -> observer.onChanged(viewModel.selectedSemester.value) }

            viewModel.allSemesters.observe(owner) { setDisplayShowTitleEnabled(it.size <= 1) }
        }

        binding.toolbarSpinner.apply {
            viewModel.allSemesters.observe(owner) { visibility = if (it.size > 1) View.VISIBLE else View.GONE }
            val adapter = SemestersSpinnerAdapter().apply { viewModel.allSemesters.observe(owner) { semesters = it.list } }
            this.adapter = adapter
            viewModel.selectedSemester.distinctUntilChanged().observe(owner) { semester ->
                viewModel.allSemesters.value?.indexOf(semester)?.takeIf { it >= 0 }?.let { setSelection(it) }
            }
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) =
                    viewModel.selectSemester(adapter.getItem(position))

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        }

        binding.navigationView.setupWithNavController(navController)

        viewModel.allSemesters.observe(this) { invalidateOptionsMenu() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.setColor(getColorCompat(R.color.menu))
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val isNotEmpty = viewModel.allSemesters.value?.isNotEmpty() ?: return false
        menu.findItem(R.id.mm_add_schedule).setShowAsAction(
            if (isNotEmpty) MenuItem.SHOW_AS_ACTION_NEVER else MenuItem.SHOW_AS_ACTION_IF_ROOM
        )
        menu.findItem(R.id.mm_edit_schedule).isVisible = isNotEmpty
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.mm_add_schedule -> {
            SemesterEditorActivity.start(this)
            true
        }
        R.id.mm_edit_schedule -> {
            LessonsEditorActivity.start(this, checkNotNull(viewModel.selectedSemester.value))
            true
        }
        else -> false
    }
}
