package ru.erdenian.studentassistant.schedule.scheduleeditor

import android.app.Application
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.DayOfWeek
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
import ru.erdenian.studentassistant.analytics.api.Analytics
import ru.erdenian.studentassistant.analytics.api.AnalyticsApi
import ru.erdenian.studentassistant.repository.api.HomeworkRepository
import ru.erdenian.studentassistant.repository.api.LessonRepository
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.api.SemesterRepository
import ru.erdenian.studentassistant.repository.api.entity.Lesson
import ru.erdenian.studentassistant.schedule.MainDispatcherRule
import ru.erdenian.studentassistant.schedule.scheduleeditor.ScheduleEditorViewModel.Operation

@OptIn(ExperimentalCoroutinesApi::class)
internal class ScheduleEditorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>()
    private val semesterRepository = mockk<SemesterRepository>()
    private val lessonRepository = mockk<LessonRepository>()
    private val homeworkRepository = mockk<HomeworkRepository>()
    private val analytics = mockk<Analytics>(relaxed = true)
    private val repositoryApi = mockk<RepositoryApi> {
        every { semesterRepository } returns this@ScheduleEditorViewModelTest.semesterRepository
        every { lessonRepository } returns this@ScheduleEditorViewModelTest.lessonRepository
        every { homeworkRepository } returns this@ScheduleEditorViewModelTest.homeworkRepository
    }
    private val analyticsApi = mockk<AnalyticsApi> {
        every { analytics } returns this@ScheduleEditorViewModelTest.analytics
    }

    private val semesterId = 1L

    private val viewModel by lazy {
        ScheduleEditorViewModel(
            application = application,
            repositoryApi = repositoryApi,
            analyticsApi = analyticsApi,
            semesterId = semesterId,
        )
    }

    @Test
    fun `deleteSemester test`() = runTest {
        coEvery { semesterRepository.delete(semesterId) } returns Unit

        assertNull(viewModel.operation.value)
        assertFalse(viewModel.isDeleted.value)

        viewModel.deleteSemester()
        advanceUntilIdle()

        coVerify { semesterRepository.delete(semesterId) }
        verify { analytics.logEvent("semester_deleted", any()) }
        assertEquals(Operation.DELETING_SEMESTER, viewModel.operation.value)
        assertTrue(viewModel.isDeleted.value)
    }

    @Test
    fun `logAddLessonClick test`() {
        viewModel.logAddLessonClick()
        verify { analytics.logEvent("lesson_add_clicked", any()) }
    }

    @Test
    fun `logEditSemesterClicked test`() {
        viewModel.logEditSemesterClicked()
        verify { analytics.logEvent("semester_edit_clicked", any()) }
    }

    @Test
    fun `logLessonClick test`() {
        val lesson = Lesson(
            subjectName = "Subject",
            type = "Type",
            teachers = emptyList(),
            classrooms = emptyList(),
            startTime = LocalTime.MIN,
            endTime = LocalTime.MAX,
            lessonRepeat = Lesson.Repeat.ByDates(emptySet()),
            semesterId = semesterId,
            id = 10L,
        )
        viewModel.logLessonClick(lesson)
        verify { analytics.logEvent("lesson_clicked", mapOf("subject_name" to "Subject", "type" to "Type")) }
    }

    @Test
    fun `logCopyLessonClick test`() {
        val lesson = Lesson(
            subjectName = "Subject",
            type = "Type",
            teachers = emptyList(),
            classrooms = emptyList(),
            startTime = LocalTime.MIN,
            endTime = LocalTime.MAX,
            lessonRepeat = Lesson.Repeat.ByDates(emptySet()),
            semesterId = semesterId,
            id = 10L,
        )
        viewModel.logCopyLessonClick(lesson)
        verify { analytics.logEvent("lesson_copy_clicked", mapOf("subject_name" to "Subject", "type" to "Type")) }
    }

    @Test
    fun `isLastLessonOfSubjectAndHasHomeworks true test`() = runTest {
        val lesson = Lesson(
            subjectName = "Subject",
            type = "T",
            teachers = emptyList(),
            classrooms = emptyList(),
            startTime = LocalTime.MIN,
            endTime = LocalTime.MAX,
            lessonRepeat = Lesson.Repeat.ByDates(emptySet()),
            semesterId = semesterId,
            id = 10L,
        )
        coEvery { lessonRepository.getCount(semesterId, "Subject") } returns 1
        coEvery { homeworkRepository.hasHomeworks(semesterId, "Subject") } returns true

        assertTrue(viewModel.isLastLessonOfSubjectAndHasHomeworks(lesson))
    }

    @Test
    fun `isLastLessonOfSubjectAndHasHomeworks false (count) test`() = runTest {
        val lesson = Lesson(
            subjectName = "Subject",
            type = "T",
            teachers = emptyList(),
            classrooms = emptyList(),
            startTime = LocalTime.MIN,
            endTime = LocalTime.MAX,
            lessonRepeat = Lesson.Repeat.ByDates(emptySet()),
            semesterId = semesterId,
            id = 10L,
        )
        coEvery { lessonRepository.getCount(semesterId, "Subject") } returns 2
        coEvery { homeworkRepository.hasHomeworks(semesterId, "Subject") } returns true

        assertFalse(viewModel.isLastLessonOfSubjectAndHasHomeworks(lesson))
    }

    @Test
    fun `isLastLessonOfSubjectAndHasHomeworks false (homeworks) test`() = runTest {
        val lesson = Lesson(
            subjectName = "Subject",
            type = "T",
            teachers = emptyList(),
            classrooms = emptyList(),
            startTime = LocalTime.MIN,
            endTime = LocalTime.MAX,
            lessonRepeat = Lesson.Repeat.ByDates(emptySet()),
            semesterId = semesterId,
            id = 10L,
        )
        coEvery { lessonRepository.getCount(semesterId, "Subject") } returns 1
        coEvery { homeworkRepository.hasHomeworks(semesterId, "Subject") } returns false

        assertFalse(viewModel.isLastLessonOfSubjectAndHasHomeworks(lesson))
    }

    @Test
    fun `deleteLesson without homeworks test`() = runTest {
        val lesson = Lesson(
            subjectName = "Subject",
            type = "T",
            teachers = emptyList(),
            classrooms = emptyList(),
            startTime = LocalTime.MIN,
            endTime = LocalTime.MAX,
            lessonRepeat = Lesson.Repeat.ByDates(emptySet()),
            semesterId = semesterId,
            id = 10L,
        )
        coEvery { lessonRepository.delete(lesson.id) } returns Unit

        viewModel.deleteLesson(lesson, withHomeworks = false)
        advanceUntilIdle()

        coVerify { lessonRepository.delete(lesson.id) }
        coVerify(exactly = 0) { homeworkRepository.delete(any<String>()) }
        verify { analytics.logEvent("lesson_deleted", any()) }
    }

    @Test
    fun `deleteLesson with homeworks test`() = runTest {
        val lesson = Lesson(
            subjectName = "Subject",
            type = "T",
            teachers = emptyList(),
            classrooms = emptyList(),
            startTime = LocalTime.MIN,
            endTime = LocalTime.MAX,
            lessonRepeat = Lesson.Repeat.ByDates(emptySet()),
            semesterId = semesterId,
            id = 10L,
        )
        coEvery { lessonRepository.delete(lesson.id) } returns Unit
        coEvery { homeworkRepository.delete(lesson.subjectName) } returns Unit

        viewModel.deleteLesson(lesson, withHomeworks = true)
        advanceUntilIdle()

        coVerify { lessonRepository.delete(lesson.id) }
        coVerify { homeworkRepository.delete(lesson.subjectName) }
        verify { analytics.logEvent("lesson_deleted", any()) }
    }

    @Test
    fun `getLessons test`() = runTest {
        val dayOfWeek = DayOfWeek.MONDAY
        val lesson = Lesson(
            subjectName = "Subject",
            type = "T",
            teachers = emptyList(),
            classrooms = emptyList(),
            startTime = LocalTime.MIN,
            endTime = LocalTime.MAX,
            lessonRepeat = Lesson.Repeat.ByWeekday(dayOfWeek, listOf(true)),
            semesterId = semesterId,
            id = 10L,
        )
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

        // Мы не проверяем, что список стал пустым, так как это зависит от мока репозитория, который не обновляет Flow
        coVerify { lessonRepository.delete(lesson.id) }

        job.cancel()
    }
}
