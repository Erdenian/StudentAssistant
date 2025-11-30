package ru.erdenian.studentassistant.schedule.lessoneditor

import android.app.Application
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalTime
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
import ru.erdenian.studentassistant.repository.api.HomeworkRepository
import ru.erdenian.studentassistant.repository.api.LessonRepository
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.api.SettingsRepository
import ru.erdenian.studentassistant.repository.api.entity.Lesson
import ru.erdenian.studentassistant.schedule.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
internal class LessonEditorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>()
    private val lessonRepository = mockk<LessonRepository>(relaxed = true)
    private val homeworkRepository = mockk<HomeworkRepository>(relaxed = true)
    private val settingsRepository = mockk<SettingsRepository>(relaxed = true)
    private val repositoryApi = mockk<RepositoryApi> {
        every { lessonRepository } returns this@LessonEditorViewModelTest.lessonRepository
        every { homeworkRepository } returns this@LessonEditorViewModelTest.homeworkRepository
        every { settingsRepository } returns this@LessonEditorViewModelTest.settingsRepository
    }

    private val semesterId = 1L
    private val defaultStartTime = LocalTime.of(9, 0)
    private val defaultDuration = Duration.ofMinutes(90)

    init {
        every { settingsRepository.defaultStartTime } returns defaultStartTime
        every { settingsRepository.defaultLessonDuration } returns defaultDuration
        every { settingsRepository.getAdvancedWeeksSelectorFlow(any()) } returns MutableStateFlow(false)
        every { lessonRepository.getSubjects(semesterId) } returns MutableStateFlow(emptyList())
        every { lessonRepository.getTypes(semesterId) } returns MutableStateFlow(emptyList())
        every { lessonRepository.getTeachers(semesterId) } returns MutableStateFlow(emptyList())
        every { lessonRepository.getClassrooms(semesterId) } returns MutableStateFlow(emptyList())
        coEvery { lessonRepository.getNextStartTime(semesterId, any()) } returns defaultStartTime
    }

    @Test
    fun `init new lesson test`() = runTest {
        val viewModel = LessonEditorViewModel(
            application, repositoryApi, semesterId, null, false, DayOfWeek.MONDAY, null
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.operation.collect() }
        advanceUntilIdle()

        assertEquals("", viewModel.subjectName.value)
        assertEquals(DayOfWeek.MONDAY, viewModel.dayOfWeek.value)
        assertEquals(defaultStartTime, viewModel.startTime.value)
        assertEquals(defaultStartTime.plus(defaultDuration), viewModel.endTime.value)
    }

    @Test
    fun `init existing lesson test`() = runTest {
        val lesson = Lesson(
            "Subject", "Type", listOf("T1"), listOf("C1"),
            LocalTime.of(10, 0), LocalTime.of(11, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.FRIDAY, listOf(true, false)),
            semesterId, 10L
        )
        coEvery { lessonRepository.get(10L) } returns lesson

        val viewModel = LessonEditorViewModel(
            application, repositoryApi, semesterId, 10L, false, null, null
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.operation.collect() }
        advanceUntilIdle()

        assertEquals("Subject", viewModel.subjectName.value)
        assertEquals("Type", viewModel.type.value)
        assertEquals("T1", viewModel.teachers.value)
        assertEquals("C1", viewModel.classrooms.value)
        assertEquals(DayOfWeek.FRIDAY, viewModel.dayOfWeek.value)
        assertEquals(listOf(true, false), viewModel.weeks.value)
    }

    @Test
    fun `save new lesson test`() = runTest {
        val viewModel = LessonEditorViewModel(
            application, repositoryApi, semesterId, null, false, DayOfWeek.MONDAY, null
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.operation.collect() }
        advanceUntilIdle()

        viewModel.subjectName.value = "Subject"
        viewModel.save()
        advanceUntilIdle()

        coVerify {
            lessonRepository.insert(
                subjectName = "Subject",
                type = "",
                teachers = emptySet(),
                classrooms = emptySet(),
                startTime = any(),
                endTime = any(),
                semesterId = semesterId,
                dayOfWeek = DayOfWeek.MONDAY,
                weeks = listOf(true)
            )
        }
        assertTrue(viewModel.done.value)
    }

    @Test
    fun `error test`() = runTest {
        val viewModel = LessonEditorViewModel(
            application, repositoryApi, semesterId, null, false, DayOfWeek.MONDAY, null
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.error.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.operation.collect() }
        advanceUntilIdle()

        assertEquals(LessonEditorViewModel.Error.EMPTY_SUBJECT_NAME, viewModel.error.value)

        viewModel.subjectName.value = "Subject"
        advanceUntilIdle()
        assertNull(viewModel.error.value)

        viewModel.endTime.value = viewModel.startTime.value.minusMinutes(1)
        advanceUntilIdle()
        assertEquals(LessonEditorViewModel.Error.WRONG_TIMES, viewModel.error.value)
    }
}
