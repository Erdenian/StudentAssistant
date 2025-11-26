package ru.erdenian.studentassistant.homeworks.homeworkeditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.erdenian.studentassistant.repository.api.RepositoryApi

internal class HomeworkEditorViewModel @AssistedInject constructor(
    application: Application,
    repositoryApi: RepositoryApi,
    @Assisted val semesterId: Long,
    @Assisted private val homeworkId: Long?,
    @Assisted subjectName: String?,
) : AndroidViewModel(application) {

    private val semesterRepository = repositoryApi.semesterRepository
    private val lessonRepository = repositoryApi.lessonRepository
    private val homeworkRepository = repositoryApi.homeworkRepository

    @AssistedFactory
    abstract class Factory {
        internal abstract fun getInternal(
            semesterId: Long,
            homeworkId: Long? = null,
            subjectName: String? = null,
        ): HomeworkEditorViewModel

        fun get(semesterId: Long, subjectName: String? = null) = getInternal(semesterId, subjectName = subjectName)
        fun get(semesterId: Long, homeworkId: Long) = getInternal(semesterId, homeworkId = homeworkId)
    }

    enum class Error {
        EMPTY_SUBJECT,
        EMPTY_DESCRIPTION,
    }

    enum class Operation {
        LOADING,
        SAVING,
        DELETING,
    }

    private val isHomeworkLoaded = MutableStateFlow(homeworkId == null)
    private val areSubjectsLoaded = MutableStateFlow(false)
    private val isSemesterLoaded = MutableStateFlow(false)
    private val operationPrivate = MutableStateFlow<Operation?>(Operation.LOADING)
    val operation = operationPrivate.asStateFlow()

    val subjectName = MutableStateFlow(subjectName.orEmpty())
    val description = MutableStateFlow("")
    val deadline = MutableStateFlow(LocalDate.now().plusWeeks(1))

    val existingSubjects = lessonRepository.getSubjects(semesterId)
        .onEach { areSubjectsLoaded.value = true }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList(),
        )
    val semesterDatesRange = semesterRepository.getFlow(semesterId)
        .filterNotNull()
        .onEach { isSemesterLoaded.value = true }
        .map { it.dateRange }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), LocalDate.now()..LocalDate.now())

    val error = combine(this.subjectName, description) { subjectName, description ->
        when {
            subjectName.isBlank() -> Error.EMPTY_SUBJECT
            description.isBlank() -> Error.EMPTY_DESCRIPTION
            else -> null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null,
    )

    val isEditing get() = (homeworkId != null)
    val lessonExists get() = subjectName.value in existingSubjects.value

    private val donePrivate = MutableStateFlow(false)
    val done: StateFlow<Boolean> get() = donePrivate.asStateFlow()

    init {
        if (homeworkId != null) {
            viewModelScope.launch {
                val homework = homeworkRepository.get(homeworkId)
                if (homework != null) {
                    this@HomeworkEditorViewModel.subjectName.value = homework.subjectName
                    description.value = homework.description
                    deadline.value = homework.deadline
                } else {
                    // Homework was deleted
                    donePrivate.value = true
                }
                isHomeworkLoaded.value = true
            }
        }

        viewModelScope.launch {
            combine(isHomeworkLoaded, areSubjectsLoaded, isSemesterLoaded) { homework, subjects, semester ->
                homework && subjects && semester
            }.filter { it }.first()
            if (operationPrivate.value == Operation.LOADING) operationPrivate.value = null
        }
    }

    fun save() {
        check(error.value == null)

        operationPrivate.value = Operation.SAVING
        viewModelScope.launch {
            if (homeworkId != null) {
                homeworkRepository.update(
                    id = homeworkId,
                    subjectName = subjectName.value,
                    description = description.value,
                    deadline = deadline.value,
                    semesterId = semesterId,
                )
            } else {
                homeworkRepository.insert(
                    subjectName = subjectName.value,
                    description = description.value,
                    deadline = deadline.value,
                    semesterId = semesterId,
                )
            }

            operationPrivate.value = null
            donePrivate.value = true
        }
    }

    fun delete() {
        operationPrivate.value = Operation.DELETING
        viewModelScope.launch {
            homeworkRepository.delete(checkNotNull(homeworkId))
            operationPrivate.value = null
            donePrivate.value = true
        }
    }
}
