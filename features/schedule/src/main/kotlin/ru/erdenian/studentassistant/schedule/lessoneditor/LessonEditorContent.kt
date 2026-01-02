package ru.erdenian.studentassistant.schedule.lessoneditor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalTime
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
import ru.erdenian.studentassistant.uikit.utils.ScreenPreviews
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.TimeField
import ru.erdenian.studentassistant.uikit.view.TopAppBarActions

/**
 * UI контент экрана редактора занятия.
 *
 * Отображает форму для ввода данных о занятии (предмет, время, повторения и т.д.).
 *
 * @param isProgress флаг загрузки.
 * @param isEditing режим редактирования.
 * @param subjectName название предмета.
 * @param existingSubjects список существующих предметов (для автодополнения).
 * @param subjectNameErrorMessage сообщение об ошибке названия.
 * @param type тип занятия.
 * @param existingTypes список существующих типов (для автодополнения).
 * @param teachers преподаватели.
 * @param existingTeachers список существующих преподавателей.
 * @param classrooms аудитории.
 * @param existingClassrooms список существующих аудиторий.
 * @param startTime время начала.
 * @param endTime время окончания.
 * @param dayOfWeek день недели.
 * @param weeks список недель повторения.
 * @param isAdvancedWeeksSelectorEnabled включен ли расширенный режим выбора недель.
 * @param onBackClick колбэк нажатия назад.
 * @param onSaveClick колбэк сохранения.
 * @param onDeleteClick колбэк удаления.
 * @param onSubjectNameChange колбэк изменения названия.
 * @param onTypeChange колбэк изменения типа.
 * @param onTeachersChange колбэк изменения преподавателей.
 * @param onClassroomsChange колбэк изменения аудиторий.
 * @param onStartTimeChange колбэк изменения времени начала.
 * @param onEndTimeChange колбэк изменения времени окончания.
 * @param onDayOfWeekChange колбэк изменения дня недели.
 * @param onWeeksChange колбэк изменения недель повторения.
 */
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
                        Icon(
                            imageVector = AppIcons.AutoMirrored.ArrowBack,
                            contentDescription = stringResource(RS.u_back),
                        )
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(
                    horizontal = MaterialTheme.dimensions.screenPaddingHorizontal,
                    vertical = MaterialTheme.dimensions.screenPaddingVertical,
                ),
        ) {
            var timePickerData: Pair<LocalTime, (LocalTime) -> Unit>? by remember { mutableStateOf(null) }

            // Subject Name
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
            }

            // Type
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
                    .placeholder(
                        visible = isProgress,
                        highlight = PlaceholderHighlight.fade(),
                    ),
            )

            // Teachers
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
                    .placeholder(
                        visible = isProgress,
                        highlight = PlaceholderHighlight.fade(),
                    ),
            )

            // Classrooms
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
                    .placeholder(
                        visible = isProgress,
                        highlight = PlaceholderHighlight.fade(),
                    ),
            )

            // Time
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                TimeField(
                    value = startTime,
                    label = stringResource(RS.le_start_time),
                    onClick = { timePickerData = startTime to onStartTimeChange },
                    enabled = !isProgress,
                    modifier = Modifier
                        .weight(1f)
                        .placeholder(
                            visible = isProgress,
                            highlight = PlaceholderHighlight.fade(),
                        ),
                )

                TimeField(
                    value = endTime,
                    label = stringResource(RS.le_end_time),
                    onClick = { timePickerData = endTime to onEndTimeChange },
                    enabled = !isProgress,
                    modifier = Modifier
                        .weight(1f)
                        .placeholder(
                            visible = isProgress,
                            highlight = PlaceholderHighlight.fade(),
                        ),
                )
            }

            // Day of Week
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(RS.le_day_of_week),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp),
                )
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
            }

            // Weeks
            key(isProgress) { // Чтобы сбросить состояние WeekSelector при завершении загрузки
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

private data class LessonEditorContentPreviewData(
    val isProgress: Boolean,
    val isEditing: Boolean,
    val subjectName: String,
    val subjectNameErrorMessage: String? = null,
    val type: String,
    val teachers: String,
    val classrooms: String,
    val weeks: List<Boolean>,
    val isAdvanced: Boolean = true,
)

private class LessonEditorContentPreviewParameterProvider : PreviewParameterProvider<LessonEditorContentPreviewData> {
    override val values = sequenceOf(
        LessonEditorContentPreviewData(
            isProgress = true,
            isEditing = false,
            subjectName = "",
            type = "",
            teachers = "",
            classrooms = "",
            weeks = listOf(true),
        ),
        LessonEditorContentPreviewData(
            isProgress = false,
            isEditing = true,
            subjectName = "",
            type = "",
            teachers = "",
            classrooms = "",
            weeks = listOf(true),
            isAdvanced = false,
        ),
        LessonEditorContentPreviewData(
            isProgress = false,
            isEditing = true,
            subjectName = "",
            subjectNameErrorMessage = "Введите название занятия",
            type = "",
            teachers = "",
            classrooms = "",
            weeks = listOf(true),
            isAdvanced = false,
        ),
        LessonEditorContentPreviewData(
            isProgress = false,
            isEditing = true,
            subjectName = Lessons.regular.subjectName,
            type = Lessons.regular.type,
            teachers = Lessons.regular.teachers.joinToString(),
            classrooms = Lessons.regular.classrooms.joinToString(),
            weeks = (Lessons.regular.lessonRepeat as Lesson.Repeat.ByWeekday).weeks,
        ),
        LessonEditorContentPreviewData(
            isProgress = false,
            isEditing = true,
            subjectName = Lessons.long.subjectName,
            type = Lessons.long.type,
            teachers = Lessons.long.teachers.joinToString(),
            classrooms = Lessons.long.classrooms.joinToString(),
            weeks = (Lessons.long.lessonRepeat as Lesson.Repeat.ByWeekday).weeks,
        ),
    )
}

@ScreenPreviews
@Composable
private fun LessonEditorContentPreview(
    @PreviewParameter(LessonEditorContentPreviewParameterProvider::class) data: LessonEditorContentPreviewData,
) = AppTheme {
    LessonEditorContent(
        isProgress = data.isProgress,
        isEditing = data.isEditing,
        subjectName = data.subjectName,
        existingSubjects = emptyList(),
        subjectNameErrorMessage = data.subjectNameErrorMessage,
        type = data.type,
        existingTypes = emptyList(),
        teachers = data.teachers,
        existingTeachers = emptyList(),
        classrooms = data.classrooms,
        existingClassrooms = emptyList(),
        startTime = Lessons.regular.startTime,
        endTime = Lessons.regular.endTime,
        dayOfWeek = DayOfWeek.MONDAY,
        weeks = data.weeks,
        isAdvancedWeeksSelectorEnabled = data.isAdvanced,
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
