package com.erdenian.studentassistant.schedule.lessoninformation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.erdenian.studentassistant.homeworks.api.HomeworkScreen
import com.erdenian.studentassistant.mediator.findComponent
import com.erdenian.studentassistant.schedule.ScheduleApi
import com.erdenian.studentassistant.schedule.api.ScheduleScreen
import com.erdenian.studentassistant.schedule.di.ScheduleComponent
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.uikit.dialog.ProgressDialog

internal class LessonInformationScreen(private val arguments: ScheduleScreen.LessonInformation) : Screen {

    @Composable
    override fun Content() {
        val viewModel = viewModel {
            findComponent<ScheduleApi, ScheduleComponent>().lessonInformationViewModelFactory.get(arguments.lessonId)
        }
        val navigator = LocalNavigator.currentOrThrow

        val isDeleted by viewModel.isDeleted.collectAsState()
        LaunchedEffect(isDeleted) {
            if (isDeleted) navigator.pop()
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

        LessonInformationContent(
            lesson = lesson,
            homeworks = homeworks?.list,
            onBackClick = navigator::pop,
            onEditClick = { navigator.push(ScreenRegistry.get(ScheduleScreen.LessonEditor(it.semesterId, it.id))) },
            onHomeworkClick = { navigator.push(ScreenRegistry.get(HomeworkScreen.HomeworkEditor(it.semesterId, it.id))) },
            onAddHomeworkClick = {
                navigator.push(
                    ScreenRegistry.get(
                        HomeworkScreen.HomeworkEditor(
                            it.semesterId,
                            it.subjectName
                        )
                    )
                )
            },
            onDeleteHomeworkClick = { homeworkForDeleteDialog = it }
        )
    }
}
