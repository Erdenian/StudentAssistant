package ru.erdenian.studentassistant.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import org.joda.time.Duration
import org.joda.time.LocalTime

class SettingsRepository(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val DEFAULT_LESSON_DURATION = "default_lesson_duration"
        private const val DEFAULT_LESSON_DURATION_MILLIS = 90L * 60L * 1000L

        private const val DEFAULT_BREAK_DURATION = "default_break_duration"
        private const val DEFAULT_BREAK_DURATION_MILLIS = 10L * 60L * 1000L

        private const val DEFAULT_START_TIME = "default_start_time"
        private const val DEFAULT_START_TIME_MILLIS = 9 * 60 * 60 * 1000
    }

    // region Default lesson duration

    var defaultLessonDuration: Duration
        get() = sharedPreferences.getLong(DEFAULT_LESSON_DURATION, DEFAULT_LESSON_DURATION_MILLIS).let(Duration::millis)
        set(value) = sharedPreferences.edit { putLong(DEFAULT_LESSON_DURATION, value.millis) }

    val defaultLessonDurationLiveData: LiveData<Duration> = sharedPreferences
        .getLongLiveData(DEFAULT_LESSON_DURATION)
        .map { it ?: DEFAULT_LESSON_DURATION_MILLIS }
        .map(Duration::millis)

    // endregion

    // region Default break duration

    var defaultBreakDuration: Duration
        get() = sharedPreferences.getLong(DEFAULT_BREAK_DURATION, DEFAULT_BREAK_DURATION_MILLIS).let(Duration::millis)
        set(value) = sharedPreferences.edit { putLong(DEFAULT_BREAK_DURATION, value.millis) }

    val defaultBreakDurationLiveData: LiveData<Duration> = sharedPreferences
        .getLongLiveData(DEFAULT_BREAK_DURATION)
        .map { it ?: DEFAULT_BREAK_DURATION_MILLIS }
        .map(Duration::millis)

    // region Default start time

    // endregion

    var defaultStartTime: LocalTime
        get() = sharedPreferences.getInt(DEFAULT_START_TIME, DEFAULT_START_TIME_MILLIS).let(LocalTime.MIDNIGHT::plusMillis)
        set(value) = sharedPreferences.edit { putInt(DEFAULT_START_TIME, value.millisOfDay) }

    val defaultStartTimeLiveData: LiveData<LocalTime> = sharedPreferences
        .getIntLiveData(DEFAULT_START_TIME)
        .map { it ?: DEFAULT_START_TIME_MILLIS }
        .map(LocalTime.MIDNIGHT::plusMillis)

    // endregion
}
