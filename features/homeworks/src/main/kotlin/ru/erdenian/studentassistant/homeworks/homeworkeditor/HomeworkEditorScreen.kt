package ru.erdenian.studentassistant.homeworks.homeworkeditor

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import ru.erdenian.studentassistant.homeworks.homeworkeditor.HomeworkEditorViewModel.Error
import ru.erdenian.studentassistant.sampledata.Homeworks
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.ExposedDropdownMenu
import ru.erdenian.studentassistant.uikit.view.ProgressDialog
import ru.erdenian.studentassistant.uikit.view.TopAppBarActions
import ru.erdenian.studentassistant.utils.showDatePicker
import ru.erdenian.studentassistant.utils.toSingleLine
import ru.erdenian.studentassistant.utils.toast

@Composable
fun HomeworkEditorScreen(
    viewModel: HomeworkEditorViewModel,
    navigateBack: () -> Unit,
    navigateToCreateLesson: (semesterId: Long, subjectName: String) -> Unit
) {
    var lessonNameToCreate by remember { mutableStateOf<String?>(null) }
    val done by viewModel.done.collectAsState()
    if (done) {
        DisposableEffect(done) {
            navigateBack()
            lessonNameToCreate?.let { navigateToCreateLesson(viewModel.semesterId, it) }
            onDispose {}
        }
    }

    val operation by viewModel.operation.collectAsState()

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

    val context = LocalContext.current

    HomeworkEditorContent(
        operation = operation,
        isEditing = viewModel.isEditing,
        existingSubjects = existingSubjects.list,
        subjectName = subjectName,
        deadline = deadline,
        description = description,
        semesterDates = semesterDatesRange,
        onBackClick = navigateBack,
        onSaveClick = {
            if (errorMessage != null) {
                context.toast(errorMessage)
            } else {
                if (viewModel.lessonExists) {
                    viewModel.save()
                } else {
                    MaterialAlertDialogBuilder(context)
                        .setTitle(RS.he_unknown_lesson)
                        .setMessage(RS.he_unknown_lesson_message)
                        .setPositiveButton(RS.he_unknown_lesson_yes) { _, _ -> viewModel.save() }
                        .setNegativeButton(RS.he_unknown_lesson_no, null)
                        .setNeutralButton(RS.he_unknown_lesson_yes_and_create) { _, _ ->
                            lessonNameToCreate = subjectName
                            viewModel.save()
                        }
                        .show()
                }
            }
        },
        onDeleteClick = { viewModel.delete() },
        onSubjectNameChange = { viewModel.subjectName.value = it.toSingleLine() },
        onDeadlineChange = { viewModel.deadline.value = it },
        onDescriptionChange = { viewModel.description.value = it }
    )
}

