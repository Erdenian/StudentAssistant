package ru.erdenian.studentassistant.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SettingsRepositoryTest {

    private val sharedPreferences = ApplicationProvider
        .getApplicationContext<Context>()
        .getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val settingsRepository = SettingsRepository(sharedPreferences)

    @AfterEach
    fun clear() = sharedPreferences.edit().apply(SharedPreferences.Editor::clear).apply()

    @Test
    fun defaultLessonDurationTest() = runBlocking(Dispatchers.Main) {
        assertEquals(Duration.standardMinutes(90), settingsRepository.defaultLessonDuration)
        val newDuration = Duration.standardMinutes(120)
        settingsRepository.defaultLessonDuration = newDuration
        assertEquals(newDuration, settingsRepository.defaultLessonDuration)
    }

    @Test
    fun defaultBreakDurationTest() = runBlocking(Dispatchers.Main) {
        assertEquals(Duration.standardMinutes(10), settingsRepository.defaultBreakDuration)
        val newDuration = Duration.standardMinutes(20)
        settingsRepository.defaultBreakDuration = newDuration
        assertEquals(newDuration, settingsRepository.defaultBreakDuration)
    }

    @Test
    fun defaultStartTimeTest() = runBlocking(Dispatchers.Main) {
        assertEquals(LocalTime(9, 0), settingsRepository.defaultStartTime)
        val newStartTime = LocalTime(10, 30)
        settingsRepository.defaultStartTime = newStartTime
        assertEquals(newStartTime, settingsRepository.defaultStartTime)
    }
}
