package ru.erdenian.studentassistant.schedule.scheduleeditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.time.DayOfWeek
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.api.entity.Lesson
import ru.erdenian.studentassistant.utils.Default

/**
 * ViewModel для экрана редактора расписания.
 *
 * Позволяет просматривать и редактировать расписание.
 * Поддерживает удаление расписания и удаление отдельных занятий (с опциональным удалением домашних заданий).
 *
 * @param semesterId идентификатор редактируемого расписания.
 */
internal class ScheduleEditorViewModel @AssistedInject constructor(
    application: Application,
    repositoryApi: RepositoryApi,
    @Assisted val semesterId: Long,
) : AndroidViewModel(application) {

    private companion object {
        private const val LESSONS_FLOWS_CACHE_SIZE = 7
        private const val LESSONS_FLOWS_LOAD_FACTOR = 0.75f
    }

    private val semesterRepository = repositoryApi.semesterRepository
    private val lessonRepository = repositoryApi.lessonRepository
    private val homeworkRepository = repositoryApi.homeworkRepository

    @AssistedFactory
    interface Factory {
        fun get(semesterId: Long): ScheduleEditorViewModel
    }

    enum class Operation {
        DELETING_LESSON,
        DELETING_SEMESTER,
    }

    private val operationPrivate = MutableStateFlow<Operation?>(null)

    /** Поток текущей выполняемой операции (для отображения индикатора прогресса). */
    val operation = operationPrivate.asStateFlow()

    private val isDeletedPrivate = MutableStateFlow(false)

    /** Поток флага удаления расписания (для навигации назад после удаления). */
    val isDeleted = isDeletedPrivate.asStateFlow()

    private val lessonsFlows = object : LinkedHashMap<DayOfWeek, Flow<List<Lesson>>>(
        LESSONS_FLOWS_CACHE_SIZE, LESSONS_FLOWS_LOAD_FACTOR, true,
    ) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<DayOfWeek, Flow<List<Lesson>>>) =
            size > LESSONS_FLOWS_CACHE_SIZE
    }

    /**
     * Возвращает поток занятий для указанного дня недели в текущем расписании.
     */
    fun getLessons(dayOfWeek: DayOfWeek): Flow<List<Lesson>> = synchronized(lessonsFlows) {
        lessonsFlows.getOrPut(dayOfWeek) {
            lessonRepository.getAllFlow(semesterId, dayOfWeek)
                .shareIn(viewModelScope, SharingStarted.Default, replay = 1)
        }
    }

    /**
     * Удаляет текущее расписание.
     */
    fun deleteSemester() {
        operationPrivate.value = Operation.DELETING_SEMESTER
        viewModelScope.launch {
            semesterRepository.delete(semesterId)
            isDeletedPrivate.value = true
        }
    }

    /**
     * Проверяет, является ли удаляемое занятие последним по данному предмету и есть ли для него домашние задания.
     *
     * Используется для отображения диалога с предложением удалить также и домашние задания.
     */
    suspend fun isLastLessonOfSubjectsAndHasHomeworks(lesson: Lesson): Boolean = coroutineScope {
        val subjectName = lesson.subjectName
        val isLastLesson = async { lessonRepository.getCount(semesterId, subjectName) == 1 }
        val hasHomeworks = async { homeworkRepository.hasHomeworks(semesterId, subjectName) }
        isLastLesson.await() && hasHomeworks.await()
    }

    /**
     * Удаляет занятие.
     *
     * @param lesson удаляемое занятие.
     * @param withHomeworks если true, удаляет также все домашние задания по этому предмету.
     */
    fun deleteLesson(lesson: Lesson, withHomeworks: Boolean = false) {
        operationPrivate.value = Operation.DELETING_LESSON
        viewModelScope.launch {
            coroutineScope {
                val deleteLesson = async { lessonRepository.delete(lesson.id) }
                val deleteHomeworks = async { if (withHomeworks) homeworkRepository.delete(lesson.subjectName) }

                deleteLesson.await()
                deleteHomeworks.await()
            }
            operationPrivate.value = null
        }
    }
}
