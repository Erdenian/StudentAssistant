package com.erdenian.studentassistant.repository

import android.content.SharedPreferences
import java.time.Duration
import java.time.LocalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

class SettingsRepository(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val DEFAULT_START_TIME_KEY = "default_start_time"
        private val DEFAULT_START_TIME = LocalTime.of(9, 0)

        private const val DEFAULT_LESSON_DURATION_KEY = "default_lesson_duration"
        private val DEFAULT_LESSON_DURATION = Duration.ofMinutes(90)

        private const val DEFAULT_BREAK_DURATION_KEY = "default_break_duration"
        private val DEFAULT_BREAK_DURATION = Duration.ofMinutes(10)

        private const val IS_ADVANCED_WEEKS_SELECTOR_ENABLED_KEY = "is_advanced_weeks_selector_enabled"
        private const val IS_ADVANCED_WEEKS_SELECTOR_ENABLED = false
    }

    // region Default start time

    var defaultStartTime: LocalTime
        get() = sharedPreferences.getLocalTime(DEFAULT_START_TIME_KEY, DEFAULT_START_TIME)
        set(value) = sharedPreferences.edit { putLocalTime(DEFAULT_START_TIME_KEY, value) }

    fun getDefaultStartTimeFlow(scope: CoroutineScope): StateFlow<LocalTime> =
        sharedPreferences.getLocalTimeFlow(scope, DEFAULT_START_TIME_KEY, DEFAULT_START_TIME)

    // endregion

    // region Default lesson duration

    var defaultLessonDuration: Duration
        get() = sharedPreferences.getDuration(DEFAULT_LESSON_DURATION_KEY, DEFAULT_LESSON_DURATION)
        set(value) = sharedPreferences.edit { putDuration(DEFAULT_LESSON_DURATION_KEY, value) }

    fun getDefaultLessonDurationFlow(scope: CoroutineScope): StateFlow<Duration> =
        sharedPreferences.getDurationFlow(scope, DEFAULT_LESSON_DURATION_KEY, DEFAULT_LESSON_DURATION)

    // endregion

    // region Default break duration

    var defaultBreakDuration: Duration
        get() = sharedPreferences.getDuration(DEFAULT_BREAK_DURATION_KEY, DEFAULT_BREAK_DURATION)
        set(value) = sharedPreferences.edit { putDuration(DEFAULT_BREAK_DURATION_KEY, value) }

    fun getDefaultBreakDurationFlow(scope: CoroutineScope): StateFlow<Duration> =
        sharedPreferences.getDurationFlow(scope, DEFAULT_BREAK_DURATION_KEY, DEFAULT_BREAK_DURATION)

    // endregion

    // region Advanced weeks selector

    var isAdvancedWeeksSelectorEnabled: Boolean
        get() = sharedPreferences.getBoolean(IS_ADVANCED_WEEKS_SELECTOR_ENABLED_KEY, IS_ADVANCED_WEEKS_SELECTOR_ENABLED)
        set(value) = sharedPreferences.edit { putBoolean(IS_ADVANCED_WEEKS_SELECTOR_ENABLED_KEY, value) }

    fun getAdvancedWeeksSelectorFlow(scope: CoroutineScope): StateFlow<Boolean> =
        sharedPreferences.getBooleanFlow(scope, IS_ADVANCED_WEEKS_SELECTOR_ENABLED_KEY, IS_ADVANCED_WEEKS_SELECTOR_ENABLED)

    // endregion

    private fun SharedPreferences.edit(block: SharedPreferences.Editor.() -> Unit) = edit().apply(block).apply()
}
