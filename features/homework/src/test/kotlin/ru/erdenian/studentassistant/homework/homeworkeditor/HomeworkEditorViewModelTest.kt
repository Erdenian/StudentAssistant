package ru.erdenian.studentassistant.homeworks.homeworkeditor

import android.app.Application
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import java.time.LocalDate
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
import ru.erdenian.studentassistant.homeworks.MainDispatcherRule
import ru.erdenian.studentassistant.repository.api.HomeworkRepository
import ru.erdenian.studentassistant.repository.api.LessonRepository
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.api.SemesterRepository
import ru.erdenian.studentassistant.repository.api.entity.Homework
import ru.erdenian.studentassistant.repository.api.entity.Semester

@OptIn(ExperimentalCoroutinesApi::class)
internal class HomeworkEditorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>()
    private val semesterRepository = mockk<SemesterRepository>()
    private val lessonRepository = mockk<LessonRepository>()
    private val homeworkRepository = mockk<HomeworkRepository>(relaxed = true)
    private val analytics = mockk<Analytics>(relaxed = true)
    private val repositoryApi = mockk<RepositoryApi> {
        every { semesterRepository } returns this@HomeworkEditorViewModelTest.semesterRepository
        every { lessonRepository } returns this@HomeworkEditorViewModelTest.lessonRepository
        every { homeworkRepository } returns this@HomeworkEditorViewModelTest.homeworkRepository
    }
    private val analyticsApi = mockk<AnalyticsApi> {
        every { analytics } returns this@HomeworkEditorViewModelTest.analytics
    }

    private val semesterId = 1L
    private val today = LocalDate.of(2023, 2, 14)
    private val semesterFlow = MutableStateFlow(
        Semester(name = "Semester", firstDay = today.minusMonths(1), lastDay = today.plusMonths(1), id = semesterId),
    )
    private val subjectsFlow = MutableStateFlow(listOf("Subject1", "Subject2"))

    init {
        every { semesterRepository.getFlow(semesterId) } returns semesterFlow
        every { lessonRepository.getSubjects(semesterId) } returns subjectsFlow
    }

    @Before
    fun setUp() {
        mockkStatic(LocalDate::class)
        every { LocalDate.now() } returns today
    }

    @After
    fun tearDown() {
        unmockkStatic(LocalDate::class)
    }

    @Test
    fun `init new homework test`() = runTest {
        val viewModel = HomeworkEditorViewModel(
            application = application,
            repositoryApi = repositoryApi,
            analyticsApi = analyticsApi,
            semesterId = semesterId,
            homeworkId = null,
            subjectName = null,
        )
        // Сбор потоков необходим, чтобы во ViewModel сработали onEach, устанавливающие флаги загрузки
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.existingSubjects.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.semesterDatesRange.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.operation.collect() }
        advanceUntilIdle()

        assertEquals("", viewModel.subjectName.value)
        assertEquals("", viewModel.description.value)
        // Проверяем дефолтный дедлайн (сегодня + 1 неделя)
        assertEquals(today.plusWeeks(1), viewModel.deadline.value)
        assertEquals(subjectsFlow.value, viewModel.existingSubjects.value)
        assertNull(viewModel.operation.value)
    }

    @Test
    fun `init existing homework test`() = runTest {
        val homework = Homework(
            subjectName = "Subject",
            description = "Description",
            deadline = today,
            isDone = false,
            semesterId = semesterId,
            id = 10L,
        )
        coEvery { homeworkRepository.get(homework.id) } returns homework

        val viewModel = HomeworkEditorViewModel(
            application = application,
            repositoryApi = repositoryApi,
            analyticsApi = analyticsApi,
            semesterId = semesterId,
            homeworkId = homework.id,
            subjectName = null,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.existingSubjects.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.semesterDatesRange.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.operation.collect() }
        advanceUntilIdle()

        assertEquals(homework.subjectName, viewModel.subjectName.value)
        assertEquals(homework.description, viewModel.description.value)
        assertEquals(homework.deadline, viewModel.deadline.value)
        assertNull(viewModel.operation.value)
    }

    @Test
    fun `logUnknownSubjectAction test`() {
        val viewModel = HomeworkEditorViewModel(
            application = application,
            repositoryApi = repositoryApi,
            analyticsApi = analyticsApi,
            semesterId = semesterId,
            homeworkId = null,
            subjectName = null,
        )

        viewModel.subjectName.value = "Subject name"
        viewModel.logUnknownSubjectAction(true)
        verify {
            analytics.logEvent(
                name = "homework_unknown_subject_decision",
                params = mapOf(
                    "action" to "save_and_create",
                    "subject_name" to "Subject name",
                ),
            )
        }

        viewModel.logUnknownSubjectAction(false)
        verify {
            analytics.logEvent(
                name = "homework_unknown_subject_decision",
                params = mapOf(
                    "action" to "save",
                    "subject_name" to "Subject name",
                ),
            )
        }
    }

    @Test
    fun `save new homework test`() = runTest {
        val viewModel = HomeworkEditorViewModel(
            application = application,
            repositoryApi = repositoryApi,
            analyticsApi = analyticsApi,
            semesterId = semesterId,
            homeworkId = null,
            subjectName = null,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.existingSubjects.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.semesterDatesRange.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.operation.collect() }
        advanceUntilIdle()

        viewModel.subjectName.value = "Subject"
        viewModel.description.value = "Description"
        viewModel.deadline.value = today

        viewModel.save()
        advanceUntilIdle()

        coVerify {
            homeworkRepository.insert(
                subjectName = "Subject",
                description = "Description",
                deadline = any(),
                semesterId = semesterId,
            )
        }
        coVerify { analytics.logEvent("homework_created", mapOf("subject_name" to "Subject")) }
        assertTrue(viewModel.done.value)
    }

    @Test
    fun `save existing homework test`() = runTest {
        val homework = Homework(
            subjectName = "Subject",
            description = "Description",
            deadline = today,
            isDone = false,
            semesterId = semesterId,
            id = 10L,
        )
        coEvery { homeworkRepository.get(homework.id) } returns homework

        val viewModel = HomeworkEditorViewModel(
            application = application,
            repositoryApi = repositoryApi,
            analyticsApi = analyticsApi,
            semesterId = semesterId,
            homeworkId = homework.id,
            subjectName = null,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.existingSubjects.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.semesterDatesRange.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.operation.collect() }
        advanceUntilIdle()

        viewModel.subjectName.value = "New Subject"
        viewModel.save()
        advanceUntilIdle()

        coVerify {
            homeworkRepository.update(
                id = homework.id,
                subjectName = "New Subject",
                description = homework.description,
                deadline = homework.deadline,
                semesterId = semesterId,
            )
        }
        coVerify { analytics.logEvent("homework_edited", mapOf("subject_name" to "New Subject")) }
        assertTrue(viewModel.done.value)
    }

    @Test
    fun `delete homework test`() = runTest {
        val homeworkId = 10L
        val homework = Homework(
            subjectName = "Subject",
            description = "Description",
            deadline = today,
            isDone = false,
            semesterId = semesterId,
            id = homeworkId,
        )
        coEvery { homeworkRepository.get(homeworkId) } returns homework

        val viewModel = HomeworkEditorViewModel(
            application = application,
            repositoryApi = repositoryApi,
            analyticsApi = analyticsApi,
            semesterId = semesterId,
            homeworkId = homeworkId,
            subjectName = null,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.existingSubjects.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.semesterDatesRange.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.operation.collect() }
        advanceUntilIdle()

        viewModel.delete()
        advanceUntilIdle()

        coVerify { homeworkRepository.delete(homeworkId) }
        coVerify { analytics.logEvent("homework_deleted", mapOf("subject_name" to "Subject")) }
        assertTrue(viewModel.done.value)
    }

    @Test
    fun `error test`() = runTest {
        val viewModel = HomeworkEditorViewModel(
            application = application,
            repositoryApi = repositoryApi,
            analyticsApi = analyticsApi,
            semesterId = semesterId,
            homeworkId = null,
            subjectName = null,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.existingSubjects.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.semesterDatesRange.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.error.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.operation.collect() }
        advanceUntilIdle()

        assertEquals(HomeworkEditorViewModel.Error.EMPTY_SUBJECT, viewModel.error.value)

        viewModel.subjectName.value = "Subject"
        advanceUntilIdle()
        assertEquals(HomeworkEditorViewModel.Error.EMPTY_DESCRIPTION, viewModel.error.value)

        viewModel.description.value = "Description"
        advanceUntilIdle()
        assertNull(viewModel.error.value)
    }

    @Test
    fun `lessonExists test`() = runTest {
        val viewModel = HomeworkEditorViewModel(
            application = application,
            repositoryApi = repositoryApi,
            analyticsApi = analyticsApi,
            semesterId = semesterId,
            homeworkId = null,
            subjectName = null,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.existingSubjects.collect() }
        advanceUntilIdle()

        // subjectsFlow имеет значения "Subject1", "Subject2"

        viewModel.subjectName.value = "Subject1"
        assertTrue(viewModel.lessonExists)

        viewModel.subjectName.value = "Subject3"
        assertFalse(viewModel.lessonExists)
    }
}
