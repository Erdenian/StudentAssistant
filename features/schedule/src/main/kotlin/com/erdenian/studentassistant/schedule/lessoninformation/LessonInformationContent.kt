package com.erdenian.studentassistant.schedule.lessoninformation

import android.content.res.Configuration
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
import com.erdenian.studentassistant.style.AutoMirrored
import com.erdenian.studentassistant.style.dimensions
import com.erdenian.studentassistant.uikit.layout.ContextMenuBox
import com.erdenian.studentassistant.uikit.placeholder.PlaceholderHighlight
import com.erdenian.studentassistant.uikit.placeholder.fade
import com.erdenian.studentassistant.uikit.placeholder.placeholder
import com.erdenian.studentassistant.uikit.view.ActionItem
import com.erdenian.studentassistant.uikit.view.LessonCard
import com.erdenian.studentassistant.uikit.view.TopAppBarActions
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
internal fun LessonInformationContent(
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
                    Icon(imageVector = AppIcons.AutoMirrored.ArrowBack, contentDescription = null)
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
) { paddingValues ->
    Column(
        modifier = Modifier.padding(paddingValues)
    ) {
        val timeFormatter = remember { DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT) }

        AnimatedContent(
            targetState = lesson,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "LessonInformationLessonCard"
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
                        horizontal = MaterialTheme.dimensions.screenPaddingHorizontal,
                        vertical = MaterialTheme.dimensions.screenPaddingVertical
                    )
                    .placeholder(
                        visible = (lessonState == null),
                        highlight = PlaceholderHighlight.fade()
                    )
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
                    }
                )
            }
        ) {
            LazyHomeworksList(
                homeworks = homeworks,
                onHomeworkClick = onHomeworkClick,
                onLongHomeworkClick = { contextMenuHomework = it }
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LessonInformationContentLoadingPreview() = AppTheme {
    LessonInformationContent(
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
        lesson = Lessons.regular,
        homeworks = List(10) { Homeworks.regular },
        onBackClick = {},
        onEditClick = {},
        onHomeworkClick = {},
        onAddHomeworkClick = {},
        onDeleteHomeworkClick = {}
    )
}
