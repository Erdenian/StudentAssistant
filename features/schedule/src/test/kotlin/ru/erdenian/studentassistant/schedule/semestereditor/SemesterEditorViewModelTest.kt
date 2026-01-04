package ru.erdenian.studentassistant.schedule.semestereditor

import android.app.Application
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.erdenian.studentassistant.analytics.api.Analytics
import ru.erdenian.studentassistant.analytics.api.AnalyticsApi
import ru.erdenian.studentassistant.repository.api.LessonRepository
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
    private val lessonRepository = mockk<LessonRepository>()
    private val analytics = mockk<Analytics>(relaxed = true)
    private val repositoryApi = mockk<RepositoryApi> {
        every { semesterRepository } returns this@SemesterEditorViewModelTest.semesterRepository
        every { lessonRepository } returns this@SemesterEditorViewModelTest.lessonRepository
    }
    private val analyticsApi = mockk<AnalyticsApi> {
        every { analytics } returns this@SemesterEditorViewModelTest.analytics
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
        val viewModel = SemesterEditorViewModel(
            application = application,
            repositoryApi = repositoryApi,
            analyticsApi = analyticsApi,
            semesterId = null,
        )
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
        val semester = Semester(
            name = "Semester 3",
            firstDay = LocalDate.of(2023, 2, 1),
            lastDay = LocalDate.of(2023, 5, 31),
            id = 10L,
        )
        coEvery { semesterRepository.get(semester.id) } returns semester

        val viewModel = SemesterEditorViewModel(
            application = application,
            repositoryApi = repositoryApi,
            analyticsApi = analyticsApi,
            semesterId = semester.id,
        )
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
        val viewModel = SemesterEditorViewModel(
            application = application,
            repositoryApi = repositoryApi,
            analyticsApi = analyticsApi,
            semesterId = null,
        )
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
        verify { analytics.logEvent("semester_created", mapOf("name" to "New Semester")) }
        assertTrue(viewModel.done.value)
    }

    @Test
    fun `save existing semester test`() = runTest {
        val semester = Semester(
            name = "Semester 3",
            firstDay = LocalDate.of(2023, 2, 1),
            lastDay = LocalDate.of(2023, 5, 31),
            id = 10L,
        )
        coEvery { semesterRepository.get(semester.id) } returns semester
        coEvery { semesterRepository.update(id = any(), name = any(), firstDay = any(), lastDay = any()) } returns Unit
        coEvery { lessonRepository.hasNonRecurringLessons(semester.id) } returns false

        val viewModel = SemesterEditorViewModel(
            application = application,
            repositoryApi = repositoryApi,
            analyticsApi = analyticsApi,
            semesterId = semester.id,
        )
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
        verify { analytics.logEvent("semester_edited", mapOf("name" to "Updated Semester")) }
        assertTrue(viewModel.done.value)
    }

    @Test
    fun `save triggers week shift dialog`() = runTest {
        val start = LocalDate.of(2023, 9, 4) // Понедельник
        val semester = Semester(name = "S1", firstDay = start, lastDay = start.plusMonths(4), id = 10L)
        coEvery { semesterRepository.get(semester.id) } returns semester
        coEvery { lessonRepository.hasNonRecurringLessons(semester.id) } returns true

        val viewModel = SemesterEditorViewModel(
            application = application,
            repositoryApi = repositoryApi,
            analyticsApi = analyticsApi,
            semesterId = semester.id,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.error.collect() }
        advanceUntilIdle()

        // Сдвигаем на 1 неделю вперед (11 сентября - понедельник). Четность не меняется в смысле
        // "понедельник-понедельник", но наша логика проверяет именно изменение даты понедельника первой недели.
        // 4 сентября -> понедельник
        // 11 сентября -> понедельник.
        // Monday(4.09) = 4.09. Monday(11.09) = 11.09. Они не равны -> Диалог должен быть.
        viewModel.firstDay.value = start.plusWeeks(1)
        viewModel.save()
        advanceUntilIdle()

        assertTrue(viewModel.showWeekShiftDialog.value)
        assertFalse(viewModel.done.value)

        // Подтверждаем
        coEvery { semesterRepository.update(id = any(), name = any(), firstDay = any(), lastDay = any()) } returns Unit
        viewModel.save(confirmWeekShift = true)
        advanceUntilIdle()

        assertTrue(viewModel.done.value)
    }

    @Test
    fun `save does not trigger week shift dialog if start week monday is same`() = runTest {
        val start = LocalDate.of(2023, 9, 4) // Понедельник
        val semester = Semester(name = "S1", firstDay = start, lastDay = start.plusMonths(4), id = 10L)
        coEvery { semesterRepository.get(semester.id) } returns semester
        coEvery { semesterRepository.update(id = any(), name = any(), firstDay = any(), lastDay = any()) } returns Unit
        coEvery { lessonRepository.hasNonRecurringLessons(semester.id) } returns true

        val viewModel = SemesterEditorViewModel(
            application = application,
            repositoryApi = repositoryApi,
            analyticsApi = analyticsApi,
            semesterId = semester.id,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.error.collect() }
        advanceUntilIdle()

        // Сдвигаем на вторник той же недели
        viewModel.firstDay.value = start.plusDays(1)
        viewModel.save()
        advanceUntilIdle()

        assertFalse(viewModel.showWeekShiftDialog.value)
        assertTrue(viewModel.done.value)
    }

    @Test
    fun `error test`() = runTest {
        val viewModel = SemesterEditorViewModel(
            application = application,
            repositoryApi = repositoryApi,
            analyticsApi = analyticsApi,
            semesterId = null,
        )
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
