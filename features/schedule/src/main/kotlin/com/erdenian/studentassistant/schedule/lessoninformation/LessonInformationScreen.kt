package com.erdenian.studentassistant.schedule.lessoninformation

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.erdenian.studentassistant.entity.Homework
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.uikit.view.ContextMenuDialog
import com.erdenian.studentassistant.uikit.view.ContextMenuItem
import com.erdenian.studentassistant.uikit.view.ProgressDialog

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

    val lesson by viewModel.lesson.collectAsState()
    val homeworks by viewModel.homeworks.collectAsState()

    val operation by viewModel.operation.collectAsState()
    when (operation) {
        LessonInformationViewModel.Operation.DELETING_HOMEWORK -> RS.li_delete_homework_progress
        null -> null
    }?.let { ProgressDialog(stringResource(it)) }

    var homeworkForDeleteDialog: Homework? by rememberSaveable { mutableStateOf(null) }
    homeworkForDeleteDialog?.let { homework ->
        AlertDialog(
            onDismissRequest = { homeworkForDeleteDialog = null },
            text = { Text(text = stringResource(RS.li_delete_homework_message)) },
            dismissButton = {
                TextButton(
                    onClick = { homeworkForDeleteDialog = null },
                    content = { Text(text = stringResource(RS.li_delete_homework_no)) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteHomework(homework.id)
                        homeworkForDeleteDialog = null
                    },
                    content = { Text(text = stringResource(RS.li_delete_homework_yes)) }
                )
            }
        )
    }

    var contextMenuHomework by rememberSaveable { mutableStateOf<Homework?>(null) }
    contextMenuHomework?.let { homework ->
        ContextMenuDialog(
            onDismissRequest = { contextMenuHomework = null },
            title = homework.subjectName,
            items = listOf(
                ContextMenuItem(stringResource(RS.li_delete_homework)) {
                    contextMenuHomework = null
                    homeworkForDeleteDialog = homework
                }
            )
        )
    }

    LessonInformationContent(
        lesson = lesson,
        homeworks = homeworks?.list,
        onBackClick = navigateBack,
        onEditClick = { navigateToEditLesson(it.semesterId, it.id) },
        onHomeworkClick = { navigateToEditHomework(it.semesterId, it.id) },
        onAddHomeworkClick = { navigateToCreateHomework(it.semesterId, it.subjectName) },
        onLongHomeworkClick = { contextMenuHomework = it }
    )
}
