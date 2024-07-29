package com.erdenian.studentassistant.settings.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import com.erdenian.studentassistant.mediator.findComponent
import com.erdenian.studentassistant.settings.SettingsApi
import com.erdenian.studentassistant.settings.di.SettingsComponent

internal class SettingsScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = viewModel { findComponent<SettingsApi, SettingsComponent>().settingsViewModel }

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
}
