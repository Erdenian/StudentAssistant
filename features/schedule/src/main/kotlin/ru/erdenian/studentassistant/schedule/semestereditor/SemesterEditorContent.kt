package ru.erdenian.studentassistant.schedule.semestereditor

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import ru.erdenian.studentassistant.sampledata.Semesters
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.AutoMirrored
import ru.erdenian.studentassistant.style.ScreenPreviews
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.dialog.DatePickerDialog
import ru.erdenian.studentassistant.uikit.placeholder.PlaceholderHighlight
import ru.erdenian.studentassistant.uikit.placeholder.fade
import ru.erdenian.studentassistant.uikit.placeholder.placeholder
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.TopAppBarActions

@Composable
internal fun SemesterEditorContent(
    isLoading: Boolean,
    isEditing: Boolean,
    name: String,
    firstDay: LocalDate,
    lastDay: LocalDate,
    errorMessage: String?,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onFirstDayChange: (LocalDate) -> Unit,
    onLastDayChange: (LocalDate) -> Unit,
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(text = stringResource(if (isEditing) RS.se_title_edit else RS.se_title_new)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = AppIcons.AutoMirrored.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                AnimatedContent(
                    targetState = isLoading,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    contentAlignment = Alignment.Center,
                    label = "SemesterEditorTopAppbar",
                ) { isLoading ->
                    TopAppBarActions(
                        actions = listOf(
                            ActionItem.AlwaysShow(
                                name = stringResource(RS.se_save),
                                imageVector = AppIcons.Check,
                                loading = isLoading,
                                onClick = onSaveClick,
                            ),
                        ),
                    )
                }
            },
        )
    },
) { paddingValues ->
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(
                horizontal = MaterialTheme.dimensions.screenPaddingHorizontal,
                vertical = MaterialTheme.dimensions.screenPaddingVertical,
            ),
    ) {
        val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT) }
        val focusManager = LocalFocusManager.current
        var datePickerData: Pair<LocalDate, (LocalDate) -> Unit>? by remember { mutableStateOf(null) }

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            enabled = !isLoading,
            label = { Text(text = stringResource(RS.se_name)) },
            isError = (errorMessage != null),
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
                    visible = isLoading,
                    highlight = PlaceholderHighlight.fade(),
                ),
        )

        AnimatedVisibility(errorMessage != null) {
            Text(
                text = errorMessage.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp),
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp),
        ) {
            Text(
                text = stringResource(RS.se_first_day),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1.0f),
            )
            TextButton(
                onClick = { datePickerData = firstDay to onFirstDayChange },
                enabled = !isLoading,
                modifier = Modifier.placeholder(
                    visible = isLoading,
                    highlight = PlaceholderHighlight.fade(),
                ),
            ) {
                Text(text = firstDay.format(dateFormatter))
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp),
        ) {
            Text(
                text = stringResource(RS.se_last_day),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1.0f),
            )
            TextButton(
                onClick = { datePickerData = lastDay to onLastDayChange },
                enabled = !isLoading,
                modifier = Modifier.placeholder(
                    visible = isLoading,
                    highlight = PlaceholderHighlight.fade(),
                ),
            ) {
                Text(text = lastDay.format(dateFormatter))
            }
        }

        datePickerData?.let { (initialDate, onConfirm) ->
            DatePickerDialog(
                onConfirm = { newValue ->
                    datePickerData = null
                    onConfirm(newValue)
                },
                onDismiss = { datePickerData = null },
                initialSelectedDate = initialDate,
            )
        }
    }
}

private data class SemesterEditorContentPreviewData(
    val isLoading: Boolean,
    val isEditing: Boolean,
    val name: String,
    val errorMessage: String? = null,
)

private class SemesterEditorContentPreviewParameterProvider :
    PreviewParameterProvider<SemesterEditorContentPreviewData> {
    override val values = sequenceOf(
        SemesterEditorContentPreviewData(isLoading = true, isEditing = false, name = ""),
        SemesterEditorContentPreviewData(isLoading = false, isEditing = false, name = "", errorMessage = "Error"),
        SemesterEditorContentPreviewData(isLoading = false, isEditing = false, name = Semesters.regular.name),
        SemesterEditorContentPreviewData(isLoading = false, isEditing = true, name = Semesters.long.name),
    )
}

@ScreenPreviews
@Composable
private fun SemesterEditorContentPreview(
    @PreviewParameter(SemesterEditorContentPreviewParameterProvider::class)
    data: SemesterEditorContentPreviewData,
) = AppTheme {
    SemesterEditorContent(
        isLoading = data.isLoading,
        isEditing = data.isEditing,
        name = data.name,
        firstDay = Semesters.regular.firstDay,
        lastDay = Semesters.regular.lastDay,
        errorMessage = data.errorMessage,
        onBackClick = {},
        onSaveClick = {},
        onNameChange = {},
        onFirstDayChange = {},
        onLastDayChange = {},
    )
}
