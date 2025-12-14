package ru.erdenian.studentassistant.schedule.lessoninformation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import ru.erdenian.studentassistant.navigation.LocalSharedTransitionScope
import ru.erdenian.studentassistant.repository.api.entity.Homework
import ru.erdenian.studentassistant.repository.api.entity.Lesson
import ru.erdenian.studentassistant.sampledata.Homeworks
import ru.erdenian.studentassistant.sampledata.Lessons
import ru.erdenian.studentassistant.schedule.composable.LazyHomeworksList
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.AutoMirrored
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.layout.ContextMenuBox
import ru.erdenian.studentassistant.uikit.utils.ScreenPreviews
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.LessonCard
import ru.erdenian.studentassistant.uikit.view.TopAppBarActions

/**
 * UI контент экрана информации о занятии.
 *
 * @param lesson занятие.
 * @param homeworks список домашних заданий по предмету.
 * @param onBackClick колбэк нажатия назад.
 * @param onEditClick колбэк нажатия редактирования.
 * @param onHomeworkClick колбэк нажатия на домашнее задание.
 * @param onAddHomeworkClick колбэк добавления домашнего задания.
 * @param onDeleteHomeworkClick колбэк удаления домашнего задания.
 */
@Composable
internal fun LessonInformationContent(
    lesson: Lesson,
    homeworks: List<Homework>?,
    onBackClick: () -> Unit,
    onEditClick: (Lesson) -> Unit,
    onHomeworkClick: (Homework) -> Unit,
    onAddHomeworkClick: (Lesson) -> Unit,
    onDeleteHomeworkClick: (Homework) -> Unit,
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(stringResource(RS.li_title)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = AppIcons.AutoMirrored.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                TopAppBarActions(
                    actions = listOf(
                        ActionItem.AlwaysShow(
                            name = stringResource(RS.li_edit),
                            imageVector = AppIcons.Edit,
                            onClick = { onEditClick(lesson) },
                        ),
                    ),
                )
            },
        )
    },
    floatingActionButton = {
        FloatingActionButton(onClick = { onAddHomeworkClick(lesson) }) {
            Icon(imageVector = AppIcons.Add, contentDescription = null)
        }
    },
) { paddingValues ->
    Column(
        modifier = Modifier.padding(paddingValues),
    ) {
        val timeFormatter = remember { DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT) }

        AnimatedContent(
            targetState = lesson,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "LessonInformationLessonCard",
        ) { lessonState ->
            val isInspection = LocalInspectionMode.current
            LessonCard(
                subjectName = lessonState.subjectName,
                type = lessonState.type,
                teachers = lessonState.teachers,
                classrooms = lessonState.classrooms,
                startTime = lessonState.startTime.format(timeFormatter),
                endTime = lessonState.endTime.format(timeFormatter),
                modifier = Modifier
                    .padding(
                        horizontal = MaterialTheme.dimensions.screenPaddingHorizontal,
                        vertical = MaterialTheme.dimensions.screenPaddingVertical,
                    )
                    .let { modifier ->
                        if (!isInspection) {
                            with(LocalSharedTransitionScope.current) {
                                modifier.sharedElement(
                                    rememberSharedContentState(lesson),
                                    LocalNavAnimatedContentScope.current,
                                )
                            }
                        } else {
                            modifier
                        }
                    },
            )
        }

        HorizontalDivider()

        var contextMenuHomework by remember { mutableStateOf<Homework?>(null) }
        ContextMenuBox(
            expanded = (contextMenuHomework != null),
            onDismissRequest = { contextMenuHomework = null },
            contextMenu = {
                DropdownMenuItem(
                    text = { Text(stringResource(RS.li_delete_homework)) },
                    onClick = {
                        val homework = checkNotNull(contextMenuHomework)
                        contextMenuHomework = null
                        onDeleteHomeworkClick(homework)
                    },
                )
            },
        ) {
            LazyHomeworksList(
                homeworks = homeworks,
                onHomeworkClick = onHomeworkClick,
                onLongHomeworkClick = { contextMenuHomework = it },
            )
        }
    }
}

private data class LessonInformationContentPreviewData(
    val lesson: Lesson,
    val homeworks: List<Homework>?,
)

@Suppress("MagicNumber")
private class LessonInformationContentPreviewParameterProvider :
    PreviewParameterProvider<LessonInformationContentPreviewData> {
    override val values = sequenceOf(
        LessonInformationContentPreviewData(Lessons.regular, null),
        LessonInformationContentPreviewData(Lessons.long, emptyList()),
        LessonInformationContentPreviewData(Lessons.regular, List(5) { Homeworks.regular }),
    )
}

@ScreenPreviews
@Composable
private fun LessonInformationContentPreview(
    @PreviewParameter(LessonInformationContentPreviewParameterProvider::class)
    data: LessonInformationContentPreviewData,
) = AppTheme {
    LessonInformationContent(
        lesson = data.lesson,
        homeworks = data.homeworks,
        onBackClick = {},
        onEditClick = {},
        onHomeworkClick = {},
        onAddHomeworkClick = {},
        onDeleteHomeworkClick = {},
    )
}
