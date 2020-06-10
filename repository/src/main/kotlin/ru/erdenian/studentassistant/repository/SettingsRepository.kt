package ru.erdenian.studentassistant.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import org.joda.time.LocalTime
import org.joda.time.Period

class SettingsRepository(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val DEFAULT_LESSON_DURATION = "default_lesson_duration"
        private const val DEFAULT_LESSON_DURATION_MILLIS = 5_400_000

        private const val DEFAULT_BREAK_DURATION = "default_break_duration"
        private const val DEFAULT_BREAK_DURATION_MILLIS = 600_000

        private const val DEFAULT_START_TIME = "default_start_time"
        private const val DEFAULT_START_TIME_MILLIS = 32_400_000
    }

    var defaultLessonDuration: Period
        get() = sharedPreferences.getInt(DEFAULT_LESSON_DURATION, DEFAULT_LESSON_DURATION_MILLIS).let(Period::millis)
        set(value) = sharedPreferences.edit { putInt(DEFAULT_LESSON_DURATION, value.toStandardDuration().millis.toInt()) }

    val defaultLessonDurationLiveData: LiveData<Period> = sharedPreferences
        .getIntLiveData(DEFAULT_LESSON_DURATION)
        .map { it ?: DEFAULT_LESSON_DURATION_MILLIS }
        .map(Period::millis)

    var defaultBreakDuration: Period
        get() = sharedPreferences.getInt(DEFAULT_BREAK_DURATION, DEFAULT_BREAK_DURATION_MILLIS).let(Period::millis)
        set(value) = sharedPreferences.edit { putInt(DEFAULT_BREAK_DURATION, value.toStandardDuration().millis.toInt()) }

    val defaultBreakDurationLiveData: LiveData<Period> = sharedPreferences
        .getIntLiveData(DEFAULT_BREAK_DURATION)
        .map { it ?: DEFAULT_BREAK_DURATION_MILLIS }
        .map(Period::millis)

    var defaultStartTime: LocalTime
        get() = sharedPreferences.getInt(DEFAULT_START_TIME, DEFAULT_START_TIME_MILLIS).let(LocalTime.MIDNIGHT::plusMillis)
        set(value) = sharedPreferences.edit { putInt(DEFAULT_START_TIME, value.millisOfDay) }

    val defaultStartTimeLiveData: LiveData<LocalTime> = sharedPreferences
        .getIntLiveData(DEFAULT_START_TIME)
        .map { it ?: DEFAULT_START_TIME_MILLIS }
        .map(LocalTime.MIDNIGHT::plusMillis)
}
