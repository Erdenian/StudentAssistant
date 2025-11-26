package ru.erdenian.studentassistant.settings.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import java.time.Duration
import java.time.LocalTime
import javax.inject.Inject
import ru.erdenian.studentassistant.repository.api.RepositoryApi

internal class SettingsViewModel @Inject constructor(
    application: Application,
    repositoryApi: RepositoryApi,
) : AndroidViewModel(application) {

    private val settingsRepository = repositoryApi.settingsRepository

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
