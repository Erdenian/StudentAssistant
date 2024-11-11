package com.erdenian.studentassistant.schedule.scheduleeditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.erdenian.studentassistant.entity.Lesson
import com.erdenian.studentassistant.entity.toImmutableSortedSet
import com.erdenian.studentassistant.repository.api.RepositoryApi
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
        if (deletedIds.isEmpty()) {
            lessons
        } else {
            lessons.asSequence().filter { it.id !in deletedIds }.toImmutableSortedSet()
        }
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

            deletedLessonIds.value += lesson.id
            operationPrivate.value = null
        }
    }
}
