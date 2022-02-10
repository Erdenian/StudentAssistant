package ru.erdenian.studentassistant.schedule.semestereditor

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
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
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import ru.erdenian.studentassistant.sampledata.Semesters
import ru.erdenian.studentassistant.schedule.semestereditor.SemesterEditorViewModel.Error
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.ProgressDialog
import ru.erdenian.studentassistant.uikit.view.TopAppBarActions
import ru.erdenian.studentassistant.utils.showDatePicker
import ru.erdenian.studentassistant.utils.toSingleLine
import ru.erdenian.studentassistant.utils.toast

@Composable
fun SemesterEditorScreen(
    viewModel: SemesterEditorViewModel,
    navigateBack: () -> Unit
) {
    var isNameChanged by rememberSaveable { mutableStateOf(false) }

    val operation by viewModel.operation.collectAsState()

    val error by viewModel.error.collectAsState()
    val errorMessage = when (error) {
        Error.EMPTY_NAME -> RS.se_error_empty_name
        Error.SEMESTER_EXISTS -> RS.se_error_name_not_available
        Error.WRONG_DATES -> RS.se_error_wrong_dates
        null -> null
    }?.let { stringResource(it) }

    val name by viewModel.name.collectAsState()
    val nameErrorMessage = errorMessage?.takeIf {
        (error == Error.EMPTY_NAME) && isNameChanged || (error == Error.SEMESTER_EXISTS)
    }

    val firstDay by viewModel.firstDay.collectAsState()
    val lastDay by viewModel.lastDay.collectAsState()

    val done by viewModel.done.collectAsState()
    DisposableEffect(done) {
        if (done) navigateBack()
        onDispose {}
    }

    val context = LocalContext.current

    SemesterEditorContent(
        operation = operation,
        isEditing = viewModel.isEditing,
        name = name,
        firstDay = firstDay,
        lastDay = lastDay,
        errorMessage = nameErrorMessage,
        onBackClick = navigateBack,
        onSaveClick = {
            isNameChanged = true
            errorMessage?.let { context.toast(it) } ?: viewModel.save()
        },
        onNameChange = { value ->
            isNameChanged = true
            viewModel.name.value = value.toSingleLine()
        },
        onFirstDayChange = { viewModel.firstDay.value = it },
        onLastDayChange = { viewModel.lastDay.value = it }
    )
}

@Composable
private fun SemesterEditorContent(
    operation: SemesterEditorViewModel.Operation?,
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
) {
    val isLoading: Boolean
    val isSaving: Boolean
    when (operation) {
        SemesterEditorViewModel.Operation.LOADING -> {
            isLoading = true
            isSaving = false
        }
        SemesterEditorViewModel.Operation.SAVING -> {
            isLoading = false
            isSaving = true
        }
        null -> {
            isLoading = false
            isSaving = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(if (isEditing) RS.se_title_edit else RS.se_title_new)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = AppIcons.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    AnimatedContent(
                        targetState = isLoading,
                        transitionSpec = { fadeIn() with fadeOut() },
                        contentAlignment = Alignment.Center
                    ) { isLoading ->
                        TopAppBarActions(
                            actions = listOf(
                                ActionItem.AlwaysShow(
                                    name = stringResource(RS.se_save),
                                    imageVector = AppIcons.Check,
                                    loading = isLoading,
                                    onClick = onSaveClick
                                )
                            )
                        )
                    }
                }
            )
        }
    ) {
        if (isSaving) {
            ProgressDialog(stringResource(RS.se_saving))
        }

        Column(
            modifier = Modifier.padding(
                horizontal = MaterialTheme.dimensions.activityHorizontalMargin,
                vertical = MaterialTheme.dimensions.activityVerticalMargin
            )
        ) {
            val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT) }
            val focusManager = LocalFocusManager.current

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                enabled = !isLoading,
                label = { Text(text = stringResource(RS.se_name)) },
                isError = (errorMessage != null),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(
                        visible = isLoading,
                        highlight = PlaceholderHighlight.shimmer(),
                    )
            )

            AnimatedVisibility(errorMessage != null) {
                Text(
                    text = errorMessage.orEmpty(),
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
                    text = stringResource(RS.se_first_day),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.weight(1.0f)
                )
                TextButton(
                    onClick = { context.showDatePicker(preselectedDate = firstDay, onDateSet = onFirstDayChange) },
                    enabled = !isLoading,
                    modifier = Modifier.placeholder(
                        visible = isLoading,
                        highlight = PlaceholderHighlight.shimmer(),
                    )
                ) {
                    Text(text = firstDay.format(dateFormatter))
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                val context = LocalContext.current

                Text(
                    text = stringResource(RS.se_last_day),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.weight(1.0f)
                )
                TextButton(
                    onClick = { context.showDatePicker(preselectedDate = lastDay, onDateSet = onLastDayChange) },
                    enabled = !isLoading,
                    modifier = Modifier.placeholder(
                        visible = isLoading,
                        highlight = PlaceholderHighlight.shimmer(),
                    )
                ) {
                    Text(text = lastDay.format(dateFormatter))
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SemesterEditorPreview() = AppTheme {
    SemesterEditorContent(
        operation = null,
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
        operation = null,
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
