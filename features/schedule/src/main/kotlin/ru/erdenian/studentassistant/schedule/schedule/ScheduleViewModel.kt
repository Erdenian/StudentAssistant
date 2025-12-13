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

/**
 * ViewModel для главного экрана расписания.
 *
 * Отвечает за:
 * - Отображение списка семестров и текущего выбранного семестра.
 * - Переключение семестров.
 * - Предоставление списка занятий для конкретных дат.
 */
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

    /**
     * Поток текущего выбранного семестра.
     */
    val selectedSemester = selectedSemesterRepository.selectedFlow

    /**
     * Поток списка всех семестров.
     */
    val allSemesters = semesterRepository.allFlow
        .stateIn(viewModelScope, SharingStarted.Default, listOfNotNull(selectedSemester.value))

    /**
     * Выбирает семестр по идентификатору.
     *
     * Также очищает кэш потоков занятий, так как они зависят от выбранного семестра.
     *
     * @param semesterId идентификатор семестра.
     */
    fun selectSemester(semesterId: Long) {
        lessonsFlows.clear()
        selectedSemesterRepository.selectSemester(semesterId)
    }

    // Храним потоки для последних запрошенных дней.
    // Этого достаточно для свайпов влево/вправо и поворота экрана,
    // но предотвращает бесконечный рост памяти.
    private val lessonsFlows = object : LinkedHashMap<LocalDate, Flow<List<Lesson>>>(
        LESSONS_FLOWS_CACHE_SIZE, LESSONS_FLOWS_LOAD_FACTOR, true,
    ) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<LocalDate, Flow<List<Lesson>>>) =
            size > LESSONS_FLOWS_CACHE_SIZE
    }

    /**
     * Возвращает поток списка занятий для указанной даты.
     *
     * Потоки кэшируются для предотвращения создания лишних запросов к БД при перерисовке UI.
     *
     * @param day дата, для которой нужно получить расписание.
     */
    fun getLessons(day: LocalDate): Flow<List<Lesson>> = synchronized(lessonsFlows) {
        lessonsFlows.getOrPut(day) {
            lessonRepository.getAllFlow(day).shareIn(viewModelScope, SharingStarted.Default, replay = 1)
        }
    }
}
