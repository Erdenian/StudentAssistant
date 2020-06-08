package ru.erdenian.studentassistant.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import org.joda.time.Period

class SettingsRepository(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val DEFAULT_LESSON_DURATION = "default_lesson_duration"
        private const val DEFAULT_LESSON_DURATION_MILLIS = 5_400_000

        private const val DEFAULT_BREAK_DURATION = "default_break_duration"
        private const val DEFAULT_BREAK_DURATION_MILLIS = 600_000
    }

    var defaultLessonDuration: Period
        get() = Period.millis(sharedPreferences.getInt(DEFAULT_LESSON_DURATION, DEFAULT_LESSON_DURATION_MILLIS))
        set(value) = sharedPreferences.edit { putInt(DEFAULT_LESSON_DURATION, value.millis) }

    val defaultLessonDurationLiveData: LiveData<Period> = sharedPreferences
        .getIntLiveData(DEFAULT_LESSON_DURATION)
        .map { Period.millis(it ?: DEFAULT_LESSON_DURATION_MILLIS) }

    var defaultBreakDuration: Period
        get() = Period.millis(sharedPreferences.getInt(DEFAULT_BREAK_DURATION, DEFAULT_BREAK_DURATION_MILLIS))
        set(value) = sharedPreferences.edit { putInt(DEFAULT_BREAK_DURATION, value.millis) }

    val defaultBreakDurationLiveData: LiveData<Period> = sharedPreferences
        .getIntLiveData(DEFAULT_BREAK_DURATION)
        .map { Period.millis(it ?: DEFAULT_BREAK_DURATION_MILLIS) }
}
