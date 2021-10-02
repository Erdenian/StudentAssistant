package ru.erdenian.studentassistant.ui.main.lessoninformation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.joda.time.format.DateTimeFormat
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.uikit.style.AppTheme
import ru.erdenian.studentassistant.uikit.views.HomeworkCard
import ru.erdenian.studentassistant.uikit.views.LessonCard
import ru.erdenian.studentassistant.utils.Homeworks
import ru.erdenian.studentassistant.utils.Lessons
import ru.erdenian.studentassistant.utils.navArgsFactory

class LessonInformationFragment : Fragment() {

    private val viewModel by viewModels<LessonInformationViewModel> {
        navArgsFactory<LessonInformationFragmentArgs> { LessonInformationViewModel(it, lesson) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(inflater.context)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (view as ComposeView).setContent {
            val lesson by viewModel.lesson.observeAsState(navArgs<LessonInformationFragmentArgs>().value.lesson)
            val homeworks by viewModel.homeworks.map { it.list }.observeAsState(emptyList())

            if (lesson == null) findNavController().popBackStack()
            else AppTheme {
                View(
                    lesson = checkNotNull(lesson),
                    homeworks = homeworks,
                    onBackClick = { findNavController().popBackStack() },
                    onEditClick = {
                        findNavController().navigate(
                            LessonInformationFragmentDirections.editLesson(checkNotNull(viewModel.lesson.value))
                        )
                    },
                    onHomeworkClick = { homework ->
                        findNavController().navigate(LessonInformationFragmentDirections.editHomework(homework))
                    },
                    onAddHomeworkClick = {
                        findNavController().navigate(
                            LessonInformationFragmentDirections.createHomework(checkNotNull(viewModel.lesson.value))
                        )
                    },
                    onDeleteHomeworkClick = { viewModel.deleteHomework(it.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun View(
    lesson: Lesson,
    homeworks: List<Homework>,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onHomeworkClick: (Homework) -> Unit,
    onAddHomeworkClick: () -> Unit,
    onDeleteHomeworkClick: (Homework) -> Unit
) {
    val timeFormatter = remember { DateTimeFormat.shortTime() }
    val deadlineFormatter = remember { DateTimeFormat.shortDate() }
    var contextMenuHomework by remember { mutableStateOf<Homework?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.lif_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = stringResource(R.string.lif_edit))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddHomeworkClick
            ) {
                Icon(painter = painterResource(R.drawable.ic_plus), contentDescription = null)
            }
        }
    ) {
        Column {
            LessonCard(
                subjectName = lesson.subjectName,
                type = lesson.type,
                teachers = lesson.teachers.list,
                classrooms = lesson.classrooms.list,
                startTime = lesson.startTime.toString(timeFormatter),
                endTime = lesson.endTime.toString(timeFormatter),
                modifier = Modifier.padding(
                    start = dimensionResource(R.dimen.activity_horizontal_margin),
                    end = dimensionResource(R.dimen.activity_horizontal_margin),
                    top = dimensionResource(R.dimen.activity_vertical_margin)
                )
            )

            Divider(modifier = Modifier.padding(top = dimensionResource(R.dimen.activity_vertical_margin)))

            Box(modifier = Modifier.fillMaxSize()) {
                if (homeworks.isEmpty()) {
                    Text(
                        text = stringResource(R.string.lif_no_homeworks),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = dimensionResource(R.dimen.activity_horizontal_margin),
                            vertical = dimensionResource(R.dimen.activity_vertical_margin)
                        ),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.cards_spacing))
                    ) {
                        itemsIndexed(homeworks) { _, homework ->
                            HomeworkCard(
                                subjectName = homework.subjectName,
                                description = homework.description,
                                deadline = homework.deadline.toString(deadlineFormatter),
                                modifier = Modifier
                                    .combinedClickable(
                                        onLongClick = { contextMenuHomework = homework },
                                        onClick = { onHomeworkClick(homework) }
                                    )
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = (contextMenuHomework != null),
                        onDismissRequest = { contextMenuHomework = null }
                    ) {
                        val context = LocalContext.current
                        DropdownMenuItem(
                            onClick = {
                                val homework = checkNotNull(contextMenuHomework)
                                contextMenuHomework = null
                                MaterialAlertDialogBuilder(context)
                                    .setMessage(R.string.lif_delete_message)
                                    .setPositiveButton(R.string.lif_delete_yes) { _, _ -> onDeleteHomeworkClick(homework) }
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
    }
}

@Preview
@Composable
private fun PreviewRegular() = AppTheme {
    View(
        lesson = Lessons.regular,
        homeworks = List(10) { Homeworks.regular },
        onBackClick = {},
        onEditClick = {},
        onHomeworkClick = {},
        onAddHomeworkClick = {},
        onDeleteHomeworkClick = {}
    )
}

@Preview
@Composable
private fun PreviewDark() = AppTheme(isDarkTheme = true) {
    View(
        lesson = Lessons.regular,
        homeworks = List(10) { Homeworks.regular },
        onBackClick = {},
        onEditClick = {},
        onHomeworkClick = {},
        onAddHomeworkClick = {},
        onDeleteHomeworkClick = {}
    )
}

@Preview
@Composable
private fun PreviewLong() = AppTheme {
    View(
        lesson = Lessons.long,
        homeworks = List(10) { Homeworks.long },
        onBackClick = {},
        onEditClick = {},
        onHomeworkClick = {},
        onAddHomeworkClick = {},
        onDeleteHomeworkClick = {}
    )
}
