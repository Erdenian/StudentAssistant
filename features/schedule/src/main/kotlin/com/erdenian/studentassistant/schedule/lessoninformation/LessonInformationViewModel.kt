package com.erdenian.studentassistant.schedule.lessoninformation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.erdenian.studentassistant.entity.emptyImmutableSortedSet
import com.erdenian.studentassistant.entity.toImmutableSortedSet
import com.erdenian.studentassistant.repository.api.RepositoryApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class LessonInformationViewModel @AssistedInject constructor(
    application: Application,
    repositoryApi: RepositoryApi,
    @Assisted lessonId: Long,
) : AndroidViewModel(application) {

    private val lessonRepository = repositoryApi.lessonRepository
    private val homeworkRepository = repositoryApi.homeworkRepository

    @AssistedFactory
    interface Factory {
        fun get(lessonId: Long): LessonInformationViewModel
    }

    enum class Operation {
        DELETING_HOMEWORK,
    }

    private val operationPrivate = MutableStateFlow<Operation?>(null)
    val operation = operationPrivate.asStateFlow()

    private val lessonPrivate = lessonRepository.getFlow(lessonId)
        .shareIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed())

    val lesson =
        lessonPrivate.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(), initialValue = null)

    val isDeleted = lessonPrivate.map { it == null }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val deletedHomeworkIds = MutableStateFlow(emptySet<Long>())

    val homeworks = combine(
        lessonPrivate.flatMapLatest { lesson ->
            if (lesson != null) {
                homeworkRepository.getActualFlow(lesson.subjectName)
            } else {
                flowOf(emptyImmutableSortedSet())
            }
        }.onEach { deletedHomeworkIds.value = emptySet() },
        deletedHomeworkIds,
    ) { homeworks, deletedIds ->
        if (deletedIds.isEmpty()) {
            homeworks
        } else {
            homeworks.asSequence().filter { it.id !in deletedIds }.toImmutableSortedSet()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun deleteHomework(id: Long) {
        operationPrivate.value = Operation.DELETING_HOMEWORK
        viewModelScope.launch {
            homeworkRepository.delete(id)
            deletedHomeworkIds.value += id
            operationPrivate.value = null
        }
    }
}
