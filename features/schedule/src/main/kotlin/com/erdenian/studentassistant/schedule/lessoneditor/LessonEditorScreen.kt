package com.erdenian.studentassistant.schedule.lessoneditor

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.erdenian.studentassistant.schedule.lessoneditor.LessonEditorViewModel.Error
import com.erdenian.studentassistant.strings.RA
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.uikit.dialog.ProgressDialog
import com.erdenian.studentassistant.utils.toSingleLine
import com.erdenian.studentassistant.utils.toast
import kotlinx.coroutines.launch

@Composable
fun LessonEditorScreen(
    viewModel: LessonEditorViewModel,
    navigateBack: () -> Unit
) {
    val done by viewModel.done.collectAsState()
    LaunchedEffect(done) {
        if (done) navigateBack()
    }

    var isSubjectNameChanged by rememberSaveable { mutableStateOf(false) }

    val isEditing = viewModel.isEditing

    val error by viewModel.error.collectAsState()
    val errorMessage = when (error) {
        Error.EMPTY_SUBJECT_NAME -> RS.le_error_empty_subject_name
        Error.WRONG_TIMES -> RS.le_error_wrong_time
        Error.EMPTY_REPEAT -> RS.le_error_empty_repeat
        null -> null
    }?.let { stringResource(it) }

    val subjectName by viewModel.subjectName.collectAsState()
    val existingSubjects by viewModel.existingSubjects.collectAsState()
    val subjectNameErrorMessage = errorMessage?.takeIf { (error == Error.EMPTY_SUBJECT_NAME) && isSubjectNameChanged }

    val type by viewModel.type.collectAsState()
    val predefinedTypes = stringArrayResource(RA.lesson_types).toList()
    val existingTypes by viewModel.existingTypes.collectAsState()
    val displayedTypes = (predefinedTypes + existingTypes.list).distinct()

    val teachers by viewModel.teachers.collectAsState()
    val existingTeachers by viewModel.existingTeachers.collectAsState()

    val classrooms by viewModel.classrooms.collectAsState()
    val existingClassrooms by viewModel.existingClassrooms.collectAsState()

    val startTime by viewModel.startTime.collectAsState()
    val endTime by viewModel.endTime.collectAsState()

    val dayOfWeek by viewModel.dayOfWeek.collectAsState()
    val weeks by viewModel.weeks.collectAsState()
    val isAdvancedWeeksSelectorEnabled by viewModel.isAdvancedWeeksSelectorEnabled.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val operation by viewModel.operation.collectAsState()

    val nonBlockingProgress: Boolean
    val blockingProgressMessageId: Int?
    when (operation) {
        LessonEditorViewModel.Operation.LOADING -> {
            nonBlockingProgress = true
            blockingProgressMessageId = null
        }
        LessonEditorViewModel.Operation.SAVING -> {
            nonBlockingProgress = false
            blockingProgressMessageId = RS.le_save_progress
        }
        LessonEditorViewModel.Operation.DELETING -> {
            nonBlockingProgress = false
            blockingProgressMessageId = RS.le_delete_progress
        }
        null -> {
            nonBlockingProgress = false
            blockingProgressMessageId = null
        }
    }

    var customOperaionMessageId by remember { mutableStateOf<Int?>(null) }
    (blockingProgressMessageId ?: customOperaionMessageId)?.let { ProgressDialog(stringResource(it)) }

    var showSaveDialog by rememberSaveable { mutableStateOf(false) }
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text(text = stringResource(RS.le_rename_others_title)) },
            text = { Text(text = stringResource(RS.le_rename_others_message)) },
            dismissButton = {
                TextButton(
                    onClick = { showSaveDialog = false },
                    content = { Text(text = stringResource(RS.le_rename_others_cancel)) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.save(false)
                        showSaveDialog = false
                    },
                    content = { Text(text = stringResource(RS.le_rename_others_no)) }
                )
                TextButton(
                    onClick = {
                        viewModel.save(true)
                        showSaveDialog = false
                    },
                    content = { Text(text = stringResource(RS.le_rename_others_yes)) }
                )
            }
        )
    }

    var showDeleteWithHomeworksDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteWithHomeworksDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteWithHomeworksDialog = false },
            title = { Text(text = stringResource(RS.le_delete_homeworks_title)) },
            text = { Text(text = stringResource(RS.le_delete_homeworks_message)) },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteWithHomeworksDialog = false },
                    content = { Text(text = stringResource(RS.le_delete_homeworks_cancel)) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.delete(false)
                        showDeleteWithHomeworksDialog = false
                    },
                    content = { Text(text = stringResource(RS.le_delete_homeworks_no)) }
                )
                TextButton(
                    onClick = {
                        viewModel.delete(true)
                        showDeleteWithHomeworksDialog = false
                    },
                    content = { Text(text = stringResource(RS.le_delete_homeworks_yes)) }
                )
            }
        )
    }

    var showDeleteWithoutHomeworksDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteWithoutHomeworksDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteWithoutHomeworksDialog = false },
            text = { Text(text = stringResource(RS.le_delete_message)) },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteWithoutHomeworksDialog = false },
                    content = { Text(text = stringResource(RS.le_delete_no)) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.delete()
                        showDeleteWithoutHomeworksDialog = false
                    },
                    content = { Text(text = stringResource(RS.le_delete_yes)) }
                )
            }
        )
    }

    LessonEditorContent(
        isProgress = nonBlockingProgress,
        isEditing = isEditing,
        subjectName = subjectName,
        existingSubjects = existingSubjects.list,
        subjectNameErrorMessage = subjectNameErrorMessage,
        type = type,
        existingTypes = displayedTypes,
        teachers = teachers,
        existingTeachers = existingTeachers.list,
        classrooms = classrooms,
        existingClassrooms = existingClassrooms.list,
        startTime = startTime,
        endTime = endTime,
        dayOfWeek = dayOfWeek,
        weeks = weeks,
        isAdvancedWeeksSelectorEnabled = isAdvancedWeeksSelectorEnabled,
        onBackClick = navigateBack,
        onSaveClick = {
            isSubjectNameChanged = true
            if (errorMessage != null) {
                context.toast(errorMessage)
            } else {
                customOperaionMessageId = RS.le_rename_others_progress
                coroutineScope.launch {
                    if (viewModel.isSubjectNameChangedAndNotLast()) showSaveDialog = true
                    else viewModel.save()
                    customOperaionMessageId = null
                }
            }
        },
        onDeleteClick = {
            customOperaionMessageId = RS.le_delete_homeworks_progress
            coroutineScope.launch {
                if (viewModel.isLastLessonOfSubjectsAndHasHomeworks()) showDeleteWithHomeworksDialog = true
                else showDeleteWithoutHomeworksDialog = true
                customOperaionMessageId = null
            }
        },
        onSubjectNameChange = { value ->
            isSubjectNameChanged = true
            viewModel.subjectName.value = value
        },
        onTypeChange = { viewModel.type.value = it.toSingleLine() },
        onTeachersChange = { viewModel.teachers.value = it.toSingleLine() },
        onClassroomsChange = { viewModel.classrooms.value = it.toSingleLine() },
        onStartTimeChange = { viewModel.startTime.value = it },
        onEndTimeChange = { viewModel.endTime.value = it },
        onDayOfWeekChange = { viewModel.dayOfWeek.value = it },
        onWeeksChange = { viewModel.weeks.value = it }
    )
}
