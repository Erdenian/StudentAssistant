package ru.erdenian.studentassistant.homeworks.homeworkeditor

import android.app.Application
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
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
    private val repositoryApi = mockk<RepositoryApi> {
        every { semesterRepository } returns this@HomeworkEditorViewModelTest.semesterRepository
        every { lessonRepository } returns this@HomeworkEditorViewModelTest.lessonRepository
        every { homeworkRepository } returns this@HomeworkEditorViewModelTest.homeworkRepository
    }

    private val semesterId = 1L
    // Используем фиксированную дату
    private val today = LocalDate.of(2023, 2, 14)
    private val semesterFlow = MutableStateFlow(
        Semester("Semester", today.minusMonths(1), today.plusMonths(1), semesterId),
    )
    private val subjectsFlow = MutableStateFlow(listOf("Subject1", "Subject2"))

    init {
        every { semesterRepository.getFlow(semesterId) } returns semesterFlow
        every { lessonRepository.getSubjects(semesterId) } returns subjectsFlow
    }

    @Test
    fun `init new homework test`() = runTest {
        val viewModel = HomeworkEditorViewModel(application, repositoryApi, semesterId, null, null)
        // Сбор потоков необходим, чтобы во ViewModel сработали onEach, устанавливающие флаги загрузки
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.existingSubjects.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.semesterDatesRange.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.operation.collect() }
        advanceUntilIdle()

        assertEquals("", viewModel.subjectName.value)
        assertEquals("", viewModel.description.value)
        assertEquals(subjectsFlow.value, viewModel.existingSubjects.value)
        assertNull(viewModel.operation.value)
    }

    @Test
    fun `init existing homework test`() = runTest {
        val homework = Homework("Subject", "Description", today, false, semesterId, 10L)
        coEvery { homeworkRepository.get(homework.id) } returns homework

        val viewModel = HomeworkEditorViewModel(application, repositoryApi, semesterId, homework.id, null)
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
    fun `save new homework test`() = runTest {
        val viewModel = HomeworkEditorViewModel(application, repositoryApi, semesterId, null, null)
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
        assertTrue(viewModel.done.value)
    }

    @Test
    fun `save existing homework test`() = runTest {
        val homework = Homework("Subject", "Description", today, false, semesterId, 10L)
        coEvery { homeworkRepository.get(homework.id) } returns homework

        val viewModel = HomeworkEditorViewModel(application, repositoryApi, semesterId, homework.id, null)
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
        assertTrue(viewModel.done.value)
    }

    @Test
    fun `delete homework test`() = runTest {
        val homeworkId = 10L
        val homework = Homework("Subject", "Description", today, false, semesterId, homeworkId)
        coEvery { homeworkRepository.get(homeworkId) } returns homework

        val viewModel = HomeworkEditorViewModel(application, repositoryApi, semesterId, homeworkId, null)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.existingSubjects.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.semesterDatesRange.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.operation.collect() }
        advanceUntilIdle()

        viewModel.delete()
        advanceUntilIdle()

        coVerify { homeworkRepository.delete(homeworkId) }
        assertTrue(viewModel.done.value)
    }

    @Test
    fun `error test`() = runTest {
        val viewModel = HomeworkEditorViewModel(application, repositoryApi, semesterId, null, null)
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
        val viewModel = HomeworkEditorViewModel(application, repositoryApi, semesterId, null, null)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.existingSubjects.collect() }
        advanceUntilIdle()

        // subjectsFlow имеет значения "Subject1", "Subject2"
        
        viewModel.subjectName.value = "Subject1"
        assertTrue(viewModel.lessonExists)

        viewModel.subjectName.value = "Subject3"
        assertFalse(viewModel.lessonExists)
    }
}
