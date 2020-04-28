package ru.erdenian.studentassistant.ui.main

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.ActivityMainBinding
import ru.erdenian.studentassistant.ui.adapter.SemestersSpinnerAdapter

class MainActivity : AppCompatActivity() {

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
            navController.addOnDestinationChangedListener { _, destination, _ -> title = destination.label }
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
}
