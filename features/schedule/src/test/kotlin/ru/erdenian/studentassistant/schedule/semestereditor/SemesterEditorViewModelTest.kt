package ru.erdenian.studentassistant.schedule.semestereditor

import android.app.Application
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate
import java.time.Month
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.api.SemesterRepository
import ru.erdenian.studentassistant.repository.api.entity.Semester
import ru.erdenian.studentassistant.schedule.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
internal class SemesterEditorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>()
    private val semesterRepository = mockk<SemesterRepository>()
    private val repositoryApi = mockk<RepositoryApi> {
        every { semesterRepository } returns this@SemesterEditorViewModelTest.semesterRepository
    }

    private val namesFlow = MutableStateFlow(listOf("Semester 1", "Semester 2"))

    init {
        every { semesterRepository.namesFlow } returns namesFlow
    }

    @Test
    fun `init new semester test`() = runTest {
        val viewModel = SemesterEditorViewModel(application, repositoryApi, null)
        // Подписываемся на error, чтобы запустить загрузку имен
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.error.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.operation.collect() }
        advanceUntilIdle()

        assertEquals("", viewModel.name.value)

        val today = LocalDate.now().withDayOfMonth(1)
        val ranges = listOf(Month.FEBRUARY..Month.MAY, Month.SEPTEMBER..Month.DECEMBER)
        val range = ranges.find { today.month <= it.endInclusive } ?: ranges.first()
        val expectedFirstDay = today.withMonth(range.start.value)
        val expectedLastDay = today.withMonth(range.endInclusive.value).withDayOfMonth(range.endInclusive.maxLength())

        assertEquals(expectedFirstDay, viewModel.firstDay.value)
        assertEquals(expectedLastDay, viewModel.lastDay.value)
        assertNull(viewModel.operation.value)
    }

    @Test
    fun `init existing semester test`() = runTest {
        val semester = Semester("Semester 3", LocalDate.of(2023, 2, 1), LocalDate.of(2023, 5, 31), 10L)
        coEvery { semesterRepository.get(semester.id) } returns semester

        val viewModel = SemesterEditorViewModel(application, repositoryApi, semester.id)
        // Подписываемся на error, чтобы запустить загрузку имен
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.error.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.operation.collect() }
        advanceUntilIdle()

        assertEquals(semester.name, viewModel.name.value)
        assertEquals(semester.firstDay, viewModel.firstDay.value)
        assertEquals(semester.lastDay, viewModel.lastDay.value)
        assertNull(viewModel.operation.value)
    }

    @Test
    fun `save new semester test`() = runTest {
        val viewModel = SemesterEditorViewModel(application, repositoryApi, null)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.error.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.operation.collect() }
        advanceUntilIdle()
        coEvery { semesterRepository.insert(any(), any(), any()) } returns Unit

        viewModel.name.value = "New Semester"
        viewModel.save()
        advanceUntilIdle()

        coVerify {
            semesterRepository.insert(
                name = "New Semester",
                firstDay = any(),
                lastDay = any(),
            )
        }
        assertTrue(viewModel.done.value)
    }

    @Test
    fun `save existing semester test`() = runTest {
        val semester = Semester("Semester 3", LocalDate.of(2023, 2, 1), LocalDate.of(2023, 5, 31), 10L)
        coEvery { semesterRepository.get(semester.id) } returns semester
        coEvery { semesterRepository.update(any(), any(), any(), any()) } returns Unit

        val viewModel = SemesterEditorViewModel(application, repositoryApi, semester.id)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.error.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.operation.collect() }
        advanceUntilIdle()

        viewModel.name.value = "Updated Semester"
        viewModel.save()
        advanceUntilIdle()

        coVerify {
            semesterRepository.update(
                id = semester.id,
                name = "Updated Semester",
                firstDay = semester.firstDay,
                lastDay = semester.lastDay,
            )
        }
        assertTrue(viewModel.done.value)
    }

    @Test
    fun `error test`() = runTest {
        val viewModel = SemesterEditorViewModel(application, repositoryApi, null)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.error.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.operation.collect() }
        advanceUntilIdle()

        assertEquals(SemesterEditorViewModel.Error.EMPTY_NAME, viewModel.error.value)

        viewModel.name.value = "Semester 1" // Уже существует в namesFlow
        advanceUntilIdle()
        assertEquals(SemesterEditorViewModel.Error.SEMESTER_EXISTS, viewModel.error.value)

        viewModel.name.value = "New Semester"
        advanceUntilIdle()
        assertNull(viewModel.error.value)

        viewModel.firstDay.value = viewModel.lastDay.value.plusDays(1)
        advanceUntilIdle()
        assertEquals(SemesterEditorViewModel.Error.WRONG_DATES, viewModel.error.value)
    }
}
