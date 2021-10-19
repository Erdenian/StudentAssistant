package ru.erdenian.studentassistant.ui.main.lessoneditor

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.map
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.ui.main.lessoneditor.LessonEditorViewModel.Error
import ru.erdenian.studentassistant.uikit.style.AppIcons
import ru.erdenian.studentassistant.uikit.style.AppTheme
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.AutoCompleteTextField
import ru.erdenian.studentassistant.uikit.view.MultiAutoCompleteTextField
import ru.erdenian.studentassistant.uikit.view.TopAppBarActions
import ru.erdenian.studentassistant.uikit.view.WeeksSelector
import ru.erdenian.studentassistant.utils.Lessons
import ru.erdenian.studentassistant.utils.observeAsStateNonNull
import ru.erdenian.studentassistant.utils.showTimePicker
import ru.erdenian.studentassistant.utils.toast

@Composable
fun LessonEditorScreen(
    viewModel: LessonEditorViewModel,
    navigateBack: () -> Unit
) {
    val done by viewModel.done.observeAsStateNonNull()
    DisposableEffect(done) {
        if (done) navigateBack()
        onDispose {}
    }

    var isSubjectNameChanged by rememberSaveable { mutableStateOf(false) }

    val isEditing = viewModel.isEditing

    val errorMessageResource by viewModel.error.map { error ->
        when (error) {
            Error.EMPTY_SUBJECT_NAME -> R.string.lef_error_empty_subject_name
            Error.WRONG_TIMES -> R.string.lef_error_wrong_time
            Error.EMPTY_REPEAT -> R.string.lef_error_empty_repeat
            null -> null
        }
    }.observeAsState()
    val errorMessage = errorMessageResource?.let { stringResource(it) }
    val error by viewModel.error.observeAsState()

    val subjectName by viewModel.subjectName.observeAsStateNonNull()
    val existingSubjects by viewModel.existingSubjects.map { it.list }.observeAsState(emptyList())
    val subjectNameErrorMessage = errorMessage?.takeIf { (error == Error.EMPTY_SUBJECT_NAME) && isSubjectNameChanged }

    val type by viewModel.type.observeAsStateNonNull()
    val predefinedTypes = stringArrayResource(R.array.lesson_types).toList()
    val existingTypes by viewModel.existingTypes
        .map { (predefinedTypes + it.list).distinct() }
        .observeAsState(emptyList())

    val teachers by viewModel.teachers.observeAsStateNonNull()
    val existingTeachers by viewModel.existingTeachers.map { it.list }.observeAsState(emptyList())

    val classrooms by viewModel.classrooms.observeAsStateNonNull()
    val existingClassrooms by viewModel.existingClassrooms.map { it.list }.observeAsState(emptyList())

    val startTime by viewModel.startTime.observeAsState(LocalTime.now())
    val endTime by viewModel.endTime.observeAsState(LocalTime.now())

    val weekday by viewModel.weekday.observeAsStateNonNull()
    val weeks by viewModel.weeks.observeAsStateNonNull()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LessonEditorContent(
        isEditing = isEditing,
        subjectName = subjectName,
        existingSubjects = existingSubjects,
        subjectNameErrorMessage = subjectNameErrorMessage,
        type = type,
        existingTypes = existingTypes,
        teachers = teachers,
        existingTeachers = existingTeachers,
        classrooms = classrooms,
        existingClassrooms = existingClassrooms,
        startTime = startTime,
        endTime = endTime,
        weekday = weekday,
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
                            .setTitle(R.string.lef_rename_others_title)
                            .setMessage(R.string.lef_rename_others_message)
                            .setPositiveButton(R.string.lef_rename_others_yes) { _, _ -> viewModel.save(true) }
                            .setNegativeButton(R.string.lef_rename_others_no) { _, _ -> viewModel.save(false) }
                            .setNeutralButton(R.string.lef_rename_others_cancel, null)
                            .show()
                    } else viewModel.save()
                }
            }
        },
        onDeleteClick = {
            coroutineScope.launch {
                if (viewModel.isLastLessonOfSubjectsAndHasHomeworks()) {
                    MaterialAlertDialogBuilder(context)
                        .setTitle(R.string.lef_delete_homeworks_title)
                        .setMessage(R.string.lef_delete_homeworks_message)
                        .setPositiveButton(R.string.lef_delete_homeworks_yes) { _, _ -> viewModel.delete(true) }
                        .setNegativeButton(R.string.lef_delete_homeworks_no) { _, _ -> viewModel.delete(false) }
                        .setNeutralButton(R.string.lef_delete_homeworks_cancel, null)
                        .show()
                } else {
                    MaterialAlertDialogBuilder(context)
                        .setMessage(R.string.lef_delete_message)
                        .setPositiveButton(R.string.lef_delete_yes) { _, _ -> viewModel.delete() }
                        .setNegativeButton(R.string.lef_delete_no, null)
                        .show()
                }
            }
        },
        onSubjectNameChange = {
            isSubjectNameChanged = true
            viewModel.subjectName.value = it
        },
        onTypeChange = { viewModel.type.value = it },
        onTeachersChange = { viewModel.teachers.value = it },
        onClassroomsChange = { viewModel.classrooms.value = it },
        onStartTimeChange = { viewModel.startTime.value = it },
        onEndTimeChange = { viewModel.endTime.value = it },
        onWeekdayChange = { viewModel.weekday.value = it },
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
    weekday: Int,
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
    onWeekdayChange: (Int) -> Unit,
    onWeeksChange: (List<Boolean>) -> Unit
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(text = stringResource(if (isEditing) R.string.lef_title_edit else R.string.lef_title_new)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = AppIcons.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                TopAppBarActions(
                    actions = listOfNotNull(
                        ActionItem.AlwaysShow(
                            name = stringResource(R.string.lef_save),
                            imageVector = AppIcons.Check,
                            onClick = onSaveClick
                        ),
                        if (isEditing) {
                            ActionItem.NeverShow(
                                name = stringResource(R.string.lef_delete),
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
                horizontal = dimensionResource(R.dimen.activity_horizontal_margin),
                vertical = dimensionResource(R.dimen.activity_vertical_margin)
            )
    ) {
        val timeFormatter = remember { DateTimeFormat.shortTime() }

        AutoCompleteTextField(
            value = subjectName,
            items = existingSubjects,
            onValueChange = onSubjectNameChange,
            label = stringResource(R.string.lef_subject_name),
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
            label = stringResource(R.string.lef_type),
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
            label = stringResource(R.string.lef_teachers),
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
            label = stringResource(R.string.lef_classrooms),
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
                text = stringResource(R.string.lef_start_time),
                style = MaterialTheme.typography.body2,
                modifier = Modifier.weight(1.0f)
            )
            TextButton(
                onClick = { context.showTimePicker(preselectedTime = startTime, onTimeSet = onStartTimeChange) }
            ) {
                Text(text = startTime.toString(timeFormatter))
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
                text = stringResource(R.string.lef_end_time),
                style = MaterialTheme.typography.body2,
                modifier = Modifier.weight(1.0f)
            )
            TextButton(
                onClick = { context.showTimePicker(preselectedTime = endTime, onTimeSet = onEndTimeChange) }
            ) {
                Text(text = endTime.toString(timeFormatter))
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        WeekdaysPicker(
            value = weekday,
            onValueChange = onWeekdayChange,
            sundayFirstDay = false,
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
        weekday = (Lessons.regular.lessonRepeat as Lesson.Repeat.ByWeekday).weekday,
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
        onWeekdayChange = {},
        onWeeksChange = {}
    )
}
