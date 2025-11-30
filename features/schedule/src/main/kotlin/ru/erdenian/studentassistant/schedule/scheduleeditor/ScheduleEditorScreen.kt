package ru.erdenian.studentassistant.schedule.scheduleeditor

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.DayOfWeek
import kotlinx.coroutines.launch
import ru.erdenian.studentassistant.navigation.LocalNavigator
import ru.erdenian.studentassistant.repository.api.entity.Lesson
import ru.erdenian.studentassistant.schedule.api.ScheduleRoute
import ru.erdenian.studentassistant.schedule.di.ScheduleComponentHolder
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.uikit.dialog.ProgressDialog

@Composable
internal fun ScheduleEditorScreen(route: ScheduleRoute.ScheduleEditor) {
    val viewModel = viewModel {
        ScheduleComponentHolder.instance.scheduleEditorViewModelFactory.get(route.semesterId)
    }
    val navController = LocalNavigator.current

    val isDeleted by viewModel.isDeleted.collectAsState()
    LaunchedEffect(isDeleted) {
        if (isDeleted) navController.goBack()
    }

    @Suppress("Wrapping")
    val rememberLessons = remember<@Composable (Int) -> State<List<Lesson>?>>(viewModel) {
        { page ->
            produceState(null, page) {
                val dayOfWeek = DayOfWeek.of(page + 1)
                viewModel.getLessons(dayOfWeek).collect { value = it }
            }
        }
    }

    val operation by viewModel.operation.collectAsState()
    when (operation) {
        ScheduleEditorViewModel.Operation.DELETING_LESSON -> RS.sce_delete_lesson_progress
        ScheduleEditorViewModel.Operation.DELETING_SEMESTER -> RS.sce_delete_progress
        null -> null
    }?.let { ProgressDialog(stringResource(it)) }

    var showHomeworksCounterOperation by remember { mutableStateOf(false) }
    if (showHomeworksCounterOperation) {
        ProgressDialog(stringResource(RS.le_delete_homeworks_progress))
    }

    var showDeleteSemesterDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteSemesterDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteSemesterDialog = false },
            text = { Text(text = stringResource(RS.sce_delete_message)) },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteSemesterDialog = false },
                    content = { Text(text = stringResource(RS.sce_delete_no)) },
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSemester()
                        showDeleteSemesterDialog = false
                    },
                    content = { Text(text = stringResource(RS.sce_delete_yes)) },
                )
            },
        )
    }

    var lessonForDeleteWithHomeworksDialog: Lesson? by rememberSaveable { mutableStateOf(null) }
    lessonForDeleteWithHomeworksDialog?.let { lesson ->
        AlertDialog(
            onDismissRequest = { lessonForDeleteWithHomeworksDialog = null },
            title = { Text(text = stringResource(RS.le_delete_homeworks_title)) },
            text = { Text(text = stringResource(RS.le_delete_homeworks_message)) },
            dismissButton = {
                TextButton(
                    onClick = { lessonForDeleteWithHomeworksDialog = null },
                    content = { Text(text = stringResource(RS.le_delete_homeworks_cancel)) },
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteLesson(lesson, false)
                        lessonForDeleteWithHomeworksDialog = null
                    },
                    content = { Text(text = stringResource(RS.le_delete_homeworks_no)) },
                )
                TextButton(
                    onClick = {
                        viewModel.deleteLesson(lesson, true)
                        lessonForDeleteWithHomeworksDialog = null
                    },
                    content = { Text(text = stringResource(RS.le_delete_homeworks_yes)) },
                )
            },
        )
    }

    var lessonForDeleteWithoutHomeworksDialog: Lesson? by rememberSaveable { mutableStateOf(null) }
    lessonForDeleteWithoutHomeworksDialog?.let { lesson ->
        AlertDialog(
            onDismissRequest = { lessonForDeleteWithoutHomeworksDialog = null },
            text = { Text(text = stringResource(RS.le_delete_message)) },
            dismissButton = {
                TextButton(
                    onClick = { lessonForDeleteWithoutHomeworksDialog = null },
                    content = { Text(text = stringResource(RS.le_delete_no)) },
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteLesson(lesson)
                        lessonForDeleteWithoutHomeworksDialog = null
                    },
                    content = { Text(text = stringResource(RS.le_delete_yes)) },
                )
            },
        )
    }

    val coroutineScope = rememberCoroutineScope()

    ScheduleEditorContent(
        rememberLessons = rememberLessons,
        onBackClick = navController::goBack,
        onEditSemesterClick = {
            navController.navigate(ScheduleRoute.SemesterEditor(semesterId = viewModel.semesterId))
        },
        onDeleteSemesterClick = { showDeleteSemesterDialog = true },
        onLessonClick = { lesson ->
            navController.navigate(
                ScheduleRoute.LessonEditor(semesterId = viewModel.semesterId, lessonId = lesson.id, copy = false),
            )
        },
        onCopyLessonClick = { lesson ->
            navController.navigate(
                ScheduleRoute.LessonEditor(semesterId = viewModel.semesterId, lessonId = lesson.id, copy = true),
            )
        },
        onDeleteLessonClick = { lesson ->
            showHomeworksCounterOperation = true
            coroutineScope.launch {
                if (viewModel.isLastLessonOfSubjectsAndHasHomeworks(lesson)) {
                    lessonForDeleteWithHomeworksDialog = lesson
                } else {
                    lessonForDeleteWithoutHomeworksDialog = lesson
                }
                showHomeworksCounterOperation = false
            }
        },
        onAddLessonClick = { dayOfWeek ->
            navController.navigate(
                ScheduleRoute.LessonEditor(semesterId = viewModel.semesterId, dayOfWeekValue = dayOfWeek.value),
            )
        },
    )
}
