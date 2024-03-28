package com.erdenian.studentassistant.homeworks.homeworkeditor

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.erdenian.studentassistant.homeworks.homeworkeditor.HomeworkEditorViewModel.Error
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.uikit.dialog.ProgressDialog
import com.erdenian.studentassistant.utils.toSingleLine
import com.erdenian.studentassistant.utils.toast

@Composable
fun HomeworkEditorScreen(
    viewModel: HomeworkEditorViewModel,
    navigateBack: () -> Unit,
    navigateToCreateLesson: (semesterId: Long, subjectName: String) -> Unit
) {
    var lessonNameToCreate by remember { mutableStateOf<String?>(null) }
    val done by viewModel.done.collectAsState()
    LaunchedEffect(done) {
        if (done) {
            navigateBack()
            lessonNameToCreate?.let { navigateToCreateLesson(viewModel.semesterId, it) }
        }
    }

    val subjectName by viewModel.subjectName.collectAsState()
    val description by viewModel.description.collectAsState()
    val deadline by viewModel.deadline.collectAsState()

    val semesterDatesRange by viewModel.semesterDatesRange.collectAsState()
    val existingSubjects by viewModel.existingSubjects.collectAsState()

    val error by viewModel.error.collectAsState()
    val errorMessage = when (error) {
        Error.EMPTY_SUBJECT -> RS.he_error_empty_subject_name
        Error.EMPTY_DESCRIPTION -> RS.he_error_empty_description
        null -> null
    }?.let { stringResource(it) }

    val operation by viewModel.operation.collectAsState()

    val nonBlockingProgress: Boolean
    val blockingProgressMessageId: Int?
    when (operation) {
        HomeworkEditorViewModel.Operation.LOADING -> {
            nonBlockingProgress = true
            blockingProgressMessageId = null
        }
        HomeworkEditorViewModel.Operation.SAVING -> {
            nonBlockingProgress = false
            blockingProgressMessageId = RS.he_save_progress
        }
        HomeworkEditorViewModel.Operation.DELETING -> {
            nonBlockingProgress = false
            blockingProgressMessageId = RS.he_delete_progress
        }
        null -> {
            nonBlockingProgress = false
            blockingProgressMessageId = null
        }
    }

    if (blockingProgressMessageId != null) {
        ProgressDialog(stringResource(blockingProgressMessageId))
    }

    var showSaveDialog by rememberSaveable { mutableStateOf(false) }
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text(text = stringResource(RS.he_unknown_lesson)) },
            text = { Text(text = stringResource(RS.he_unknown_lesson_message)) },
            dismissButton = {
                TextButton(
                    onClick = { showSaveDialog = false },
                    content = { Text(text = stringResource(RS.he_unknown_lesson_no)) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        lessonNameToCreate = subjectName
                        viewModel.save()
                        showSaveDialog = false
                    },
                    content = { Text(text = stringResource(RS.he_unknown_lesson_yes_and_create)) }
                )
                TextButton(
                    onClick = {
                        viewModel.save()
                        showSaveDialog = false
                    },
                    content = { Text(text = stringResource(RS.he_unknown_lesson_yes)) }
                )
            }
        )
    }

    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            text = { Text(text = stringResource(RS.he_delete_message)) },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    content = { Text(text = stringResource(RS.he_delete_no)) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.delete()
                        showDeleteDialog = false
                    },
                    content = { Text(text = stringResource(RS.he_delete_yes)) }
                )
            }
        )
    }

    val context = LocalContext.current
    HomeworkEditorContent(
        isProgress = nonBlockingProgress,
        isEditing = viewModel.isEditing,
        existingSubjects = existingSubjects.list,
        subjectName = subjectName,
        deadline = deadline,
        description = description,
        semesterDates = semesterDatesRange,
        onBackClick = navigateBack,
        onSaveClick = {
            when {
                (errorMessage != null) -> context.toast(errorMessage)
                viewModel.lessonExists -> viewModel.save()
                else -> showSaveDialog = true
            }
        },
        onDeleteClick = { showDeleteDialog = true },
        onSubjectNameChange = { viewModel.subjectName.value = it.toSingleLine() },
        onDeadlineChange = { viewModel.deadline.value = it },
        onDescriptionChange = { viewModel.description.value = it }
    )
}
