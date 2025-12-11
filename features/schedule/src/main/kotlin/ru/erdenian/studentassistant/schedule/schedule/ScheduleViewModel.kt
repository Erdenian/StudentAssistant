package ru.erdenian.studentassistant.schedule.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.api.entity.Lesson
import ru.erdenian.studentassistant.utils.Default

internal class ScheduleViewModel @Inject constructor(
    application: Application,
    repositoryApi: RepositoryApi,
) : AndroidViewModel(application) {

    private companion object {
        private const val LESSONS_FLOWS_CACHE_SIZE = 15
        private const val LESSONS_FLOWS_LOAD_FACTOR = 0.75f
    }

    private val selectedSemesterRepository = repositoryApi.selectedSemesterRepository
    private val semesterRepository = repositoryApi.semesterRepository
    private val lessonRepository = repositoryApi.lessonRepository

    val selectedSemester = selectedSemesterRepository.selectedFlow
    val allSemesters = semesterRepository.allFlow
        .stateIn(viewModelScope, SharingStarted.Default, listOfNotNull(selectedSemester.value))

    fun selectSemester(semesterId: Long) = selectedSemesterRepository.selectSemester(semesterId)

    // Храним потоки для последних запрошенных дней.
    // Этого достаточно для свайпов влево/вправо и поворота экрана,
    // но предотвращает бесконечный рост памяти.
    private val lessonsFlows = object : LinkedHashMap<LocalDate, Flow<List<Lesson>>>(
        LESSONS_FLOWS_CACHE_SIZE, LESSONS_FLOWS_LOAD_FACTOR, true,
    ) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<LocalDate, Flow<List<Lesson>>>) =
            size > LESSONS_FLOWS_CACHE_SIZE
    }

    fun getLessons(day: LocalDate): Flow<List<Lesson>> = synchronized(lessonsFlows) {
        lessonsFlows.getOrPut(day) {
            lessonRepository.getAllFlow(day).shareIn(viewModelScope, SharingStarted.Default, replay = 1)
        }
    }
}
