package ru.erdenian.studentassistant.schedule.lessoninformation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.api.entity.Lesson
import ru.erdenian.studentassistant.utils.Default

internal class LessonInformationViewModel @AssistedInject constructor(
    application: Application,
    repositoryApi: RepositoryApi,
    @Assisted lessonArg: Lesson,
) : AndroidViewModel(application) {

    private val lessonRepository = repositoryApi.lessonRepository
    private val homeworkRepository = repositoryApi.homeworkRepository

    @AssistedFactory
    interface Factory {
        fun get(lessonArg: Lesson): LessonInformationViewModel
    }

    enum class Operation {
        DELETING_HOMEWORK,
    }

    private val operationPrivate = MutableStateFlow<Operation?>(null)
    val operation = operationPrivate.asStateFlow()

    private val lessonPrivate = lessonRepository.getFlow(lessonArg.id)
        .shareIn(scope = viewModelScope, started = SharingStarted.Default)

    val lesson = lessonPrivate.stateIn(viewModelScope, SharingStarted.Default, lessonArg)

    val isDeleted = lessonPrivate.map { it == null }.stateIn(viewModelScope, SharingStarted.Default, false)

    val homeworks = lessonPrivate.flatMapLatest { lesson ->
        lesson?.let { homeworkRepository.getActualFlow(it.subjectName) } ?: flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.Default, null)

    fun deleteHomework(id: Long) {
        operationPrivate.value = Operation.DELETING_HOMEWORK
        viewModelScope.launch {
            homeworkRepository.delete(id)
            operationPrivate.value = null
        }
    }
}
