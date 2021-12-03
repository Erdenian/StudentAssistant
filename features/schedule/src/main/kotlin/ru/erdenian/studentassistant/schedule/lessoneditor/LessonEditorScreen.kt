package ru.erdenian.studentassistant.schedule.lessoneditor

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlinx.coroutines.launch
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.sampledata.Lessons
import ru.erdenian.studentassistant.schedule.R
import ru.erdenian.studentassistant.schedule.lessoneditor.LessonEditorViewModel.Error
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.AutoCompleteTextField
import ru.erdenian.studentassistant.uikit.view.MultiAutoCompleteTextField
import ru.erdenian.studentassistant.uikit.view.TopAppBarActions
import ru.erdenian.studentassistant.utils.showTimePicker
import ru.erdenian.studentassistant.utils.toast

@Composable
fun LessonEditorScreen(
    viewModel: LessonEditorViewModel,
    navigateBack: () -> Unit
) {
    val done by viewModel.done.collectAsState()
    DisposableEffect(done) {
        if (done) navigateBack()
        onDispose {}
    }

    var isSubjectNameChanged by rememberSaveable { mutableStateOf(false) }

    val isEditing = viewModel.isEditing

    val error by viewModel.error.collectAsState()
    val errorMessage = when (error) {
        Error.EMPTY_SUBJECT_NAME -> R.string.le_error_empty_subject_name
        Error.WRONG_TIMES -> R.string.le_error_wrong_time
        Error.EMPTY_REPEAT -> R.string.le_error_empty_repeat
        null -> null
    }?.let { stringResource(it) }

    val subjectName by viewModel.subjectName.collectAsState()
    val existingSubjects by viewModel.existingSubjects.collectAsState()
    val subjectNameErrorMessage = errorMessage?.takeIf { (error == Error.EMPTY_SUBJECT_NAME) && isSubjectNameChanged }

    val type by viewModel.type.collectAsState()
    val predefinedTypes = stringArrayResource(R.array.lesson_types).toList()
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

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LessonEditorContent(
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
        onBackClick = navigateBack,
        onSaveClick = {
            isSubjectNameChanged = true
            if (errorMessage != null) {
                context.toast(errorMessage)
            } else {
                coroutineScope.launch {
                    if (viewModel.isSubjectNameChangedAndNotLast()) {
                        MaterialAlertDialogBuilder(context)
                            .setTitle(R.string.le_rename_others_title)
                            .setMessage(R.string.le_rename_others_message)
                            .setPositiveButton(R.string.le_rename_others_yes) { _, _ -> viewModel.save(true) }
                            .setNegativeButton(R.string.le_rename_others_no) { _, _ -> viewModel.save(false) }
                            .setNeutralButton(R.string.le_rename_others_cancel, null)
                            .show()
                    } else viewModel.save()
                }
            }
        },
        onDeleteClick = {
            coroutineScope.launch {
                if (viewModel.isLastLessonOfSubjectsAndHasHomeworks()) {
                    MaterialAlertDialogBuilder(context)
                        .setTitle(R.string.le_delete_homeworks_title)
                        .setMessage(R.string.le_delete_homeworks_message)
                        .setPositiveButton(R.string.le_delete_homeworks_yes) { _, _ -> viewModel.delete(true) }
                        .setNegativeButton(R.string.le_delete_homeworks_no) { _, _ -> viewModel.delete(false) }
                        .setNeutralButton(R.string.le_delete_homeworks_cancel, null)
                        .show()
                } else {
                    MaterialAlertDialogBuilder(context)
                        .setMessage(R.string.le_delete_message)
                        .setPositiveButton(R.string.le_delete_yes) { _, _ -> viewModel.delete() }
                        .setNegativeButton(R.string.le_delete_no, null)
                        .show()
                }
            }
        },
        onSubjectNameChange = { value ->
            isSubjectNameChanged = true
            viewModel.subjectName.value = value
        },
        onTypeChange = { viewModel.type.value = it },
        onTeachersChange = { viewModel.teachers.value = it },
        onClassroomsChange = { viewModel.classrooms.value = it },
        onStartTimeChange = { viewModel.startTime.value = it },
        onEndTimeChange = { viewModel.endTime.value = it },
        onDayOfWeekChange = { viewModel.dayOfWeek.value = it },
        onWeeksChange = { viewModel.weeks.value = it }
    )
}

@Composable
private fun LessonEditorContent(
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
    onWeeksChange: (List<Boolean>) -> Unit
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(text = stringResource(if (isEditing) R.string.le_title_edit else R.string.le_title_new)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = AppIcons.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                TopAppBarActions(
                    actions = listOfNotNull(
                        ActionItem.AlwaysShow(
                            name = stringResource(R.string.le_save),
                            imageVector = AppIcons.Check,
                            onClick = onSaveClick
                        ),
                        if (isEditing) {
                            ActionItem.NeverShow(
                                name = stringResource(R.string.le_delete),
                                onClick = onDeleteClick
                            )
                        } else null
                    )
                )
            }
        )
    }
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = MaterialTheme.dimensions.activityHorizontalMargin,
                vertical = MaterialTheme.dimensions.activityVerticalMargin
            )
    ) {
        val timeFormatter = remember { DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT) }

        AutoCompleteTextField(
            value = subjectName,
            items = existingSubjects,
            onValueChange = onSubjectNameChange,
            label = stringResource(R.string.le_subject_name),
            error = subjectNameErrorMessage ?: "",
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
        )

        AutoCompleteTextField(
            value = type,
            items = existingTypes,
            onValueChange = onTypeChange,
            label = stringResource(R.string.le_type),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        MultiAutoCompleteTextField(
            value = teachers,
            items = existingTeachers,
            onValueChange = onTeachersChange,
            label = stringResource(R.string.le_teachers),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        MultiAutoCompleteTextField(
            value = classrooms,
            items = existingClassrooms,
            onValueChange = onClassroomsChange,
            label = stringResource(R.string.le_classrooms),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            val context = LocalContext.current

            Text(
                text = stringResource(R.string.le_start_time),
                style = MaterialTheme.typography.body2,
                modifier = Modifier.weight(1.0f)
            )
            TextButton(
                onClick = { context.showTimePicker(preselectedTime = startTime, onTimeSet = onStartTimeChange) }
            ) {
                Text(text = startTime.format(timeFormatter))
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            val context = LocalContext.current

            Text(
                text = stringResource(R.string.le_end_time),
                style = MaterialTheme.typography.body2,
                modifier = Modifier.weight(1.0f)
            )
            TextButton(
                onClick = { context.showTimePicker(preselectedTime = endTime, onTimeSet = onEndTimeChange) }
            ) {
                Text(text = endTime.format(timeFormatter))
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        WeekdayPicker(
            value = dayOfWeek,
            onValueChange = onDayOfWeekChange,
            modifier = Modifier.fillMaxWidth()
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        WeeksSelector(
            weeks = weeks,
            onWeeksChange = onWeeksChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LessonEditorContentPreview() = AppTheme {
    LessonEditorContent(
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
        onWeeksChange = {}
    )
}
