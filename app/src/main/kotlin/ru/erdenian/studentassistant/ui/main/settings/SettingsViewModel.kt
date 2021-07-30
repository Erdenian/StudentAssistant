package ru.erdenian.studentassistant.ui.main.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import ru.erdenian.studentassistant.repository.SettingsRepository

class SettingsViewModel(application: Application) : AndroidViewModel(application), DIAware {

    override val di by closestDI()
    private val settingsRepository by di.instance<SettingsRepository>()

    val defaultStartTimeLiveData = settingsRepository.defaultStartTimeLiveData
    fun setDefaultStartTime(time: LocalTime) {
        settingsRepository.defaultStartTime = time
    }

    val defaultLessonDurationLiveData = settingsRepository.defaultLessonDurationLiveData
    fun setDefaultLessonDuration(duration: Duration) {
        settingsRepository.defaultLessonDuration = duration
    }

    val defaultBreakDurationLiveData = settingsRepository.defaultBreakDurationLiveData
    fun setDefaultBreakDuration(duration: Duration) {
        settingsRepository.defaultBreakDuration = duration
    }
}
