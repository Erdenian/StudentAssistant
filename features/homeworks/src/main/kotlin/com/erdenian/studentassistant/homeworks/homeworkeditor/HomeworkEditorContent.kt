package com.erdenian.studentassistant.homeworks.homeworkeditor

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erdenian.studentassistant.sampledata.Homeworks
import com.erdenian.studentassistant.sampledata.Lessons
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.style.AppIcons
import com.erdenian.studentassistant.style.AppTheme
import com.erdenian.studentassistant.style.AutoMirrored
import com.erdenian.studentassistant.style.dimensions
import com.erdenian.studentassistant.uikit.dialog.DatePickerDialog
import com.erdenian.studentassistant.uikit.view.ActionItem
import com.erdenian.studentassistant.uikit.view.TopAppBarActions
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material3.placeholder
import com.google.accompanist.placeholder.material3.shimmer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
internal fun HomeworkEditorContent(
    isProgress: Boolean,
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
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(text = stringResource(RS.he_title)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = AppIcons.AutoMirrored.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                TopAppBarActions(
                    actions = listOfNotNull(
                        ActionItem.AlwaysShow(
                            name = stringResource(RS.he_save),
                            imageVector = AppIcons.Check,
                            onClick = onSaveClick,
                            loading = isProgress
                        ),
                        if (isEditing) {
                            ActionItem.NeverShow(
                                name = stringResource(RS.he_delete),
                                onClick = onDeleteClick,
                                loading = isProgress
                            )
                        } else null
                    )
                )
            }
        )
    },
    modifier = Modifier.imePadding()
) { paddingValues ->
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(
                horizontal = MaterialTheme.dimensions.screenPaddingHorizontal,
                vertical = MaterialTheme.dimensions.screenPaddingVertical
            )
    ) {
        val descriptionFocusRequester = remember { FocusRequester() }
        val currentExistingSubjects by rememberUpdatedState(existingSubjects)

        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded && currentExistingSubjects.isNotEmpty() }
        ) {
            var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = subjectName)) }
            val textFieldValue = textFieldValueState.copy(text = subjectName)

            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { value ->
                    textFieldValueState = value
                    if (subjectName != value.text) onSubjectNameChange(value.text)
                },
                enabled = !isProgress,
                label = { Text(text = stringResource(RS.he_subject)) },
                trailingIcon = {
                    if (existingSubjects.isNotEmpty())
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { descriptionFocusRequester.requestFocus() }
                ),
                singleLine = true,
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .placeholder(
                        visible = isProgress,
                        highlight = PlaceholderHighlight.shimmer()
                    )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                focusable = false
            ) {
                existingSubjects.forEach { subject ->
                    DropdownMenuItem(
                        text = { Text(text = subject) },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        onClick = {
                            textFieldValueState = textFieldValueState.copy(
                                text = subject,
                                selection = TextRange(subject.length)
                            )
                            onSubjectNameChange(subject)
                            expanded = false
                        }
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            var showDatePicker by remember { mutableStateOf(false) }

            Text(
                text = stringResource(RS.he_deadline_label),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1.0f)
            )

            TextButton(
                onClick = { showDatePicker = true },
                enabled = !isProgress,
                modifier = Modifier.placeholder(
                    visible = isProgress,
                    highlight = PlaceholderHighlight.shimmer()
                )
            ) {
                val deadlineFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT) }
                Text(text = deadline.format(deadlineFormatter))
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onConfirm = { newValue ->
                        showDatePicker = false
                        onDeadlineChange(newValue)
                    },
                    onDismiss = { showDatePicker = false },
                    initialSelectedDate = deadline,
                    datesRange = semesterDates
                )
            }
        }

        AnimatedVisibility(
            visible = !isProgress,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            SimpleTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text(text = stringResource(RS.he_description)) },
                enabled = !isProgress,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(descriptionFocusRequester)
            )
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
    colors: TextFieldColors = TextFieldDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) = Box {
    @Composable
    fun TextFieldColors.textColor(
        enabled: Boolean,
        interactionSource: InteractionSource
    ): State<Color> {
        val focused by interactionSource.collectIsFocusedAsState()

        val targetValue = when {
            !enabled -> disabledTextColor
            focused -> focusedTextColor
            else -> unfocusedTextColor
        }
        return rememberUpdatedState(targetValue)
    }

    val textStyle = LocalTextStyle.current
    val textColor = textStyle.color.takeOrElse { colors.textColor(enabled, interactionSource).value }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        textStyle = textStyle.merge(TextStyle(color = textColor)),
        keyboardOptions = keyboardOptions,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        interactionSource = interactionSource,
        modifier = modifier
    )

    if (value.isEmpty() && (label != null)) {
        @Composable
        fun TextFieldColors.labelColor(enabled: Boolean): State<Color> {
            val targetValue = when {
                !enabled -> disabledLabelColor
                else -> unfocusedLabelColor
            }
            return rememberUpdatedState(targetValue)
        }

        val contentColor = colors.labelColor(enabled).value
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            content = label
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeworkEditorContentRegularPreview() = AppTheme {
    HomeworkEditorContent(
        isProgress = false,
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
        isProgress = false,
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
        isProgress = false,
        isEditing = true,
        existingSubjects = listOf(Lessons.long.subjectName),
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

@Preview
@Composable
private fun HomeworkEditorContentLoadingPreview() = AppTheme {
    HomeworkEditorContent(
        isProgress = true,
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
