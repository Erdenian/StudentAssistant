package ru.erdenian.studentassistant.ui.main.homeworkeditor

import android.content.res.Configuration
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.ui.main.homeworkeditor.HomeworkEditorViewModel.Error
import ru.erdenian.studentassistant.uikit.style.AppIcons
import ru.erdenian.studentassistant.uikit.style.AppTheme
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.ExposedDropdownMenu
import ru.erdenian.studentassistant.uikit.view.TopAppBarActions
import ru.erdenian.studentassistant.utils.Homeworks
import ru.erdenian.studentassistant.utils.showDatePicker
import ru.erdenian.studentassistant.utils.toast

@Composable
fun HomeworkEditorScreen(
    viewModel: HomeworkEditorViewModel,
    navigateBack: () -> Unit,
    navigateToCreateLesson: (semesterId: Long, subjectName: String) -> Unit
) {
    val done by viewModel.done.collectAsState()
    if (done) {
        DisposableEffect(done) {
            navigateBack()
            onDispose {}
        }
    }

    val subjectName by viewModel.subjectName.collectAsState()
    val description by viewModel.description.collectAsState()
    val deadline by viewModel.deadline.collectAsState()

    val semesterDatesRange by viewModel.semesterDatesRange.collectAsState()
    val existingSubjects by viewModel.existingSubjects.collectAsState()

    val error by viewModel.error.collectAsState()
    val errorMessage = when (error) {
        Error.EMPTY_SUBJECT -> R.string.hef_error_empty_subject_name
        Error.EMPTY_DESCRIPTION -> R.string.hef_error_empty_description
        null -> null
    }?.let { stringResource(it) }

    val isLoaded by viewModel.isLoaded.collectAsState()

    val context = LocalContext.current

    HomeworkEditorContent(
        isLoaded = isLoaded,
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
                        .setTitle(R.string.hef_unknown_lesson)
                        .setMessage(R.string.hef_unknown_lesson_message)
                        .setPositiveButton(R.string.hef_unknown_lesson_yes) { _, _ -> viewModel.save() }
                        .setNegativeButton(R.string.hef_unknown_lesson_no, null)
                        .setNeutralButton(R.string.hef_unknown_lesson_yes_and_create) { _, _ ->
                            viewModel.save()
                            navigateToCreateLesson(viewModel.semesterId, subjectName)
                        }
                        .show()
                }
            }
        },
        onDeleteClick = {
            MaterialAlertDialogBuilder(context)
                .setMessage(R.string.hef_delete_message)
                .setPositiveButton(R.string.hef_delete_yes) { _, _ -> viewModel.delete() }
                .setNegativeButton(R.string.hef_delete_no, null)
                .show()
        },
        onSubjectNameChange = { value, _ -> viewModel.subjectName.value = value },
        onDeadlineChange = { viewModel.deadline.value = it },
        onDescriptionChange = { viewModel.description.value = it }
    )
}

@Composable
private fun HomeworkEditorContent(
    isLoaded: Boolean,
    isEditing: Boolean,
    existingSubjects: List<String>,
    subjectName: String,
    deadline: LocalDate,
    description: String,
    semesterDates: ClosedRange<LocalDate>,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSubjectNameChange: (String, Int) -> Unit,
    onDeadlineChange: (LocalDate) -> Unit,
    onDescriptionChange: (String) -> Unit
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(text = stringResource(R.string.hef_title)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = AppIcons.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                TopAppBarActions(
                    actions = listOfNotNull(
                        ActionItem.AlwaysShow(
                            name = stringResource(R.string.hef_save),
                            imageVector = AppIcons.Check,
                            onClick = onSaveClick,
                            enabled = isLoaded
                        ),
                        if (isEditing) {
                            ActionItem.NeverShow(
                                name = stringResource(R.string.hef_delete),
                                onClick = onDeleteClick,
                                enabled = isLoaded
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
            horizontal = dimensionResource(R.dimen.activity_horizontal_margin),
            vertical = dimensionResource(R.dimen.activity_vertical_margin)
        )
    ) {
        val descriptionFocusRequester = remember { FocusRequester() }

        ExposedDropdownMenu(
            value = subjectName,
            items = existingSubjects,
            onValueChange = onSubjectNameChange,
            enabled = isLoaded,
            label = stringResource(R.string.hef_subject),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { descriptionFocusRequester.requestFocus() }
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            val context = LocalContext.current

            Text(
                text = stringResource(R.string.hef_deadline_label),
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
                enabled = isLoaded
            ) {
                val deadlineFormatter = remember { DateTimeFormat.shortDate() }
                Text(text = deadline.toString(deadlineFormatter))
            }
        }

        SimpleTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text(text = stringResource(R.string.hef_description)) },
            enabled = isLoaded,
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(descriptionFocusRequester)
        )
    }
}

@Composable
private fun SimpleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors()
) = Box {
    val textStyle = LocalTextStyle.current
    val textColor = textStyle.color.takeOrElse { colors.textColor(true).value }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        textStyle = textStyle.merge(TextStyle(color = textColor)),
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
        isLoaded = true,
        isEditing = true,
        existingSubjects = emptyList(),
        subjectName = Homeworks.regular.subjectName,
        deadline = Homeworks.regular.deadline,
        description = Homeworks.regular.description,
        semesterDates = LocalDate(2021, 9, 1)..LocalDate(2022, 6, 30),
        onBackClick = {},
        onSaveClick = {},
        onDeleteClick = {},
        onSubjectNameChange = { _, _ -> },
        onDeadlineChange = {},
        onDescriptionChange = {}
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeworkEditorContentEmptyPreview() = AppTheme {
    HomeworkEditorContent(
        isLoaded = true,
        isEditing = true,
        existingSubjects = emptyList(),
        subjectName = "",
        deadline = LocalDate(2021, 10, 3),
        description = "",
        semesterDates = LocalDate(2021, 9, 1)..LocalDate(2022, 6, 30),
        onBackClick = {},
        onSaveClick = {},
        onDeleteClick = {},
        onSubjectNameChange = { _, _ -> },
        onDeadlineChange = {},
        onDescriptionChange = {}
    )
}

@Preview
@Composable
private fun HomeworkEditorContentLongPreview() = AppTheme {
    HomeworkEditorContent(
        isLoaded = true,
        isEditing = true,
        existingSubjects = emptyList(),
        subjectName = Homeworks.long.subjectName,
        deadline = Homeworks.long.deadline,
        description = Homeworks.long.description,
        semesterDates = LocalDate(2021, 9, 1)..LocalDate(2022, 6, 30),
        onBackClick = {},
        onSaveClick = {},
        onDeleteClick = {},
        onSubjectNameChange = { _, _ -> },
        onDeadlineChange = {},
        onDescriptionChange = {}
    )
}