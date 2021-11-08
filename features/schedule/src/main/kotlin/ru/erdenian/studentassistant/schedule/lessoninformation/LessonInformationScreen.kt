package ru.erdenian.studentassistant.schedule.lessoninformation

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.joda.time.format.DateTimeFormat
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.sampledata.Homeworks
import ru.erdenian.studentassistant.sampledata.Lessons
import ru.erdenian.studentassistant.schedule.R
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.HomeworkCard
import ru.erdenian.studentassistant.uikit.view.LessonCard
import ru.erdenian.studentassistant.uikit.view.TopAppBarActions

@Composable
fun LessonInformationScreen(
    viewModel: LessonInformationViewModel,
    navigateBack: () -> Unit,
    navigateToEditLesson: (semesterId: Long, lessonId: Long) -> Unit,
    navigateToEditHomework: (semesterId: Long, homeworkId: Long) -> Unit,
    navigateToCreateHomework: (semesterId: Long, subjectName: String) -> Unit
) {
    val isDeleted by viewModel.isDeleted.collectAsState()
    if (isDeleted) {
        DisposableEffect(isDeleted) {
            if (isDeleted) navigateBack()
            onDispose {}
        }
    }

    val lesson by viewModel.lesson.collectAsState()
    val homeworks by viewModel.homeworks.collectAsState()

    LessonInformationContent(
        lesson = lesson,
        homeworks = homeworks.list,
        onBackClick = navigateBack,
        onEditClick = { navigateToEditLesson(checkNotNull(lesson).semesterId, viewModel.lessonId) },
        onHomeworkClick = { navigateToEditHomework(checkNotNull(lesson).semesterId, it.id) },
        onAddHomeworkClick = { navigateToCreateHomework(checkNotNull(lesson).semesterId, checkNotNull(lesson).subjectName) },
        onDeleteHomeworkClick = { viewModel.deleteHomework(it.id) }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LessonInformationContent(
    lesson: Lesson?,
    homeworks: List<Homework>,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onHomeworkClick: (Homework) -> Unit,
    onAddHomeworkClick: () -> Unit,
    onDeleteHomeworkClick: (Homework) -> Unit
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(stringResource(R.string.li_title)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = AppIcons.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                TopAppBarActions(
                    actions = listOf(
                        ActionItem.AlwaysShow(
                            name = stringResource(R.string.li_edit),
                            imageVector = AppIcons.Edit,
                            onClick = onEditClick
                        )
                    )
                )
            }
        )
    },
    floatingActionButton = {
        FloatingActionButton(onClick = onAddHomeworkClick) {
            Icon(imageVector = AppIcons.Add, contentDescription = null)
        }
    }
) {
    if (lesson == null) {
        CircularProgressIndicator()
    } else {
        Column {
            val timeFormatter = remember { DateTimeFormat.shortTime() }

            LessonCard(
                subjectName = lesson.subjectName,
                type = lesson.type,
                teachers = lesson.teachers.list,
                classrooms = lesson.classrooms.list,
                startTime = lesson.startTime.toString(timeFormatter),
                endTime = lesson.endTime.toString(timeFormatter),
                modifier = Modifier.padding(
                    horizontal = AppTheme.dimensions.activityHorizontalMargin,
                    vertical = AppTheme.dimensions.activityVerticalMargin
                )
            )

            Divider()

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                if (homeworks.isEmpty()) {
                    Text(
                        text = stringResource(R.string.li_no_homeworks),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = AppTheme.dimensions.activityHorizontalMargin)
                    )
                } else {
                    var contextMenuHomework by remember { mutableStateOf<Homework?>(null) }

                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = AppTheme.dimensions.activityHorizontalMargin,
                            vertical = AppTheme.dimensions.activityVerticalMargin
                        ),
                        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.cardsSpacing),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(
                            items = homeworks,
                            key = { _, item -> item.id }
                        ) { _, homework ->
                            val deadlineFormatter = remember { DateTimeFormat.shortDate() }

                            HomeworkCard(
                                subjectName = homework.subjectName,
                                description = homework.description,
                                deadline = homework.deadline.toString(deadlineFormatter),
                                onClick = { onHomeworkClick(homework) },
                                onLongClick = { contextMenuHomework = homework }
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
                                    .setMessage(R.string.li_delete_message)
                                    .setPositiveButton(R.string.li_delete_yes) { _, _ -> onDeleteHomeworkClick(homework) }
                                    .setNegativeButton(R.string.li_delete_no, null)
                                    .show()
                            }
                        ) {
                            Text(text = stringResource(R.string.li_delete_homework))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LessonInformationContentRegularPreview() = AppTheme {
    LessonInformationContent(
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
private fun LessonInformationContentLongPreview() = AppTheme {
    LessonInformationContent(
        lesson = Lessons.long,
        homeworks = List(10) { Homeworks.long },
        onBackClick = {},
        onEditClick = {},
        onHomeworkClick = {},
        onAddHomeworkClick = {},
        onDeleteHomeworkClick = {}
    )
}
