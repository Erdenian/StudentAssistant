package com.erdenian.studentassistant.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erdenian.studentassistant.settings.di.SettingsComponentHolder

@Composable
internal fun SettingsScreen() {
    val viewModel = viewModel { SettingsComponentHolder.instance.settingsViewModel }

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
