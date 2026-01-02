package ru.erdenian.studentassistant.schedule.schedule

import android.app.Application
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import ru.erdenian.studentassistant.repository.api.LessonRepository
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.api.SelectedSemesterRepository
import ru.erdenian.studentassistant.repository.api.SemesterRepository
import ru.erdenian.studentassistant.repository.api.entity.Lesson
import ru.erdenian.studentassistant.repository.api.entity.Semester
import ru.erdenian.studentassistant.schedule.MainDispatcherRule

internal class ScheduleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>()
    private val selectedSemesterRepository = mockk<SelectedSemesterRepository>(relaxed = true)
    private val semesterRepository = mockk<SemesterRepository>()
    private val lessonRepository = mockk<LessonRepository>()
    private val repositoryApi = mockk<RepositoryApi> {
        every { selectedSemesterRepository } returns this@ScheduleViewModelTest.selectedSemesterRepository
        every { semesterRepository } returns this@ScheduleViewModelTest.semesterRepository
        every { lessonRepository } returns this@ScheduleViewModelTest.lessonRepository
    }

    private val selectedSemesterFlow = MutableStateFlow<Semester?>(null)
    private val allSemestersFlow = MutableStateFlow<List<Semester>>(emptyList())

    init {
        every { selectedSemesterRepository.selectedFlow } returns selectedSemesterFlow
        every { semesterRepository.allFlow } returns allSemestersFlow
    }

    private val viewModel by lazy { ScheduleViewModel(application, repositoryApi) }

    @Test
    fun `init test`() {
        assertEquals(selectedSemesterFlow.value, viewModel.selectedSemester.value)
        assertEquals(allSemestersFlow.value, viewModel.allSemesters.value)
    }

    @Test
    fun `selectSemester test`() {
        val semesterId = 10L
        viewModel.selectSemester(semesterId)
        verify { selectedSemesterRepository.selectSemester(semesterId) }
    }

    @Test
    fun `getLessons test`() = runTest {
        // Используем фиксированную дату
        val date = LocalDate.of(2023, 2, 14)
        val lessons = listOf(mockk<Lesson>())
        val lessonsFlow = flowOf(lessons)
        every { lessonRepository.getAllFlow(date) } returns lessonsFlow

        assertEquals(lessons, viewModel.getLessons(date).first())
    }
}
