package ru.erdenian.studentassistant.schedule.semestereditor

import android.app.Application
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import java.time.LocalDate
import java.time.Month
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
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

    // Фиксированная дата для детерминированности тестов (10 апреля 2023)
    private val fixedDate = LocalDate.of(2023, 4, 10)

    init {
        every { semesterRepository.namesFlow } returns namesFlow
    }

    @Before
    fun setUp() {
        mockkStatic(LocalDate::class)
        every { LocalDate.now() } returns fixedDate
    }

    @After
    fun tearDown() {
        unmockkStatic(LocalDate::class)
    }

    @Test
    fun `init new semester test`() = runTest {
        val viewModel = SemesterEditorViewModel(application, repositoryApi, null)
        // Подписываемся на error, чтобы запустить загрузку имен
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.error.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.operation.collect() }
        advanceUntilIdle()

        assertEquals("", viewModel.name.value)

        // Логика ViewModel: если сегодня 10 апреля, ближайший диапазон FEBRUARY..MAY
        // Start: 1 февраля того же года (2023)
        // End: Конец мая того же года (2023)
        val expectedFirstDay = LocalDate.of(2023, Month.FEBRUARY, 1)
        val expectedLastDay = LocalDate.of(2023, Month.MAY, 31)

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
