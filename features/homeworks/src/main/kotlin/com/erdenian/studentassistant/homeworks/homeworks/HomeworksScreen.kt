package com.erdenian.studentassistant.homeworks.homeworks

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
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
fun HomeworksScreen(
    viewModel: HomeworksViewModel,
    navigateToCreateHomework: (semesterId: Long) -> Unit,
    navigateToEditHomework: (semesterId: Long, homeworkId: Long) -> Unit
) {
    val semesters by viewModel.allSemesters.collectAsState()
    val selectedSemester by viewModel.selectedSemester.collectAsState()

    val overdueHomeworks by viewModel.overdue.collectAsState()
    val actualHomeworks by viewModel.actual.collectAsState()
    val pastHomeworks by viewModel.past.collectAsState()

    val operation by viewModel.operation.collectAsState()
    when (operation) {
        HomeworksViewModel.Operation.DELETING_HOMEWORK -> RS.h_delete_progress
        null -> null
    }?.let { ProgressDialog(stringResource(it)) }

    var homeworkForDeleteDialog: Homework? by rememberSaveable { mutableStateOf(null) }
    homeworkForDeleteDialog?.let { homework ->
        AlertDialog(
            onDismissRequest = { homeworkForDeleteDialog = null },
            text = { Text(text = stringResource(RS.h_delete_message)) },
            dismissButton = {
                TextButton(
                    onClick = { homeworkForDeleteDialog = null },
                    content = { Text(text = stringResource(RS.h_delete_no)) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteHomework(homework.id)
                        homeworkForDeleteDialog = null
                    },
                    content = { Text(text = stringResource(RS.h_delete_yes)) }
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
                ContextMenuItem(stringResource(RS.h_delete_homework)) {
                    contextMenuHomework = null
                    homeworkForDeleteDialog = homework
                }
            )
        )
    }

    HomeworksContent(
        semesters = semesters.map { it.name },
        selectedSemester = selectedSemester,
        overdueHomeworks = overdueHomeworks?.list,
        actualHomeworks = actualHomeworks?.list,
        pastHomeworks = pastHomeworks?.list,
        onSelectedSemesterChange = { viewModel.selectSemester(semesters.list[it].id) },
        onAddHomeworkClick = { navigateToCreateHomework(it.id) },
        onHomeworkClick = { navigateToEditHomework(it.semesterId, it.id) },
        onLongHomeworkClick = { contextMenuHomework = it }
    )
}
