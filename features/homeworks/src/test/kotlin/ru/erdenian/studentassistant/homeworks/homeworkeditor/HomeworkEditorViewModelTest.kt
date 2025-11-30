package ru.erdenian.studentassistant.homeworks.homeworkeditor

import android.app.Application
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
    private val semesterFlow = MutableStateFlow(
        Semester("Semester", LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(1), semesterId),
    )
    private val subjectsFlow = MutableStateFlow(listOf("Subject1", "Subject2"))

    init {
        every { semesterRepository.getFlow(semesterId) } returns semesterFlow
        every { lessonRepository.getSubjects(semesterId) } returns subjectsFlow
    }

    @Test
    fun `init new homework test`() = runTest {
        val viewModel = HomeworkEditorViewModel(application, repositoryApi, semesterId, null, null)
        backgroundScope.launch { viewModel.existingSubjects.collect() }
        backgroundScope.launch { viewModel.semesterDatesRange.collect() }
        runCurrent()

        assertEquals("", viewModel.subjectName.value)
        assertEquals("", viewModel.description.value)
        assertEquals(subjectsFlow.value, viewModel.existingSubjects.value)
        assertNull(viewModel.operation.value)
    }

    @Test
    fun `init existing homework test`() = runTest {
        val homework = Homework("Subject", "Description", LocalDate.now(), false, semesterId, 10L)
        coEvery { homeworkRepository.get(homework.id) } returns homework

        val viewModel = HomeworkEditorViewModel(application, repositoryApi, semesterId, homework.id, null)
        backgroundScope.launch { viewModel.existingSubjects.collect() }
        backgroundScope.launch { viewModel.semesterDatesRange.collect() }
        runCurrent()

        assertEquals(homework.subjectName, viewModel.subjectName.value)
        assertEquals(homework.description, viewModel.description.value)
        assertEquals(homework.deadline, viewModel.deadline.value)
        assertNull(viewModel.operation.value)
    }

    @Test
    fun `save new homework test`() = runTest {
        val viewModel = HomeworkEditorViewModel(application, repositoryApi, semesterId, null, null)
        backgroundScope.launch { viewModel.existingSubjects.collect() }
        backgroundScope.launch { viewModel.semesterDatesRange.collect() }

        viewModel.subjectName.value = "Subject"
        viewModel.description.value = "Description"
        viewModel.deadline.value = LocalDate.now()

        viewModel.save()
        runCurrent()

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
        val homework = Homework("Subject", "Description", LocalDate.now(), false, semesterId, 10L)
        coEvery { homeworkRepository.get(homework.id) } returns homework

        val viewModel = HomeworkEditorViewModel(application, repositoryApi, semesterId, homework.id, null)
        backgroundScope.launch { viewModel.existingSubjects.collect() }
        backgroundScope.launch { viewModel.semesterDatesRange.collect() }

        viewModel.subjectName.value = "New Subject"
        viewModel.save()
        runCurrent()

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
        val homework = Homework("Subject", "Description", LocalDate.now(), false, semesterId, homeworkId)
        coEvery { homeworkRepository.get(homeworkId) } returns homework

        val viewModel = HomeworkEditorViewModel(application, repositoryApi, semesterId, homeworkId, null)
        backgroundScope.launch { viewModel.existingSubjects.collect() }
        backgroundScope.launch { viewModel.semesterDatesRange.collect() }

        viewModel.delete()
        runCurrent()

        coVerify { homeworkRepository.delete(homeworkId) }
        assertTrue(viewModel.done.value)
    }

    @Test
    fun `error test`() = runTest {
        val viewModel = HomeworkEditorViewModel(application, repositoryApi, semesterId, null, null)
        backgroundScope.launch { viewModel.existingSubjects.collect() }
        backgroundScope.launch { viewModel.semesterDatesRange.collect() }
        runCurrent()

        assertEquals(HomeworkEditorViewModel.Error.EMPTY_SUBJECT, viewModel.error.first())

        viewModel.subjectName.value = "Subject"
        assertEquals(HomeworkEditorViewModel.Error.EMPTY_DESCRIPTION, viewModel.error.first())

        viewModel.description.value = "Description"
        assertNull(viewModel.error.first())
    }
}
