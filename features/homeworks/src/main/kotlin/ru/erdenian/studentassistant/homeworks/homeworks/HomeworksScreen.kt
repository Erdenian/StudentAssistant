package ru.erdenian.studentassistant.homeworks.homeworks

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.erdenian.studentassistant.homeworks.api.HomeworksRoute
import ru.erdenian.studentassistant.homeworks.di.HomeworksComponentHolder
import ru.erdenian.studentassistant.navigation.LocalNavController
import ru.erdenian.studentassistant.repository.api.entity.Homework
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.uikit.dialog.ProgressDialog

@Composable
internal fun HomeworksScreen() {
    val viewModel = viewModel { HomeworksComponentHolder.instance.homeworksViewModel }
    val navController = LocalNavController.current

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
                    content = { Text(text = stringResource(RS.h_delete_no)) },
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteHomework(homework.id)
                        homeworkForDeleteDialog = null
                    },
                    content = { Text(text = stringResource(RS.h_delete_yes)) },
                )
            },
        )
    }

    HomeworksContent(
        semesters = semesters.map { it.name },
        selectedSemester = selectedSemester,
        overdueHomeworks = overdueHomeworks,
        actualHomeworks = actualHomeworks,
        pastHomeworks = pastHomeworks,
        onSelectedSemesterChange = { viewModel.selectSemester(semesters[it].id) },
        onAddHomeworkClick = { navController.navigate(HomeworksRoute.HomeworkEditor(semesterId = it.id)) },
        onHomeworkClick = { homework ->
            navController.navigate(
                HomeworksRoute.HomeworkEditor(semesterId = homework.semesterId, homeworkId = homework.id),
            )
        },
        onDeleteHomeworkClick = { homeworkForDeleteDialog = it },
    )
}
