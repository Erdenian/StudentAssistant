package ru.erdenian.studentassistant.settings.ui

import android.app.Application
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Duration
import java.time.LocalTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.api.SettingsRepository
import ru.erdenian.studentassistant.settings.MainDispatcherRule

internal class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>()
    private val settingsRepository = mockk<SettingsRepository>(relaxed = true)
    private val repositoryApi = mockk<RepositoryApi> {
        every { settingsRepository } returns this@SettingsViewModelTest.settingsRepository
    }

    private val defaultStartTimeFlow = MutableStateFlow(LocalTime.of(9, 0))
    private val defaultLessonDurationFlow = MutableStateFlow(Duration.ofMinutes(90))
    private val defaultBreakDurationFlow = MutableStateFlow(Duration.ofMinutes(10))
    private val isAdvancedWeeksSelectorEnabledFlow = MutableStateFlow(false)

    init {
        every { settingsRepository.getDefaultStartTimeFlow(any()) } returns defaultStartTimeFlow
        every { settingsRepository.getDefaultLessonDurationFlow(any()) } returns defaultLessonDurationFlow
        every { settingsRepository.getDefaultBreakDurationFlow(any()) } returns defaultBreakDurationFlow
        every { settingsRepository.getAdvancedWeeksSelectorFlow(any()) } returns isAdvancedWeeksSelectorEnabledFlow
    }

    private val viewModel by lazy { SettingsViewModel(application, repositoryApi) }

    @Test
    fun `defaultStartTime flow and setter test`() = runTest {
        val expected = LocalTime.of(10, 0)

        assertEquals(defaultStartTimeFlow.value, viewModel.defaultStartTimeFlow.value)

        viewModel.setDefaultStartTime(expected)
        verify { settingsRepository.defaultStartTime = expected }
    }

    @Test
    fun `defaultLessonDuration flow and setter test`() = runTest {
        val expected = Duration.ofMinutes(45)

        assertEquals(defaultLessonDurationFlow.value, viewModel.defaultLessonDurationFlow.value)

        viewModel.setDefaultLessonDuration(expected)
        verify { settingsRepository.defaultLessonDuration = expected }
    }

    @Test
    fun `defaultBreakDuration flow and setter test`() = runTest {
        val expected = Duration.ofMinutes(20)

        assertEquals(defaultBreakDurationFlow.value, viewModel.defaultBreakDurationFlow.value)

        viewModel.setDefaultBreakDuration(expected)
        verify { settingsRepository.defaultBreakDuration = expected }
    }

    @Test
    fun `isAdvancedWeeksSelectorEnabled flow and setter test`() = runTest {
        val expected = true

        assertEquals(isAdvancedWeeksSelectorEnabledFlow.value, viewModel.isAdvancedWeeksSelectorEnabledFlow.value)

        viewModel.setAdvancedWeeksSelectorEnabled(expected)
        verify { settingsRepository.isAdvancedWeeksSelectorEnabled = expected }
    }
}
