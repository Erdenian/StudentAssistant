package ru.erdenian.studentassistant.settings

import java.time.Duration
import java.time.LocalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.erdenian.studentassistant.repository.api.SettingsRepository

internal class FakeSettingsRepository : SettingsRepository {

    private val _defaultStartTimeFlow = MutableStateFlow(LocalTime.of(9, 0))
    override var defaultStartTime: LocalTime
        get() = _defaultStartTimeFlow.value
        set(value) {
            _defaultStartTimeFlow.value = value
        }

    override fun getDefaultStartTimeFlow(scope: CoroutineScope): StateFlow<LocalTime> =
        _defaultStartTimeFlow.asStateFlow()

    private val _defaultLessonDurationFlow = MutableStateFlow(Duration.ofMinutes(90))
    override var defaultLessonDuration: Duration
        get() = _defaultLessonDurationFlow.value
        set(value) {
            _defaultLessonDurationFlow.value = value
        }

    override fun getDefaultLessonDurationFlow(scope: CoroutineScope): StateFlow<Duration> =
        _defaultLessonDurationFlow.asStateFlow()

    private val _defaultBreakDurationFlow = MutableStateFlow(Duration.ofMinutes(10))
    override var defaultBreakDuration: Duration
        get() = _defaultBreakDurationFlow.value
        set(value) {
            _defaultBreakDurationFlow.value = value
        }

    override fun getDefaultBreakDurationFlow(scope: CoroutineScope): StateFlow<Duration> =
        _defaultBreakDurationFlow.asStateFlow()

    private val _isAdvancedWeeksSelectorEnabledFlow = MutableStateFlow(false)
    override var isAdvancedWeeksSelectorEnabled: Boolean
        get() = _isAdvancedWeeksSelectorEnabledFlow.value
        set(value) {
            _isAdvancedWeeksSelectorEnabledFlow.value = value
        }

    override fun getAdvancedWeeksSelectorFlow(scope: CoroutineScope): StateFlow<Boolean> =
        _isAdvancedWeeksSelectorEnabledFlow.asStateFlow()
}
