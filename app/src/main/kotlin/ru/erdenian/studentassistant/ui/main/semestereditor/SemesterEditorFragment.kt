package ru.erdenian.studentassistant.ui.main.semestereditor

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.joda.time.format.DateTimeFormat
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.FragmentSemesterEditorBinding
import ru.erdenian.studentassistant.ui.main.semestereditor.SemesterEditorViewModel.Error
import ru.erdenian.studentassistant.utils.binding
import ru.erdenian.studentassistant.utils.distinctUntilChanged
import ru.erdenian.studentassistant.utils.getColorCompat
import ru.erdenian.studentassistant.utils.setColor
import ru.erdenian.studentassistant.utils.showDatePicker
import ru.erdenian.studentassistant.utils.toast

class SemesterEditorFragment : Fragment(R.layout.fragment_semester_editor) {

    companion object {
        private const val DATE_FORMAT = "dd.MM.yyyy"
    }

    private val viewModel by viewModels<SemesterEditorViewModel> {
        object : ViewModelProvider.Factory {
            private val application = requireActivity().application
            private val args by navArgs<SemesterEditorFragmentArgs>()

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                SemesterEditorViewModel(application, args.semester) as T
        }
    }

    private val binding by binding { FragmentSemesterEditorBinding.bind(requireView()) }

    @Suppress("ComplexMethod")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        val owner = viewLifecycleOwner

        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            checkNotNull(supportActionBar).setDisplayHomeAsUpEnabled(true)
            if (!viewModel.isEditing) title = getString(R.string.sea_title_new)
        }

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
            setOnClickListener { requireContext().showDatePicker(viewModel.firstDay.value) { viewModel.firstDay.value = it } }
        }

        binding.lastDay.apply {
            viewModel.lastDay.observe(owner) { text = it.toString(dateFormatter) }
            setOnClickListener { requireContext().showDatePicker(viewModel.lastDay.value) { viewModel.lastDay.value = it } }
        }

        viewModel.saved.observe(owner) { if (it) findNavController().popBackStack() }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_semester_editor, menu)
        menu.setColor(requireContext().getColorCompat(R.color.menu))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            findNavController().popBackStack()
            true
        }
        R.id.mse_save -> {
            viewModel.error.value?.let { error ->
                requireContext().toast(
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
