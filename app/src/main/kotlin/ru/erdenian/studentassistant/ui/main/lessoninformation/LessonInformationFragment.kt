package ru.erdenian.studentassistant.ui.main.lessoninformation

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.joda.time.format.DateTimeFormat
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.FragmentLessonInformationBinding
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.uikit.style.AppTheme
import ru.erdenian.studentassistant.uikit.views.HomeworkCard
import ru.erdenian.studentassistant.utils.getColorCompat
import ru.erdenian.studentassistant.utils.navArgsFactory
import ru.erdenian.studentassistant.utils.setColor

class LessonInformationFragment : Fragment(R.layout.fragment_lesson_information) {

    companion object {
        private const val TIME_FORMAT = "HH:mm"
    }

    private val viewModel by viewModels<LessonInformationViewModel> {
        navArgsFactory<LessonInformationFragmentArgs> { LessonInformationViewModel(it, lesson) }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Suppress("ComplexMethod")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentLessonInformationBinding.bind(view)
        val owner = viewLifecycleOwner
        setHasOptionsMenu(true)

        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            checkNotNull(supportActionBar).setDisplayHomeAsUpEnabled(true)
        }

        binding.content.subjectName.apply {
            viewModel.lesson.observe(owner) { text = it?.subjectName }
        }

        binding.content.startTime.apply {
            viewModel.lesson.observe(owner) { text = it?.startTime?.toString(TIME_FORMAT) }
        }

        binding.content.endTime.apply {
            viewModel.lesson.observe(owner) { text = it?.endTime?.toString(TIME_FORMAT) }
        }

        binding.content.type.apply {
            viewModel.lesson.observe(owner) { text = it?.type }
        }

        binding.content.homeworksFlipper.apply {
            val containsHomeworksIndex = 0
            val noHomeworksIndex = 1
            viewModel.homeworks.observe(owner) { homeworks ->
                displayedChild = if (homeworks.isEmpty()) noHomeworksIndex else containsHomeworksIndex
            }
        }

        binding.content.homeworks.setContent {
            val deadlineFormatter = DateTimeFormat.shortDate()
            AppTheme {
                val homeworks by viewModel.homeworks.map { it.list }.observeAsState(emptyList())
                var contextMenuHomework by remember { mutableStateOf<Homework?>(null) }
                Box {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = dimensionResource(R.dimen.activity_horizontal_margin),
                            vertical = dimensionResource(R.dimen.activity_vertical_margin)
                        )
                    ) {
                        itemsIndexed(homeworks) { index, homework ->
                            if (index != 0) Spacer(modifier = Modifier.height(dimensionResource(R.dimen.cards_spacing)))

                            HomeworkCard(
                                subjectName = homework.subjectName,
                                description = homework.description,
                                deadline = homework.deadline.toString(deadlineFormatter),
                                modifier = Modifier
                                    .combinedClickable(
                                        onLongClick = { contextMenuHomework = homework },
                                        onClick = {
                                            findNavController().navigate(
                                                LessonInformationFragmentDirections.editHomework(homework)
                                            )
                                        }
                                    )
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = (contextMenuHomework != null),
                        onDismissRequest = { contextMenuHomework = null }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                val homeworkId = checkNotNull(contextMenuHomework).id
                                contextMenuHomework = null
                                MaterialAlertDialogBuilder(requireContext())
                                    .setMessage(R.string.lif_delete_message)
                                    .setPositiveButton(R.string.lif_delete_yes) { _, _ -> viewModel.deleteHomework(homeworkId) }
                                    .setNegativeButton(R.string.lif_delete_no, null)
                                    .show()
                            }
                        ) {
                            Text(text = stringResource(R.string.hf_delete_homework))
                        }
                    }
                }
            }
        }

        binding.addHomework.setOnClickListener {
            findNavController().navigate(
                LessonInformationFragmentDirections.createHomework(checkNotNull(viewModel.lesson.value))
            )
        }

        viewModel.lesson.observe(owner) { if (it == null) findNavController().popBackStack() }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_lesson_information, menu)
        menu.setColor(requireContext().getColorCompat(R.color.menu))
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            findNavController().popBackStack()
            true
        }
        R.id.mli_edit -> {
            findNavController().navigate(LessonInformationFragmentDirections.editLesson(checkNotNull(viewModel.lesson.value)))
            true
        }
        else -> false
    }
}
