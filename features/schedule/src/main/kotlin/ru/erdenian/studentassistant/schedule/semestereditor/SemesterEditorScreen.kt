package ru.erdenian.studentassistant.schedule.semestereditor

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import ru.erdenian.studentassistant.sampledata.Semesters
import ru.erdenian.studentassistant.schedule.R
import ru.erdenian.studentassistant.schedule.semestereditor.SemesterEditorViewModel.Error
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.TopAppBarActions
import ru.erdenian.studentassistant.utils.showDatePicker
import ru.erdenian.studentassistant.utils.toast

@Composable
fun SemesterEditorScreen(
    viewModel: SemesterEditorViewModel,
    navigateBack: () -> Unit
) {
    var isNameChanged by rememberSaveable { mutableStateOf(false) }

    val error by viewModel.error.collectAsState()
    val errorMessage = when (error) {
        Error.EMPTY_NAME -> R.string.se_error_empty_name
        Error.SEMESTER_EXISTS -> R.string.se_error_name_not_available
        Error.WRONG_DATES -> R.string.se_error_wrong_dates
        null -> null
    }?.let { stringResource(it) }

    val name by viewModel.name.collectAsState()
    val nameErrorMessage = errorMessage?.takeIf { (error == Error.EMPTY_NAME) && isNameChanged }

    val firstDay by viewModel.firstDay.collectAsState()
    val lastDay by viewModel.lastDay.collectAsState()

    val done by viewModel.done.collectAsState()
    DisposableEffect(done) {
        if (done) navigateBack()
        onDispose {}
    }

    val context = LocalContext.current

    SemesterEditorContent(
        isEditing = viewModel.isEditing,
        name = name,
        firstDay = firstDay,
        lastDay = lastDay,
        errorMessage = nameErrorMessage,
        onBackClick = navigateBack,
        onSaveClick = {
            isNameChanged = true
            errorMessage?.let { it -> context.toast(it) } ?: viewModel.save()
        },
        onNameChange = { value ->
            isNameChanged = true
            viewModel.name.value = value
        },
        onFirstDayChange = { viewModel.firstDay.value = it },
        onLastDayChange = { viewModel.lastDay.value = it }
    )
}

@Composable
private fun SemesterEditorContent(
    isEditing: Boolean,
    name: String,
    firstDay: LocalDate,
    lastDay: LocalDate,
    errorMessage: String?,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onFirstDayChange: (LocalDate) -> Unit,
    onLastDayChange: (LocalDate) -> Unit
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(text = stringResource(if (isEditing) R.string.se_title_edit else R.string.se_title_new)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = AppIcons.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                TopAppBarActions(
                    actions = listOf(
                        ActionItem.AlwaysShow(
                            name = stringResource(R.string.se_save),
                            imageVector = AppIcons.Check,
                            onClick = onSaveClick
                        )
                    )
                )
            }
        )
    }
) {
    Column(
        modifier = Modifier.padding(
            horizontal = AppTheme.dimensions.activityHorizontalMargin,
            vertical = AppTheme.dimensions.activityVerticalMargin
        )
    ) {
        val dateFormatter = remember { DateTimeFormat.shortDate() }
        val focusManager = LocalFocusManager.current

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(text = stringResource(R.string.se_name)) },
            isError = (errorMessage != null),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            val context = LocalContext.current

            Text(
                text = stringResource(R.string.se_first_day),
                style = MaterialTheme.typography.body2,
                modifier = Modifier.weight(1.0f)
            )
            TextButton(
                onClick = { context.showDatePicker(preselectedDate = firstDay, onDateSet = onFirstDayChange) }
            ) {
                Text(text = firstDay.toString(dateFormatter))
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            val context = LocalContext.current

            Text(
                text = stringResource(R.string.se_last_day),
                style = MaterialTheme.typography.body2,
                modifier = Modifier.weight(1.0f)
            )
            TextButton(
                onClick = { context.showDatePicker(preselectedDate = lastDay, onDateSet = onLastDayChange) }
            ) {
                Text(text = lastDay.toString(dateFormatter))
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SemesterEditorPreview() = AppTheme {
    SemesterEditorContent(
        isEditing = false,
        name = Semesters.regular.name,
        firstDay = Semesters.regular.firstDay,
        lastDay = Semesters.regular.lastDay,
        errorMessage = null,
        onBackClick = { },
        onSaveClick = {},
        onNameChange = {},
        onFirstDayChange = {},
        onLastDayChange = {}
    )
}

@Preview
@Composable
private fun SemesterEditorLongPreview() = AppTheme {
    SemesterEditorContent(
        isEditing = false,
        name = Semesters.long.name,
        firstDay = Semesters.long.firstDay,
        lastDay = Semesters.long.lastDay,
        errorMessage = null,
        onBackClick = { },
        onSaveClick = {},
        onNameChange = {},
        onFirstDayChange = {},
        onLastDayChange = {}
    )
}
