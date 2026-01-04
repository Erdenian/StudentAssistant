package ru.erdenian.studentassistant.settings.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import java.time.Duration
import java.time.LocalTime
import javax.inject.Inject
import ru.erdenian.studentassistant.analytics.api.AnalyticsApi
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.settings.di.SettingsComponentHolder

/**
 * ViewModel для экрана настроек.
 *
 * Управляет чтением и записью настроек приложения через
 * [ru.erdenian.studentassistant.repository.api.SettingsRepository].
 */
internal class SettingsViewModel @Inject constructor(
    application: Application,
    repositoryApi: RepositoryApi,
    analyticsApi: AnalyticsApi,
) : AndroidViewModel(application) {

    private val settingsRepository = repositoryApi.settingsRepository
    private val analytics = analyticsApi.analytics

    /**
     * Поток текущего времени начала первого занятия по умолчанию.
     */
    val defaultStartTimeFlow = settingsRepository.getDefaultStartTimeFlow(viewModelScope)

    /**
     * Устанавливает новое время начала первого занятия по умолчанию.
     */
    fun setDefaultStartTime(time: LocalTime) {
        settingsRepository.defaultStartTime = time
        analytics.logEvent(
            name = "default_start_time_changed",
            params = mapOf("time" to time.toString()),
        )
    }

    /**
     * Поток текущей длительности занятия по умолчанию.
     */
    val defaultLessonDurationFlow = settingsRepository.getDefaultLessonDurationFlow(viewModelScope)

    /**
     * Устанавливает новую длительность занятия по умолчанию.
     */
    fun setDefaultLessonDuration(duration: Duration) {
        settingsRepository.defaultLessonDuration = duration
        analytics.logEvent(
            name = "default_lesson_duration_changed",
            params = mapOf("duration_minutes" to duration.toMinutes()),
        )
    }

    /**
     * Поток текущей длительности перемены по умолчанию.
     */
    val defaultBreakDurationFlow = settingsRepository.getDefaultBreakDurationFlow(viewModelScope)

    /**
     * Устанавливает новую длительность перемены по умолчанию.
     */
    fun setDefaultBreakDuration(duration: Duration) {
        settingsRepository.defaultBreakDuration = duration
        analytics.logEvent(
            name = "default_break_duration_changed",
            params = mapOf("duration_minutes" to duration.toMinutes()),
        )
    }

    /**
     * Поток состояния настройки "Расширенный выбор недель".
     */
    val isAdvancedWeeksSelectorEnabledFlow = settingsRepository.getAdvancedWeeksSelectorFlow(viewModelScope)

    /**
     * Включает или выключает расширенный режим выбора недель.
     */
    fun setAdvancedWeeksSelectorEnabled(enabled: Boolean) {
        settingsRepository.isAdvancedWeeksSelectorEnabled = enabled
        analytics.logEvent(
            name = "advanced_weeks_selector_changed",
            params = mapOf("is_enabled" to enabled),
        )
        analytics.setUserProperty(
            name = "is_advanced_weeks_selector_enabled",
            value = enabled.toString(),
        )
    }

    override fun onCleared() {
        super.onCleared()
        // Очищаем граф зависимостей фичи настроек, так как пользователь покинул экран
        SettingsComponentHolder.clear()
    }
}
