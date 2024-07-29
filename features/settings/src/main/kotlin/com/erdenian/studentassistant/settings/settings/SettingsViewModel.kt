package com.erdenian.studentassistant.settings.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.erdenian.studentassistant.repository.SettingsRepository
import java.time.Duration
import java.time.LocalTime
import javax.inject.Inject

internal class SettingsViewModel @Inject constructor(
    application: Application,
    private val settingsRepository: SettingsRepository
) : AndroidViewModel(application) {

    val defaultStartTimeFlow = settingsRepository.getDefaultStartTimeFlow(viewModelScope)
    fun setDefaultStartTime(time: LocalTime) {
        settingsRepository.defaultStartTime = time
    }

    val defaultLessonDurationFlow = settingsRepository.getDefaultLessonDurationFlow(viewModelScope)
    fun setDefaultLessonDuration(duration: Duration) {
        settingsRepository.defaultLessonDuration = duration
    }

    val defaultBreakDurationFlow = settingsRepository.getDefaultBreakDurationFlow(viewModelScope)
    fun setDefaultBreakDuration(duration: Duration) {
        settingsRepository.defaultBreakDuration = duration
    }

    val isAdvancedWeeksSelectorEnabledFlow = settingsRepository.getAdvancedWeeksSelectorFlow(viewModelScope)
    fun setAdvancedWeeksSelectorEnabled(enabled: Boolean) {
        settingsRepository.isAdvancedWeeksSelectorEnabled = enabled
    }
}
