package ru.erdenian.studentassistant.homeworks.homeworkeditor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import ru.erdenian.studentassistant.sampledata.Homeworks
import ru.erdenian.studentassistant.sampledata.Lessons
import ru.erdenian.studentassistant.sampledata.Semesters
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.AutoMirrored
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.dialog.DatePickerDialog
import ru.erdenian.studentassistant.uikit.placeholder.PlaceholderHighlight
import ru.erdenian.studentassistant.uikit.placeholder.fade
import ru.erdenian.studentassistant.uikit.placeholder.placeholder
import ru.erdenian.studentassistant.uikit.utils.ScreenPreviews
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.DateField
import ru.erdenian.studentassistant.uikit.view.TopAppBarActions

/**
 * UI контент экрана редактора домашнего задания.
 *
 * @param isProgress флаг загрузки.
 * @param isEditing режим редактирования.
 * @param existingSubjects список существующих предметов.
 * @param subjectName название предмета.
 * @param deadline срок сдачи.
 * @param description описание.
 * @param semesterDates диапазон дат расписания (для ограничения выбора даты).
 * @param onBackClick колбэк нажатия назад.
 * @param onSaveClick колбэк сохранения.
 * @param onDeleteClick колбэк удаления.
 * @param onSubjectNameChange колбэк изменения названия.
 * @param onDeadlineChange колбэк изменения дедлайна.
 * @param onDescriptionChange колбэк изменения описания.
 */
@OptIn(ExperimentalFoundationApi::class)
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
    onDescriptionChange: (String) -> Unit,
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
                            loading = isProgress,
                        ),
                        if (isEditing) {
                            ActionItem.NeverShow(
                                name = stringResource(RS.he_delete),
                                onClick = onDeleteClick,
                                loading = isProgress,
                            )
                        } else {
                            null
                        },
                    ),
                )
            },
        )
    },
    modifier = Modifier.imePadding(),
) { paddingValues ->
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(paddingValues)
            .padding(
                horizontal = MaterialTheme.dimensions.screenPaddingHorizontal,
                vertical = MaterialTheme.dimensions.screenPaddingVertical,
            )
            .fillMaxSize(),
    ) {
        val descriptionFocusRequester = remember { FocusRequester() }
        val currentExistingSubjects by rememberUpdatedState(existingSubjects)

        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded && currentExistingSubjects.isNotEmpty() },
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
                    if (existingSubjects.isNotEmpty()) {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { descriptionFocusRequester.requestFocus() },
                ),
                singleLine = true,
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                    .fillMaxWidth()
                    .placeholder(
                        visible = isProgress,
                        highlight = PlaceholderHighlight.fade(),
                    ),
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                existingSubjects.forEach { subject ->
                    DropdownMenuItem(
                        text = { Text(text = subject) },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        onClick = {
                            textFieldValueState = textFieldValueState.copy(
                                text = subject,
                                selection = TextRange(subject.length),
                            )
                            onSubjectNameChange(subject)
                            expanded = false
                        },
                    )
                }
            }
        }

        var showDatePicker by remember { mutableStateOf(false) }
        DateField(
            value = deadline,
            label = stringResource(RS.he_deadline_label),
            onClick = { showDatePicker = true },
            enabled = !isProgress,
            modifier = Modifier
                .fillMaxWidth()
                .placeholder(
                    visible = isProgress,
                    highlight = PlaceholderHighlight.fade(),
                ),
        )

        if (showDatePicker) {
            DatePickerDialog(
                onConfirm = { newValue ->
                    showDatePicker = false
                    onDeadlineChange(newValue)
                },
                onDismiss = { showDatePicker = false },
                initialSelectedDate = deadline,
                datesRange = semesterDates,
            )
        }

        // Используем TextFieldState для нового API
        val descriptionState = rememberTextFieldState(initialText = description)

        // Синхронизация: если описание изменилось извне (ViewModel), обновляем State
        LaunchedEffect(description) {
            if (descriptionState.text.toString() != description) {
                descriptionState.edit { replace(0, length, description) }
            }
        }

        // Синхронизация: слушаем изменения State и передаем их наверх
        LaunchedEffect(descriptionState) {
            snapshotFlow { descriptionState.text }
                .collect { onDescriptionChange(it.toString()) }
        }

        // Используем перегрузку с TextFieldState, так как она автоматически обрабатывает
        // прокрутку к курсору (bringIntoView) при открытии клавиатуры и изменении размера поля,
        // в отличие от старых перегрузок со String/TextFieldValue.
        OutlinedTextField(
            state = descriptionState,
            label = { Text(text = stringResource(RS.he_description)) },
            enabled = !isProgress,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier
                .fillMaxSize()
                .placeholder(
                    visible = isProgress,
                    highlight = PlaceholderHighlight.fade(),
                )
                .focusRequester(descriptionFocusRequester),
        )
    }
}

private data class HomeworkEditorContentPreviewData(
    val isProgress: Boolean,
    val isEditing: Boolean,
    val subjectName: String,
    val description: String,
    val existingSubjects: List<String> = emptyList(),
)

private class HomeworkEditorContentPreviewParameterProvider :
    PreviewParameterProvider<HomeworkEditorContentPreviewData> {
    override val values = sequenceOf(
        HomeworkEditorContentPreviewData(
            isProgress = true,
            isEditing = false,
            subjectName = "",
            description = "",
        ),
        HomeworkEditorContentPreviewData(
            isProgress = false,
            isEditing = true,
            subjectName = "",
            description = "",
            existingSubjects = listOf(""),
        ),
        HomeworkEditorContentPreviewData(
            isProgress = false,
            isEditing = true,
            subjectName = Homeworks.regular.subjectName,
            description = Homeworks.regular.description,
        ),
        HomeworkEditorContentPreviewData(
            isProgress = false,
            isEditing = true,
            subjectName = Homeworks.long.subjectName,
            description = Homeworks.long.description,
            existingSubjects = listOf(Lessons.long.subjectName),
        ),
    )
}

@ScreenPreviews
@Composable
private fun HomeworkEditorContentPreview(
    @PreviewParameter(HomeworkEditorContentPreviewParameterProvider::class) data: HomeworkEditorContentPreviewData,
) = AppTheme {
    HomeworkEditorContent(
        isProgress = data.isProgress,
        isEditing = data.isEditing,
        existingSubjects = data.existingSubjects,
        subjectName = data.subjectName,
        deadline = Homeworks.regular.deadline,
        description = data.description,
        semesterDates = Semesters.regular.dateRange,
        onBackClick = {},
        onSaveClick = {},
        onDeleteClick = {},
        onSubjectNameChange = {},
        onDeadlineChange = {},
        onDescriptionChange = {},
    )
}
