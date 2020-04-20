package ru.erdenian.studentassistant.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.findNavController
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.ActivityMainBinding
import ru.erdenian.studentassistant.ui.adapter.SemestersSpinnerAdapter
import ru.erdenian.studentassistant.ui.help.HelpActivity
import ru.erdenian.studentassistant.ui.lessonseditor.LessonsEditorActivity
import ru.erdenian.studentassistant.ui.semestereditor.SemesterEditorActivity
import ru.erdenian.studentassistant.utils.getColorCompat
import ru.erdenian.studentassistant.utils.setColor
import ru.erdenian.studentassistant.utils.startActivity

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

        binding.toolbar.also { toolbar ->
            setSupportActionBar(toolbar)

            val navController = findNavController(R.id.nav_host_fragment)

            viewModel.selectedSemester.observe(owner) { semester ->
                binding.navigationView.menu.run {
                    findItem(R.id.bm_homeworks).isEnabled = (semester != null)
                }
            }

            binding.navigationView.setOnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.bm_schedule -> {
                        navController.navigate(
                            if (viewModel.selectedSemester.value != null) {
                                R.id.nav_action_schedule
                            } else R.id.nav_action_no_schedule
                        )
                        true
                    }
                    R.id.bm_homeworks -> {
                        navController.navigate(
                            if (viewModel.hasLessons.value == true) {
                                R.id.nav_action_homeworks
                            } else R.id.nav_action_no_lessons
                        )
                        true
                    }
                    R.id.bm_help -> {
                        startActivity<HelpActivity>()
                        true
                    }
                    else -> false
                }
            }

            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.nav_fragment_no_schedule, R.id.nav_fragment_schedule ->
                        binding.navigationView.selectedItemId = R.id.bm_schedule
                    R.id.nav_fragment_homeworks ->
                        binding.navigationView.selectedItemId = R.id.bm_homeworks
                }
            }

            viewModel.selectedSemester.observe(owner) { selectedSemester ->
                val id = navController.currentDestination?.id
                if ((id == R.id.nav_fragment_no_schedule) && (selectedSemester != null)) {
                    navController.navigate(R.id.nav_action_schedule)
                } else if ((id != R.id.nav_fragment_no_schedule) && (selectedSemester == null)) {
                    navController.navigate(R.id.nav_action_no_schedule)
                }
            }

            viewModel.hasLessons.observe(owner) { hasLessons ->
                val currentId = navController.currentDestination?.id
                if ((currentId == R.id.nav_fragment_no_lessons) && (hasLessons == true)) {
                    navController.navigate(R.id.nav_action_homeworks)
                } else if ((currentId == R.id.nav_fragment_homeworks) && (hasLessons == false)) {
                    navController.navigate(R.id.nav_action_no_lessons)
                }
            }
        }
        supportActionBar?.apply {
            viewModel.selectedSemester.observe(owner) { semester ->
                title = semester?.name ?: getString(R.string.nsf_title)
            }
            viewModel.allSemesters.observe(owner) { semesters ->
                setDisplayShowTitleEnabled(semesters.size <= 1)
            }
        }

        binding.toolbarSpinner.apply {
            viewModel.allSemesters.observe(owner) { semesters ->
                visibility = if (semesters.size > 1) View.VISIBLE else View.GONE
            }
            val adapter = SemestersSpinnerAdapter().apply {
                viewModel.allSemesters.observe(owner) { semesters = it.list }
            }
            this.adapter = adapter
            viewModel.selectedSemester.distinctUntilChanged().observe(owner) { semester ->
                viewModel.allSemesters.value.indexOf(semester)
                    .takeIf { it >= 0 }
                    ?.let { setSelection(it) }
            }
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.selectSemester(adapter.getItem(position))
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        }

        viewModel.allSemesters.observe(this) { invalidateOptionsMenu() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.setColor(getColorCompat(R.color.menu))
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val isNotEmpty = viewModel.allSemesters.safeValue?.isNotEmpty() ?: return false
        menu.findItem(R.id.mm_edit_schedule).isVisible = isNotEmpty
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.mm_add_schedule -> {
            SemesterEditorActivity.startForResult(this, SEMESTER_EDITOR_REQUEST_CODE)
            true
        }
        R.id.mm_edit_schedule -> {
            LessonsEditorActivity.start(this, checkNotNull(viewModel.selectedSemester.value))
            true
        }
        else -> false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if ((requestCode == SEMESTER_EDITOR_REQUEST_CODE) && (resultCode == RESULT_OK)) {
            viewModel.selectSemester(
                requireNotNull(
                    data?.getParcelableExtra(SemesterEditorActivity.SEMESTER_RESULT_EXTRA)
                )
            )
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
