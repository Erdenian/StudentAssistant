package ru.erdenian.studentassistant.ui.main.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import ru.erdenian.studentassistant.repository.SettingsRepository

class SettingsViewModel(application: Application) : AndroidViewModel(application), DIAware {

    override val di by closestDI()
    private val settingsRepository by di.instance<SettingsRepository>()

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
}
