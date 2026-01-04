package ru.erdenian.studentassistant.schedule.lessoninformation

import android.app.Application
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import ru.erdenian.studentassistant.analytics.api.Analytics
import ru.erdenian.studentassistant.analytics.api.AnalyticsApi
import ru.erdenian.studentassistant.repository.api.HomeworkRepository
import ru.erdenian.studentassistant.repository.api.LessonRepository
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.api.entity.Homework
import ru.erdenian.studentassistant.repository.api.entity.Lesson
import ru.erdenian.studentassistant.schedule.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
internal class LessonInformationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>()
    private val lessonRepository = mockk<LessonRepository>()
    private val homeworkRepository = mockk<HomeworkRepository>()
    private val analytics = mockk<Analytics>(relaxed = true)
    private val repositoryApi = mockk<RepositoryApi> {
        every { lessonRepository } returns this@LessonInformationViewModelTest.lessonRepository
        every { homeworkRepository } returns this@LessonInformationViewModelTest.homeworkRepository
    }
    private val analyticsApi = mockk<AnalyticsApi> {
        every { analytics } returns this@LessonInformationViewModelTest.analytics
    }

    private val lesson = Lesson(
        "Subject", "T", emptyList(), emptyList(), LocalTime.MIN, LocalTime.MAX,
        Lesson.Repeat.ByDates(emptySet()), 1L, 10L,
    )

    private val lessonFlow = MutableStateFlow<Lesson?>(lesson)
    private val homeworksFlow = MutableStateFlow<List<Homework>>(emptyList())

    init {
        every { lessonRepository.getFlow(lesson.id) } returns lessonFlow
        every { homeworkRepository.getActualFlow(lesson.subjectName) } returns homeworksFlow
    }

    private val viewModel by lazy {
        LessonInformationViewModel(application, repositoryApi, analyticsApi, lesson)
    }

    @Test
    fun `init test`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.homeworks.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.lesson.collect() }
        advanceUntilIdle()

        // Ожидаем, пока придет не null значение
        viewModel.homeworks.filterNotNull().first()

        assertEquals(lesson, viewModel.lesson.value)
        assertFalse(viewModel.isDeleted.value)
        assertEquals(emptyList<Homework>(), viewModel.homeworks.value)
    }

    @Test
    fun `logEditLessonClicked test`() {
        viewModel.logEditLessonClicked()
        verify { analytics.logEvent("lesson_edit_clicked", any()) }
    }

    @Test
    fun `logAddHomeworkClicked test`() {
        viewModel.logAddHomeworkClicked()
        verify { analytics.logEvent("homework_add_clicked", any()) }
    }

    @Test
    fun `logHomeworkClicked test`() {
        val homework = Homework("Subject", "D", LocalDate.MAX, false, 1L, 100L)
        viewModel.logHomeworkClicked(homework)
        verify { analytics.logEvent("homework_clicked", mapOf("subject_name" to "Subject")) }
    }

    @Test
    fun `deleteHomework test`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.homeworks.collect() }
        val homework = Homework("Subject", "D", LocalDate.MAX, false, 1L, 100L)
        homeworksFlow.value = listOf(homework)
        coEvery { homeworkRepository.delete(homework.id) } returns Unit

        // Ожидаем обновления списка домашних заданий
        viewModel.homeworks.filterNotNull().first()
        advanceUntilIdle()

        assertEquals(listOf(homework), viewModel.homeworks.value)

        viewModel.deleteHomework(homework.id)
        advanceUntilIdle()

        coVerify { homeworkRepository.delete(homework.id) }
        verify { analytics.logEvent("homework_deleted", any()) }
    }

    @Test
    fun `isDeleted test`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.isDeleted.collect() }
        advanceUntilIdle()

        assertFalse(viewModel.isDeleted.value)
        lessonFlow.value = null
        advanceUntilIdle()
        assertTrue(viewModel.isDeleted.value)
    }
}
