package ru.erdenian.studentassistant.repository

import android.content.Context
import androidx.core.content.edit
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalTime
import org.joda.time.Period
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SettingsRepositoryTest {

    private val sharedPreferences = ApplicationProvider
        .getApplicationContext<Context>()
        .getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val settingsRepository = SettingsRepository(sharedPreferences)

    @AfterEach
    fun clear() = sharedPreferences.edit(true) { clear() }

    @Test
    fun defaultLessonDurationTest() = runBlocking(Dispatchers.Main) {
        assertEquals(
            Period.minutes(90).normalizedStandard(),
            settingsRepository.defaultLessonDuration.normalizedStandard()
        )
        val newDuration = Period.hours(2)
        settingsRepository.defaultLessonDuration = newDuration
        assertEquals(newDuration.normalizedStandard(), settingsRepository.defaultLessonDuration.normalizedStandard())
    }

    @Test
    fun defaultBreakDurationTest() = runBlocking(Dispatchers.Main) {
        assertEquals(
            Period.minutes(10).normalizedStandard(),
            settingsRepository.defaultBreakDuration.normalizedStandard()
        )
        val newDuration = Period.hours(1)
        settingsRepository.defaultBreakDuration = newDuration
        assertEquals(newDuration.normalizedStandard(), settingsRepository.defaultBreakDuration.normalizedStandard())
    }

    @Test
    fun defaultStartTimeTest() = runBlocking(Dispatchers.Main) {
        assertEquals(LocalTime(9, 0), settingsRepository.defaultStartTime)
        val newStartTime = LocalTime(10, 30)
        settingsRepository.defaultStartTime = newStartTime
        assertEquals(newStartTime, settingsRepository.defaultStartTime)
    }
}
