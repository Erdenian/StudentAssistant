package ru.erdenian.studentassistant.ui.main.lessonseditor

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.FragmentLessonsEditorBinding
import ru.erdenian.studentassistant.utils.colorAttr
import ru.erdenian.studentassistant.utils.getColorCompat
import ru.erdenian.studentassistant.utils.navArgsFactory
import ru.erdenian.studentassistant.utils.setColor

class LessonsEditorFragment : Fragment(R.layout.fragment_lessons_editor) {

    private val viewModel by viewModels<LessonsEditorViewModel> {
        navArgsFactory<LessonsEditorFragmentArgs> { LessonsEditorViewModel(it, semester) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentLessonsEditorBinding.bind(view)
        val owner = viewLifecycleOwner
        setHasOptionsMenu(true)

        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            checkNotNull(supportActionBar).apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowTitleEnabled(false)
            }
        }

        binding.spinner.apply {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    binding.flipper.displayedChild = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        }

        binding.byWeekdaysPager.apply {
            adapter = LessonsEditorPagerAdapter(childFragmentManager)
        }
        binding.byWeekdaysPagerTabStrip.apply {
            val color = context.colorAttr(R.attr.colorPrimary)
            setTextColor(color)
            tabIndicatorColor = color
        }

        // TODO: 13.11.2016 добавить заполнение списка пар по датам

        binding.addLesson.setOnClickListener {
            lifecycleScope.launch {
                val weekday = binding.byWeekdaysPager.currentItem + 1
                findNavController().navigate(
                    LessonsEditorFragmentDirections.addLesson(checkNotNull(viewModel.semester.value).id, weekday)
                )
            }
        }

        viewModel.semester.observe(owner) { if (it == null) findNavController().popBackStack() }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_lessons_editor, menu)
        menu.setColor(requireContext().getColorCompat(R.color.menu))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            findNavController().popBackStack()
            true
        }
        R.id.mlse_edit_semester -> {
            findNavController().navigate(LessonsEditorFragmentDirections.editSemester(checkNotNull(viewModel.semester.value)))
            true
        }
        R.id.mlse_delete_semester -> {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.lsea_delete_message)
                .setPositiveButton(R.string.lsea_delete_yes) { _, _ -> viewModel.deleteSemester() }
                .setNegativeButton(R.string.lsea_delete_no, null)
                .show()
            true
        }
        else -> false
    }
}
