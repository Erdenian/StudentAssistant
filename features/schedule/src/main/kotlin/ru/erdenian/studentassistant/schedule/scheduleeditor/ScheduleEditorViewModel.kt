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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.api.entity.Lesson

internal class ScheduleEditorViewModel @AssistedInject constructor(
    application: Application,
    repositoryApi: RepositoryApi,
    @Assisted val semesterId: Long,
) : AndroidViewModel(application) {

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
    val operation = operationPrivate.asStateFlow()

    private val isDeletedPrivate = MutableStateFlow(false)
    val isDeleted = isDeletedPrivate.asStateFlow()

    private val deletedLessonIds = MutableStateFlow(emptySet<Long>())

    fun getLessons(dayOfWeek: DayOfWeek) = combine(
        lessonRepository.getAllFlow(semesterId, dayOfWeek).onEach { deletedLessonIds.value = emptySet() },
        deletedLessonIds.asStateFlow(),
    ) { lessons, deletedIds ->
        if (deletedIds.isEmpty()) lessons else lessons.filter { it.id !in deletedIds }
    }

    fun deleteSemester() {
        operationPrivate.value = Operation.DELETING_SEMESTER
        viewModelScope.launch {
            semesterRepository.delete(semesterId)
            operationPrivate.value = null
            isDeletedPrivate.value = true
        }
    }

    suspend fun isLastLessonOfSubjectsAndHasHomeworks(lesson: Lesson): Boolean = coroutineScope {
        val subjectName = lesson.subjectName
        val isLastLesson = async { lessonRepository.getCount(semesterId, subjectName) == 1 }
        val hasHomeworks = async { homeworkRepository.hasHomeworks(semesterId, subjectName) }
        isLastLesson.await() && hasHomeworks.await()
    }

    fun deleteLesson(lesson: Lesson, withHomeworks: Boolean = false) {
        operationPrivate.value = Operation.DELETING_LESSON
        viewModelScope.launch {
            coroutineScope {
                val deleteLesson = async { lessonRepository.delete(lesson.id) }
                val deleteHomeworks = async { if (withHomeworks) homeworkRepository.delete(lesson.subjectName) }

                deleteLesson.await()
                deleteHomeworks.await()
            }

            /*
             * Добавляем ID удаленного урока в список исключенных.
             *
             * Это необходимо для предотвращения "мерцания" элемента в списке (LessonCard).
             * Room обновляет Flow асинхронно. Может возникнуть ситуация, когда:
             * 1. Транзакция удаления в БД завершилась (await() прошел).
             * 2. Прогресс-бар скрылся (operationPrivate.value = null).
             * 3. А Flow от Room еще не успел эмитировать новый список без этого урока.
             *
             * В этот момент пользователь снова увидел бы удаленный урок.
             * Добавляя ID в deletedLessonIds, мы принудительно фильтруем его в getLessons().
             * Когда Room наконец пришлет обновленный список, сработает onEach в getLessons,
             * который очистит deletedLessonIds.
             */
            deletedLessonIds.value += lesson.id
            operationPrivate.value = null
        }
    }
}
