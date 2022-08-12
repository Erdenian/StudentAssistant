package com.erdenian.studentassistant.schedule.semestereditor

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erdenian.studentassistant.sampledata.Semesters
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.style.AppIcons
import com.erdenian.studentassistant.style.AppTheme
import com.erdenian.studentassistant.style.dimensions
import com.erdenian.studentassistant.uikit.view.ActionItem
import com.erdenian.studentassistant.uikit.view.TopAppBarActions
import com.erdenian.studentassistant.utils.showDatePicker
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

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
    onLastDayChange: (LocalDate) -> Unit
) = Scaffold(
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
) { paddingValues ->
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(
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
                    highlight = PlaceholderHighlight.shimmer()
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
                    highlight = PlaceholderHighlight.shimmer()
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
                    highlight = PlaceholderHighlight.shimmer()
                )
            ) {
                Text(text = lastDay.format(dateFormatter))
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SemesterEditorLoadingPreview() = AppTheme {
    SemesterEditorContent(
        isLoading = true,
        isEditing = false,
        name = Semesters.regular.name,
        firstDay = Semesters.regular.firstDay,
        lastDay = Semesters.regular.lastDay,
        errorMessage = null,
        onBackClick = {},
        onSaveClick = {},
        onNameChange = {},
        onFirstDayChange = {},
        onLastDayChange = {}
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SemesterEditorPreview() = AppTheme {
    SemesterEditorContent(
        isLoading = false,
        isEditing = false,
        name = Semesters.regular.name,
        firstDay = Semesters.regular.firstDay,
        lastDay = Semesters.regular.lastDay,
        errorMessage = null,
        onBackClick = {},
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
        isLoading = false,
        isEditing = false,
        name = Semesters.long.name,
        firstDay = Semesters.long.firstDay,
        lastDay = Semesters.long.lastDay,
        errorMessage = null,
        onBackClick = {},
        onSaveClick = {},
        onNameChange = {},
        onFirstDayChange = {},
        onLastDayChange = {}
    )
}
