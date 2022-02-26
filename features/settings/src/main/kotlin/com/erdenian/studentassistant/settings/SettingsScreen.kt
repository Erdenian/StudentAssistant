package com.erdenian.studentassistant.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.erdenian.studentassistant.settings.preference.BooleanPreference
import com.erdenian.studentassistant.settings.preference.DurationPreference
import com.erdenian.studentassistant.settings.preference.TimePreference
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.style.AppTheme
import java.time.Duration
import java.time.LocalTime

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val defaultStartTime by viewModel.defaultStartTimeFlow.collectAsState()
    val defaultLessonDuration by viewModel.defaultLessonDurationFlow.collectAsState()
    val defaultBreakDuration by viewModel.defaultBreakDurationFlow.collectAsState()
    val isAdvancedWeeksSelectorEnabled by viewModel.isAdvancedWeeksSelectorEnabledFlow.collectAsState()

    SettingsContent(
        defaultStartTime = defaultStartTime,
        onDefaultStartTimeChange = viewModel::setDefaultStartTime,
        defaultLessonDuration = defaultLessonDuration,
        onDefaultLessonDurationChange = viewModel::setDefaultLessonDuration,
        defaultBreakDuration = defaultBreakDuration,
        onDefaultBreakDurationChange = viewModel::setDefaultBreakDuration,
        isAdvancedWeeksSelectorEnabled = isAdvancedWeeksSelectorEnabled,
        isAdvancedWeeksSelectorEnabledChange = viewModel::setAdvancedWeeksSelectorEnabled
    )
}

@Composable
private fun SettingsContent(
    defaultStartTime: LocalTime,
    onDefaultStartTimeChange: (LocalTime) -> Unit,
    defaultLessonDuration: Duration,
    onDefaultLessonDurationChange: (Duration) -> Unit,
    defaultBreakDuration: Duration,
    onDefaultBreakDurationChange: (Duration) -> Unit,
    isAdvancedWeeksSelectorEnabled: Boolean,
    isAdvancedWeeksSelectorEnabledChange: (Boolean) -> Unit
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(text = stringResource(RS.st_title)) }
        )
    }
) {
    Column {
        TimePreference(
            title = stringResource(RS.st_default_start_time),
            value = defaultStartTime,
            onValueChange = onDefaultStartTimeChange
        )
        DurationPreference(
            title = stringResource(RS.st_default_lesson_duration),
            value = defaultLessonDuration,
            onValueChange = onDefaultLessonDurationChange
        )
        DurationPreference(
            title = stringResource(RS.st_default_break_duration),
            value = defaultBreakDuration,
            onValueChange = onDefaultBreakDurationChange
        )
        BooleanPreference(
            title = stringResource(RS.st_is_advanced_weeks_selector_enabled),
            description = stringResource(RS.st_is_advanced_weeks_selector_enabled_description),
            value = isAdvancedWeeksSelectorEnabled,
            onValueChange = isAdvancedWeeksSelectorEnabledChange
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingsPreview() = AppTheme {
    SettingsContent(
        defaultStartTime = LocalTime.now(),
        onDefaultStartTimeChange = {},
        defaultLessonDuration = Duration.ZERO,
        onDefaultLessonDurationChange = {},
        defaultBreakDuration = Duration.ZERO,
        onDefaultBreakDurationChange = {},
        isAdvancedWeeksSelectorEnabled = true,
        isAdvancedWeeksSelectorEnabledChange = {}
    )
}