@Composable
private fun HomeworkEditorContent(
    operation: HomeworkEditorViewModel.Operation?,
    isEditing: Boolean,
    existingSubjects: List<String>,
    subjectName: String,
    deadline: LocalDate,
    description: String,
    semesterDates: ClosedRange<LocalDate>,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSubjectNameChange: (String) -> Unit,
    onDeadlineChange: (LocalDate) -> Unit,
    onDescriptionChange: (String) -> Unit
) {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(RS.he_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = AppIcons.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    TopAppBarActions(
                        actions = listOfNotNull(
                            ActionItem.AlwaysShow(
                                name = stringResource(RS.he_save),
                                imageVector = AppIcons.Check,
                                onClick = onSaveClick,
                                loading = nonBlockingProgress
                            ),
                            if (isEditing) {
                                val context = LocalContext.current
                                ActionItem.NeverShow(
                                    name = stringResource(RS.he_delete),
                                    onClick = {
                                        MaterialAlertDialogBuilder(context)
                                            .setMessage(RS.he_delete_message)
                                            .setPositiveButton(RS.he_delete_yes) { _, _ -> onDeleteClick() }
                                            .setNegativeButton(RS.he_delete_no, null)
                                            .show()
                                    },
                                    loading = nonBlockingProgress
                                )
                            } else null
                        )
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = MaterialTheme.dimensions.activityHorizontalMargin,
                vertical = MaterialTheme.dimensions.activityVerticalMargin
            )
        ) {
            val descriptionFocusRequester = remember { FocusRequester() }

            ExposedDropdownMenu(
                value = subjectName,
                items = existingSubjects,
                onValueChange = onSubjectNameChange,
                enabled = !nonBlockingProgress,
                label = stringResource(RS.he_subject),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { descriptionFocusRequester.requestFocus() }
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(
                        visible = nonBlockingProgress,
                        highlight = PlaceholderHighlight.shimmer()
                    )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                val context = LocalContext.current

                Text(
                    text = stringResource(RS.he_deadline_label),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.weight(1.0f)
                )
                TextButton(
                    onClick = {
                        context.showDatePicker(
                            deadline,
                            semesterDates.start,
                            semesterDates.endInclusive
                        ) { onDeadlineChange(it) }
                    },
                    enabled = !nonBlockingProgress,
                    modifier = Modifier.placeholder(
                        visible = nonBlockingProgress,
                        highlight = PlaceholderHighlight.shimmer()
                    )
                ) {
                    val deadlineFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT) }
                    Text(text = deadline.format(deadlineFormatter))
                }
            }

            AnimatedVisibility(
                visible = !nonBlockingProgress,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SimpleTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text(text = stringResource(RS.he_description)) },
                    enabled = !nonBlockingProgress,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier
                        .fillMaxSize()
                        .focusRequester(descriptionFocusRequester)
                )
            }
        }
    }
}

@Composable
private fun SimpleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    colors: TextFieldColors = TextFieldDefaults.textFieldColors()
) = Box {
    val textStyle = LocalTextStyle.current
    val textColor = textStyle.color.takeOrElse { colors.textColor(true).value }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        textStyle = textStyle.merge(TextStyle(color = textColor)),
        keyboardOptions = keyboardOptions,
        modifier = modifier
    )

    if (value.isEmpty() && (label != null)) {
        val contentColor = colors.labelColor(
            enabled = true,
            error = false,
            interactionSource = remember { MutableInteractionSource() }
        ).value
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalContentAlpha provides contentColor.alpha,
            content = label
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeworkEditorContentRegularPreview() = AppTheme {
    HomeworkEditorContent(
        operation = null,
        isEditing = true,
        existingSubjects = emptyList(),
        subjectName = Homeworks.regular.subjectName,
        deadline = Homeworks.regular.deadline,
        description = Homeworks.regular.description,
        semesterDates = LocalDate.of(2021, 9, 1)..LocalDate.of(2022, 6, 30),
        onBackClick = {},
        onSaveClick = {},
        onDeleteClick = {},
        onSubjectNameChange = {},
        onDeadlineChange = {},
        onDescriptionChange = {}
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeworkEditorContentEmptyPreview() = AppTheme {
    HomeworkEditorContent(
        operation = null,
        isEditing = true,
        existingSubjects = emptyList(),
        subjectName = "",
        deadline = LocalDate.of(2021, 10, 3),
        description = "",
        semesterDates = LocalDate.of(2021, 9, 1)..LocalDate.of(2022, 6, 30),
        onBackClick = {},
        onSaveClick = {},
        onDeleteClick = {},
        onSubjectNameChange = {},
        onDeadlineChange = {},
        onDescriptionChange = {}
    )
}

@Preview
@Composable
private fun HomeworkEditorContentLongPreview() = AppTheme {
    HomeworkEditorContent(
        operation = null,
        isEditing = true,
        existingSubjects = emptyList(),
        subjectName = Homeworks.long.subjectName,
        deadline = Homeworks.long.deadline,
        description = Homeworks.long.description,
        semesterDates = LocalDate.of(2021, 9, 1)..LocalDate.of(2022, 6, 30),
        onBackClick = {},
        onSaveClick = {},
        onDeleteClick = {},
        onSubjectNameChange = {},
        onDeadlineChange = {},
        onDescriptionChange = {}
    )
}
