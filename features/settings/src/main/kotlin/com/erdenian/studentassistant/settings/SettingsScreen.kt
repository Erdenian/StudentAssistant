package com.erdenian.studentassistant.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
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
        isAdvancedWeeksSelectorEnabledChange = viewModel::setAdvancedWeeksSelectorEnabled,
    )
}
