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
import com.erdenian.studentassistant.homeworks.api.HomeworksRoute
import com.erdenian.studentassistant.navigation.LocalNavController
import com.erdenian.studentassistant.repository.api.entity.Homework
import com.erdenian.studentassistant.schedule.api.ScheduleRoute
import com.erdenian.studentassistant.schedule.di.ScheduleComponentHolder
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.uikit.dialog.ProgressDialog

@Composable
internal fun LessonInformationScreen(route: ScheduleRoute.LessonInformation) {
    val viewModel = viewModel {
        ScheduleComponentHolder.instance.lessonInformationViewModelFactory.get(route.lesson)
    }
    val navController = LocalNavController.current

    val isDeleted by viewModel.isDeleted.collectAsState()
    LaunchedEffect(isDeleted) {
        if (isDeleted) navController.popBackStack()
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
                    content = { Text(text = stringResource(RS.li_delete_homework_no)) },
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteHomework(homework.id)
                        homeworkForDeleteDialog = null
                    },
                    content = { Text(text = stringResource(RS.li_delete_homework_yes)) },
                )
            },
        )
    }

    LessonInformationContent(
        lesson = lesson ?: return,
        homeworks = homeworks,
        onBackClick = navController::popBackStack,
        onEditClick = { clickedLesson ->
            navController.navigate(
                ScheduleRoute.LessonEditor(
                    semesterId = clickedLesson.semesterId,
                    lessonId = clickedLesson.id,
                ),
            )
        },
        onHomeworkClick = { clickedHomework ->
            navController.navigate(
                HomeworksRoute.HomeworkEditor(semesterId = clickedHomework.semesterId, homeworkId = clickedHomework.id),
            )
        },
        onAddHomeworkClick = { currentLesson ->
            navController.navigate(
                HomeworksRoute.HomeworkEditor(
                    semesterId = currentLesson.semesterId,
                    subjectName = currentLesson.subjectName,
                ),
            )
        },
        onDeleteHomeworkClick = { homeworkForDeleteDialog = it },
    )
}
