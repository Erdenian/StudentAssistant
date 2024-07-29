package com.erdenian.studentassistant.homeworks.homeworks

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
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.erdenian.studentassistant.entity.Homework
import com.erdenian.studentassistant.homeworks.HomeworksApi
import com.erdenian.studentassistant.homeworks.api.HomeworkScreen
import com.erdenian.studentassistant.homeworks.di.HomeworksComponent
import com.erdenian.studentassistant.mediator.findComponent
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.uikit.dialog.ProgressDialog

internal class HomeworksScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = viewModel { findComponent<HomeworksApi, HomeworksComponent>().homeworksViewModel }
        val navigator = LocalNavigator.currentOrThrow

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

        HomeworksContent(
            semesters = semesters.map { it.name },
            selectedSemester = selectedSemester,
            overdueHomeworks = overdueHomeworks?.list,
            actualHomeworks = actualHomeworks?.list,
            pastHomeworks = pastHomeworks?.list,
            onSelectedSemesterChange = { viewModel.selectSemester(semesters.list[it].id) },
            onAddHomeworkClick = { navigator.push(ScreenRegistry.get(HomeworkScreen.HomeworkEditor(it.id))) },
            onHomeworkClick = { navigator.push(ScreenRegistry.get(HomeworkScreen.HomeworkEditor(it.semesterId, it.id))) },
            onDeleteHomeworkClick = { homeworkForDeleteDialog = it }
        )
    }
}
