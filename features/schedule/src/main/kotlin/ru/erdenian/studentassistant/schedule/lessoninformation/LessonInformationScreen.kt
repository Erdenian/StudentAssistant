package ru.erdenian.studentassistant.schedule.lessoninformation

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.sampledata.Homeworks
import ru.erdenian.studentassistant.sampledata.Lessons
import ru.erdenian.studentassistant.schedule.R
import ru.erdenian.studentassistant.schedule.composable.LazyHomeworksList
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.LessonCard
import ru.erdenian.studentassistant.uikit.view.ProgressDialog
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
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
            title = { Text(stringResource(R.string.li_title)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = AppIcons.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                val icon = AppIcons.Edit
                TopAppBarActions(
                    actions = listOf(
                        if (lesson == null) {
                            ActionItem.AlwaysShow(
                                name = stringResource(R.string.li_edit),
                                onClick = {},
                                enabled = false
                            ) {
                                CircularProgressIndicator(
                                    color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
                                    modifier = Modifier.size(icon.defaultWidth, icon.defaultHeight)
                                )
                            }
                        } else {
                            ActionItem.AlwaysShow(
                                name = stringResource(R.string.li_edit),
                                imageVector = icon,
                                onClick = { onEditClick(lesson) }
                            )
                        }
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
            LessonInformationViewModel.Operation.DELETING_HOMEWORK -> R.string.li_delete_homework_progress
        }
        ProgressDialog { Text(text = stringResource(stringId)) }
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
                teachers = lessonState?.teachers?.list ?: listOf(emptyText),
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
                        .setMessage(R.string.li_delete_homework_message)
                        .setPositiveButton(R.string.li_delete_homework_yes) { _, _ -> onDeleteHomeworkClick(homework) }
                        .setNegativeButton(R.string.li_delete_homework_no, null)
                        .show()
                }
            ) {
                Text(text = stringResource(R.string.li_delete_homework))
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LessonInformationContentRegularPreview() = AppTheme {
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

@Preview
@Composable
private fun LessonInformationContentLongPreview() = AppTheme {
    LessonInformationContent(
        operation = null,
        lesson = Lessons.long,
        homeworks = List(10) { Homeworks.long },
        onBackClick = {},
        onEditClick = {},
        onHomeworkClick = {},
        onAddHomeworkClick = {},
        onDeleteHomeworkClick = {}
    )
}
