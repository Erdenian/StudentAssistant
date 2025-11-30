package ru.erdenian.studentassistant.homeworks.homeworks

import android.app.Application
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import ru.erdenian.studentassistant.homeworks.MainDispatcherRule
import ru.erdenian.studentassistant.repository.api.HomeworkRepository
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.api.SelectedSemesterRepository
import ru.erdenian.studentassistant.repository.api.SemesterRepository
import ru.erdenian.studentassistant.repository.api.entity.Homework
import ru.erdenian.studentassistant.repository.api.entity.Semester

internal class HomeworksViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>()
    private val selectedSemesterRepository = mockk<SelectedSemesterRepository>(relaxed = true)
    private val semesterRepository = mockk<SemesterRepository>(relaxed = true)
    private val homeworkRepository = mockk<HomeworkRepository>(relaxed = true)
    private val repositoryApi = mockk<RepositoryApi> {
        every { selectedSemesterRepository } returns this@HomeworksViewModelTest.selectedSemesterRepository
        every { semesterRepository } returns this@HomeworksViewModelTest.semesterRepository
        every { homeworkRepository } returns this@HomeworksViewModelTest.homeworkRepository
    }

    private val selectedSemesterFlow = MutableStateFlow<Semester?>(null)
    private val allSemestersFlow = MutableStateFlow<List<Semester>>(emptyList())
    private val overdueFlow = MutableStateFlow<List<Homework>>(emptyList())
    private val actualFlow = MutableStateFlow<List<Homework>>(emptyList())
    private val pastFlow = MutableStateFlow<List<Homework>>(emptyList())

    init {
        every { selectedSemesterRepository.selectedFlow } returns selectedSemesterFlow
        every { semesterRepository.allFlow } returns allSemestersFlow
        every { homeworkRepository.overdueFlow } returns overdueFlow
        every { homeworkRepository.actualFlow } returns actualFlow
        every { homeworkRepository.pastFlow } returns pastFlow
    }

    private val viewModel by lazy { HomeworksViewModel(application, repositoryApi) }

    @Test
    fun `selectSemester test`() {
        val id = 10L
        viewModel.selectSemester(id)
        coVerify { selectedSemesterRepository.selectSemester(id) }
    }

    @Test
    fun `deleteHomework test`() = runTest {
        val homework = Homework("Subject", "Description", LocalDate.now(), false, 1L, 10L)
        overdueFlow.value = listOf(homework)

        assertEquals(listOf(homework), viewModel.overdue.first())

        viewModel.deleteHomework(homework.id)

        // Проверяем, что удаление вызвалось в репозитории
        coVerify { homeworkRepository.delete(homework.id) }

        // Проверяем, что ID добавился в список удаленных и фильтруется локально
        // (так как overdueFlow.value мы не меняли, список должен стать пустым только благодаря фильтрации во ViewModel)
        assertEquals(emptyList<Homework>(), viewModel.overdue.first())
    }
}
