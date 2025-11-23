package ru.erdenian.studentassistant.schedule.lessoneditor

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import ru.erdenian.studentassistant.repository.api.entity.Lesson
import ru.erdenian.studentassistant.sampledata.Lessons
import ru.erdenian.studentassistant.schedule.lessoneditor.composable.AutoCompleteTextField
import ru.erdenian.studentassistant.schedule.lessoneditor.composable.MultiAutoCompleteTextField
import ru.erdenian.studentassistant.schedule.lessoneditor.composable.WeekdayPicker
import ru.erdenian.studentassistant.schedule.lessoneditor.composable.WeeksSelector
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.AutoMirrored
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.dialog.TimePickerDialog
import ru.erdenian.studentassistant.uikit.placeholder.PlaceholderHighlight
import ru.erdenian.studentassistant.uikit.placeholder.fade
import ru.erdenian.studentassistant.uikit.placeholder.placeholder
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.TopAppBarActions

@Composable
internal fun LessonEditorContent(
    isProgress: Boolean,
    isEditing: Boolean,
    subjectName: String,
    existingSubjects: List<String>,
    subjectNameErrorMessage: String?,
    type: String,
    existingTypes: List<String>,
    teachers: String,
    existingTeachers: List<String>,
    classrooms: String,
    existingClassrooms: List<String>,
    startTime: LocalTime,
    endTime: LocalTime,
    dayOfWeek: DayOfWeek,
    weeks: List<Boolean>,
    isAdvancedWeeksSelectorEnabled: Boolean,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSubjectNameChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onTeachersChange: (String) -> Unit,
    onClassroomsChange: (String) -> Unit,
    onStartTimeChange: (LocalTime) -> Unit,
    onEndTimeChange: (LocalTime) -> Unit,
    onDayOfWeekChange: (DayOfWeek) -> Unit,
    onWeeksChange: (List<Boolean>) -> Unit,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(if (isEditing) RS.le_title_edit else RS.le_title_new)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = AppIcons.AutoMirrored.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    TopAppBarActions(
                        actions = listOfNotNull(
                            ActionItem.AlwaysShow(
                                name = stringResource(RS.le_save),
                                imageVector = AppIcons.Check,
                                loading = isProgress,
                                onClick = onSaveClick,
                            ),
                            if (isEditing) {
                                ActionItem.NeverShow(
                                    name = stringResource(RS.le_delete),
                                    loading = isProgress,
                                    onClick = onDeleteClick,
                                )
                            } else {
                                null
                            },
                        ),
                    )
                },
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
        modifier = Modifier.imePadding(),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(
                    horizontal = MaterialTheme.dimensions.screenPaddingHorizontal,
                    vertical = MaterialTheme.dimensions.screenPaddingVertical,
                ),
        ) {
            val timeFormatter = remember { DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT) }
            var timePickerData: Pair<LocalTime, (LocalTime) -> Unit>? by remember { mutableStateOf(null) }

            AutoCompleteTextField(
                value = subjectName,
                items = existingSubjects,
                onValueChange = onSubjectNameChange,
                enabled = !isProgress,
                label = { Text(text = stringResource(RS.le_subject_name)) },
                isError = (subjectNameErrorMessage != null),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next,
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(
                        visible = isProgress,
                        highlight = PlaceholderHighlight.fade(),
                    ),
            )

            AnimatedVisibility(subjectNameErrorMessage != null) {
                Text(
                    text = subjectNameErrorMessage.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp),
                )
            }

            AutoCompleteTextField(
                value = type,
                items = existingTypes,
                onValueChange = onTypeChange,
                enabled = !isProgress,
                label = { Text(text = stringResource(RS.le_type)) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next,
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .placeholder(
                        visible = isProgress,
                        highlight = PlaceholderHighlight.fade(),
                    ),
            )

            MultiAutoCompleteTextField(
                value = teachers,
                items = existingTeachers,
                onValueChange = onTeachersChange,
                enabled = !isProgress,
                label = { Text(text = stringResource(RS.le_teachers)) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next,
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .placeholder(
                        visible = isProgress,
                        highlight = PlaceholderHighlight.fade(),
                    ),
            )

            val focusManager = LocalFocusManager.current
            MultiAutoCompleteTextField(
                value = classrooms,
                items = existingClassrooms,
                onValueChange = onClassroomsChange,
                enabled = !isProgress,
                label = { Text(text = stringResource(RS.le_classrooms)) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() },
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .placeholder(
                        visible = isProgress,
                        highlight = PlaceholderHighlight.fade(),
                    ),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                Text(
                    text = stringResource(RS.le_start_time),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1.0f),
                )
                TextButton(
                    onClick = { timePickerData = startTime to onStartTimeChange },
                    enabled = !isProgress,
                    modifier = Modifier.placeholder(
                        visible = isProgress,
                        highlight = PlaceholderHighlight.fade(),
                    ),
                ) {
                    Text(text = startTime.format(timeFormatter))
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                Text(
                    text = stringResource(RS.le_end_time),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1.0f),
                )
                TextButton(
                    onClick = { timePickerData = endTime to onEndTimeChange },
                    enabled = !isProgress,
                    modifier = Modifier.placeholder(
                        visible = isProgress,
                        highlight = PlaceholderHighlight.fade(),
                    ),
                ) {
                    Text(text = endTime.format(timeFormatter))
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            WeekdayPicker(
                value = dayOfWeek,
                onValueChange = onDayOfWeekChange,
                enabled = !isProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(
                        visible = isProgress,
                        highlight = PlaceholderHighlight.fade(),
                    ),
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            key(isProgress) { // To reset WeekSelector's state when loading completes
                WeeksSelector(
                    weeks = weeks,
                    onWeeksChange = onWeeksChange,
                    isAdvancedMode = isAdvancedWeeksSelectorEnabled,
                    enabled = !isProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .placeholder(
                            visible = isProgress,
                            highlight = PlaceholderHighlight.fade(),
                        ),
                )
            }

            timePickerData?.let { (initialTime, onConfirm) ->
                TimePickerDialog(
                    onConfirm = { newValue ->
                        timePickerData = null
                        onConfirm(newValue)
                    },
                    onDismiss = { timePickerData = null },
                    initialTime = initialTime,
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LessonEditorContentPreview() = AppTheme {
    LessonEditorContent(
        isProgress = false,
        isEditing = true,
        subjectName = Lessons.regular.subjectName,
        existingSubjects = emptyList(),
        subjectNameErrorMessage = null,
        type = Lessons.regular.type,
        existingTypes = emptyList(),
        teachers = Lessons.regular.teachers.joinToString(),
        existingTeachers = emptyList(),
        classrooms = Lessons.regular.classrooms.joinToString(),
        existingClassrooms = emptyList(),
        startTime = Lessons.regular.startTime,
        endTime = Lessons.regular.endTime,
        dayOfWeek = (Lessons.regular.lessonRepeat as Lesson.Repeat.ByWeekday).dayOfWeek,
        weeks = (Lessons.regular.lessonRepeat as Lesson.Repeat.ByWeekday).weeks,
        isAdvancedWeeksSelectorEnabled = true,
        onBackClick = {},
        onSaveClick = {},
        onDeleteClick = {},
        onSubjectNameChange = {},
        onTypeChange = {},
        onTeachersChange = {},
        onClassroomsChange = {},
        onStartTimeChange = {},
        onEndTimeChange = {},
        onDayOfWeekChange = {},
        onWeeksChange = {},
    )
}

@Preview
@Composable
private fun LessonEditorContentLongPreview() = AppTheme {
    LessonEditorContent(
        isProgress = false,
        isEditing = true,
        subjectName = Lessons.long.subjectName,
        existingSubjects = emptyList(),
        subjectNameErrorMessage = null,
        type = Lessons.long.type,
        existingTypes = emptyList(),
        teachers = Lessons.long.teachers.joinToString(),
        existingTeachers = emptyList(),
        classrooms = Lessons.long.classrooms.joinToString(),
        existingClassrooms = emptyList(),
        startTime = Lessons.long.startTime,
        endTime = Lessons.long.endTime,
        dayOfWeek = (Lessons.long.lessonRepeat as Lesson.Repeat.ByWeekday).dayOfWeek,
        weeks = (Lessons.long.lessonRepeat as Lesson.Repeat.ByWeekday).weeks,
        isAdvancedWeeksSelectorEnabled = true,
        onBackClick = {},
        onSaveClick = {},
        onDeleteClick = {},
        onSubjectNameChange = {},
        onTypeChange = {},
        onTeachersChange = {},
        onClassroomsChange = {},
        onStartTimeChange = {},
        onEndTimeChange = {},
        onDayOfWeekChange = {},
        onWeeksChange = {},
    )
}

@Preview
@Composable
private fun LessonEditorContentLoadingPreview() = AppTheme {
    LessonEditorContent(
        isProgress = true,
        isEditing = true,
        subjectName = Lessons.regular.subjectName,
        existingSubjects = emptyList(),
        subjectNameErrorMessage = null,
        type = Lessons.regular.type,
        existingTypes = emptyList(),
        teachers = Lessons.regular.teachers.joinToString(),
        existingTeachers = emptyList(),
        classrooms = Lessons.regular.classrooms.joinToString(),
        existingClassrooms = emptyList(),
        startTime = Lessons.regular.startTime,
        endTime = Lessons.regular.endTime,
        dayOfWeek = (Lessons.regular.lessonRepeat as Lesson.Repeat.ByWeekday).dayOfWeek,
        weeks = (Lessons.regular.lessonRepeat as Lesson.Repeat.ByWeekday).weeks,
        isAdvancedWeeksSelectorEnabled = true,
        onBackClick = {},
        onSaveClick = {},
        onDeleteClick = {},
        onSubjectNameChange = {},
        onTypeChange = {},
        onTeachersChange = {},
        onClassroomsChange = {},
        onStartTimeChange = {},
        onEndTimeChange = {},
        onDayOfWeekChange = {},
        onWeeksChange = {},
    )
}
