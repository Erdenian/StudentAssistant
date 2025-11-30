package ru.erdenian.studentassistant.schedule.scheduleeditor

import android.app.Application
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.time.LocalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.toList
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
import ru.erdenian.studentassistant.repository.api.HomeworkRepository
import ru.erdenian.studentassistant.repository.api.LessonRepository
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.api.SemesterRepository
import ru.erdenian.studentassistant.repository.api.entity.Lesson
import ru.erdenian.studentassistant.schedule.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
internal class ScheduleEditorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>()
    private val semesterRepository = mockk<SemesterRepository>()
    private val lessonRepository = mockk<LessonRepository>()
    private val homeworkRepository = mockk<HomeworkRepository>()
    private val repositoryApi = mockk<RepositoryApi> {
        every { semesterRepository } returns this@ScheduleEditorViewModelTest.semesterRepository
        every { lessonRepository } returns this@ScheduleEditorViewModelTest.lessonRepository
        every { homeworkRepository } returns this@ScheduleEditorViewModelTest.homeworkRepository
    }

    private val semesterId = 1L

    private val viewModel by lazy {
        ScheduleEditorViewModel(application, repositoryApi, semesterId)
    }

    @Test
    fun `deleteSemester test`() = runTest {
        coEvery { semesterRepository.delete(semesterId) } returns Unit

        assertNull(viewModel.operation.value)
        assertFalse(viewModel.isDeleted.value)

        viewModel.deleteSemester()
        advanceUntilIdle()

        coVerify { semesterRepository.delete(semesterId) }
        assertNull(viewModel.operation.value)
        assertTrue(viewModel.isDeleted.value)
    }

    @Test
    fun `isLastLessonOfSubjectsAndHasHomeworks true test`() = runTest {
        val lesson = Lesson("Subject", "T", emptyList(), emptyList(), LocalTime.MIN, LocalTime.MAX, Lesson.Repeat.ByDates(emptySet()), semesterId, 10L)
        coEvery { lessonRepository.getCount(semesterId, "Subject") } returns 1
        coEvery { homeworkRepository.hasHomeworks(semesterId, "Subject") } returns true

        assertTrue(viewModel.isLastLessonOfSubjectsAndHasHomeworks(lesson))
    }

    @Test
    fun `isLastLessonOfSubjectsAndHasHomeworks false (count) test`() = runTest {
        val lesson = Lesson("Subject", "T", emptyList(), emptyList(), LocalTime.MIN, LocalTime.MAX, Lesson.Repeat.ByDates(emptySet()), semesterId, 10L)
        coEvery { lessonRepository.getCount(semesterId, "Subject") } returns 2
        coEvery { homeworkRepository.hasHomeworks(semesterId, "Subject") } returns true

        assertFalse(viewModel.isLastLessonOfSubjectsAndHasHomeworks(lesson))
    }

    @Test
    fun `isLastLessonOfSubjectsAndHasHomeworks false (homeworks) test`() = runTest {
        val lesson = Lesson("Subject", "T", emptyList(), emptyList(), LocalTime.MIN, LocalTime.MAX, Lesson.Repeat.ByDates(emptySet()), semesterId, 10L)
        coEvery { lessonRepository.getCount(semesterId, "Subject") } returns 1
        coEvery { homeworkRepository.hasHomeworks(semesterId, "Subject") } returns false

        assertFalse(viewModel.isLastLessonOfSubjectsAndHasHomeworks(lesson))
    }

    @Test
    fun `deleteLesson without homeworks test`() = runTest {
        val lesson = Lesson("Subject", "T", emptyList(), emptyList(), LocalTime.MIN, LocalTime.MAX, Lesson.Repeat.ByDates(emptySet()), semesterId, 10L)
        coEvery { lessonRepository.delete(lesson.id) } returns Unit

        viewModel.deleteLesson(lesson, withHomeworks = false)
        advanceUntilIdle()

        coVerify { lessonRepository.delete(lesson.id) }
        coVerify(exactly = 0) { homeworkRepository.delete(any<String>()) }
    }

    @Test
    fun `deleteLesson with homeworks test`() = runTest {
        val lesson = Lesson("Subject", "T", emptyList(), emptyList(), LocalTime.MIN, LocalTime.MAX, Lesson.Repeat.ByDates(emptySet()), semesterId, 10L)
        coEvery { lessonRepository.delete(lesson.id) } returns Unit
        coEvery { homeworkRepository.delete(lesson.subjectName) } returns Unit

        viewModel.deleteLesson(lesson, withHomeworks = true)
        advanceUntilIdle()

        coVerify { lessonRepository.delete(lesson.id) }
        coVerify { homeworkRepository.delete(lesson.subjectName) }
    }

    @Test
    fun `getLessons test`() = runTest {
        val dayOfWeek = java.time.DayOfWeek.MONDAY
        val lesson = Lesson("Subject", "T", emptyList(), emptyList(), LocalTime.MIN, LocalTime.MAX, Lesson.Repeat.ByWeekday(dayOfWeek, listOf(true)), semesterId, 10L)
        every { lessonRepository.getAllFlow(semesterId, dayOfWeek) } returns MutableStateFlow(listOf(lesson))

        val results = mutableListOf<List<Lesson>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.getLessons(dayOfWeek).toList(results)
        }
        advanceUntilIdle()

        assertEquals(listOf(lesson), results.last())

        // Симуляция удаления
        coEvery { lessonRepository.delete(lesson.id) } returns Unit
        viewModel.deleteLesson(lesson)
        advanceUntilIdle()

        // После удаления занятие должно быть отфильтровано локально
        assertEquals(emptyList<Lesson>(), results.last())

        job.cancel()
    }
}
