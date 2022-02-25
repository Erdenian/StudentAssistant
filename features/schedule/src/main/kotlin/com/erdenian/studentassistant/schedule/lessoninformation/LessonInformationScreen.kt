package com.erdenian.studentassistant.schedule.lessoninformation

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.erdenian.studentassistant.entity.Homework
import com.erdenian.studentassistant.entity.Lesson
import com.erdenian.studentassistant.sampledata.Homeworks
import com.erdenian.studentassistant.sampledata.Lessons
import com.erdenian.studentassistant.schedule.composable.LazyHomeworksList
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.style.AppIcons
import com.erdenian.studentassistant.style.AppTheme
import com.erdenian.studentassistant.style.dimensions
import com.erdenian.studentassistant.uikit.view.ActionItem
import com.erdenian.studentassistant.uikit.view.ContextMenuDialog
import com.erdenian.studentassistant.uikit.view.ContextMenuItem
import com.erdenian.studentassistant.uikit.view.LessonCard
import com.erdenian.studentassistant.uikit.view.ProgressDialog
import com.erdenian.studentassistant.uikit.view.TopAppBarActions
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun LessonInformationScreen(
    viewModel: LessonInformationViewModel,
    navigateBack: () -> Unit,
    navigateToEditLesson: (semesterId: Long, lessonId: Long) -> Unit,
    navigateToEditHomework: (semesterId: Long, homeworkId: Long) -> Unit,
    navigateToCreateHomework: (semesterId: Long, subjectName: String) -> Unit
) {
    val isDeleted by viewModel.isDeleted.collectAsState()
    LaunchedEffect(isDeleted) {
        if (isDeleted) navigateBack()
    }

    val operation by viewModel.operation.collectAsState()
    val lesson by viewModel.lesson.collectAsState()
    val homeworks by viewModel.homeworks.collectAsState()

    LessonInformationContent(
        operation = operation,
        lesson = lesson,
        homeworks = homeworks?.list,
        onBackClick = navigateBack,
        onEditClick = { navigateToEditLesson(it.semesterId, it.id) },
        onHomeworkClick = { navigateToEditHomework(it.semesterId, it.id) },
        onAddHomeworkClick = { navigateToCreateHomework(it.semesterId, it.subjectName) },
        onDeleteHomeworkClick = { viewModel.deleteHomework(it.id) }
    )
}

@Composable
private fun LessonInformationContent(
    operation: LessonInformationViewModel.Operation?,
    lesson: Lesson?,
    homeworks: List<Homework>?,
    onBackClick: () -> Unit,
    onEditClick: (Lesson) -> Unit,
    onHomeworkClick: (Homework) -> Unit,
    onAddHomeworkClick: (Lesson) -> Unit,
    onDeleteHomeworkClick: (Homework) -> Unit
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(stringResource(RS.li_title)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = AppIcons.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                TopAppBarActions(
                    actions = listOf(
                        ActionItem.AlwaysShow(
                            name = stringResource(RS.li_edit),
                            imageVector = AppIcons.Edit,
                            loading = (lesson == null),
                            onClick = { lesson?.let(onEditClick) }
                        )
                    )
                )
            }
        )
    },
    floatingActionButton = {
        if (lesson != null) {
            FloatingActionButton(onClick = { onAddHomeworkClick(lesson) }) {
                Icon(imageVector = AppIcons.Add, contentDescription = null)
            }
        }
    }
) {
    if (operation != null) {
        val stringId = when (operation) {
            LessonInformationViewModel.Operation.DELETING_HOMEWORK -> RS.li_delete_homework_progress
        }
        ProgressDialog(stringResource(stringId))
    }

    Column {
        val timeFormatter = remember { DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT) }

        AnimatedContent(
            targetState = lesson,
            transitionSpec = { fadeIn() with fadeOut() }
        ) { lessonState ->
            // Random non-empty string to make LessonCard larger. User will not see it behind the shimmer
            val emptyText = "Loading..."
            LessonCard(
                subjectName = lessonState?.subjectName ?: emptyText,
                type = lessonState?.type ?: emptyText,
                teachers = lessonState?.teachers?.list ?: emptyList(),
                classrooms = lessonState?.classrooms?.list ?: listOf(emptyText),
                startTime = lessonState?.startTime?.format(timeFormatter) ?: emptyText,
                endTime = lessonState?.endTime?.format(timeFormatter) ?: emptyText,
                modifier = Modifier
                    .padding(
                        horizontal = MaterialTheme.dimensions.activityHorizontalMargin,
                        vertical = MaterialTheme.dimensions.activityVerticalMargin
                    )
                    .placeholder(
                        visible = (lessonState == null),
                        highlight = PlaceholderHighlight.shimmer()
                    )
            )
        }

        Divider()

        var contextMenuHomework by remember { mutableStateOf<Homework?>(null) }

        LazyHomeworksList(
            homeworks = homeworks,
            onHomeworkClick = onHomeworkClick,
            onLongHomeworkClick = { contextMenuHomework = it }
        )

        contextMenuHomework?.let { homework ->
            val context = LocalContext.current
            ContextMenuDialog(
                onDismissRequest = { contextMenuHomework = null },
                title = homework.subjectName,
                items = listOf(
                    ContextMenuItem(stringResource(RS.li_delete_homework)) {
                        contextMenuHomework = null
                        MaterialAlertDialogBuilder(context)
                            .setMessage(RS.li_delete_homework_message)
                            .setPositiveButton(RS.li_delete_homework_yes) { _, _ -> onDeleteHomeworkClick(homework) }
                            .setNegativeButton(RS.li_delete_homework_no, null)
                            .show()
                    }
                )
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LessonInformationContentLoadingPreview() = AppTheme {
    LessonInformationContent(
        operation = null,
        lesson = null,
        homeworks = null,
        onBackClick = {},
        onEditClick = {},
        onHomeworkClick = {},
        onAddHomeworkClick = {},
        onDeleteHomeworkClick = {}
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LessonInformationContentNoHomeworksPreview() = AppTheme {
    LessonInformationContent(
        operation = null,
        lesson = Lessons.regular,
        homeworks = emptyList(),
        onBackClick = {},
        onEditClick = {},
        onHomeworkClick = {},
        onAddHomeworkClick = {},
        onDeleteHomeworkClick = {}
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LessonInformationContentPreview() = AppTheme {
    LessonInformationContent(
        operation = null,
        lesson = Lessons.regular,
        homeworks = List(10) { Homeworks.regular },
        onBackClick = {},
        onEditClick = {},
        onHomeworkClick = {},
        onAddHomeworkClick = {},
        onDeleteHomeworkClick = {}
    )
}
