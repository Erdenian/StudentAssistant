package com.erdenian.studentassistant.repository.impl

import android.content.SharedPreferences
import com.erdenian.studentassistant.repository.api.SettingsRepository
import dagger.Reusable
import java.time.Duration
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope

@Reusable
internal class SettingsRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : SettingsRepository {

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

    override var defaultStartTime
        get() = sharedPreferences.getLocalTime(DEFAULT_START_TIME_KEY, DEFAULT_START_TIME)
        set(value) = sharedPreferences.edit { putLocalTime(DEFAULT_START_TIME_KEY, value) }

    override fun getDefaultStartTimeFlow(scope: CoroutineScope) =
        sharedPreferences.getLocalTimeFlow(scope, DEFAULT_START_TIME_KEY, DEFAULT_START_TIME)

    // endregion

    // region Default lesson duration

    override var defaultLessonDuration
        get() = sharedPreferences.getDuration(DEFAULT_LESSON_DURATION_KEY, DEFAULT_LESSON_DURATION)
        set(value) = sharedPreferences.edit { putDuration(DEFAULT_LESSON_DURATION_KEY, value) }

    override fun getDefaultLessonDurationFlow(scope: CoroutineScope) =
        sharedPreferences.getDurationFlow(scope, DEFAULT_LESSON_DURATION_KEY, DEFAULT_LESSON_DURATION)

    // endregion

    // region Default break duration

    override var defaultBreakDuration
        get() = sharedPreferences.getDuration(DEFAULT_BREAK_DURATION_KEY, DEFAULT_BREAK_DURATION)
        set(value) = sharedPreferences.edit { putDuration(DEFAULT_BREAK_DURATION_KEY, value) }

    override fun getDefaultBreakDurationFlow(scope: CoroutineScope) =
        sharedPreferences.getDurationFlow(scope, DEFAULT_BREAK_DURATION_KEY, DEFAULT_BREAK_DURATION)

    // endregion

    // region Advanced weeks selector

    override var isAdvancedWeeksSelectorEnabled
        get() = sharedPreferences.getBoolean(IS_ADVANCED_WEEKS_SELECTOR_ENABLED_KEY, IS_ADVANCED_WEEKS_SELECTOR_ENABLED)
        set(value) = sharedPreferences.edit { putBoolean(IS_ADVANCED_WEEKS_SELECTOR_ENABLED_KEY, value) }

    override fun getAdvancedWeeksSelectorFlow(scope: CoroutineScope) = sharedPreferences.getBooleanFlow(
        scope,
        IS_ADVANCED_WEEKS_SELECTOR_ENABLED_KEY,
        IS_ADVANCED_WEEKS_SELECTOR_ENABLED,
    )

    // endregion

    private fun SharedPreferences.edit(block: SharedPreferences.Editor.() -> Unit) = edit().apply(block).apply()
}
