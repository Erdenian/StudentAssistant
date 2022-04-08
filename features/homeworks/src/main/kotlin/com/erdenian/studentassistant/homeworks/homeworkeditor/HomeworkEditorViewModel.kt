package com.erdenian.studentassistant.homeworks.homeworkeditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.erdenian.studentassistant.entity.immutableSortedSetOf
import com.erdenian.studentassistant.repository.HomeworkRepository
import com.erdenian.studentassistant.repository.LessonRepository
import com.erdenian.studentassistant.repository.SemesterRepository
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
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance

class HomeworkEditorViewModel private constructor(
    application: Application,
    val semesterId: Long,
    private val homeworkId: Long?,
    subjectName: String?
) : AndroidViewModel(application), DIAware {

    override val di by closestDI()
    private val semesterRepository by instance<SemesterRepository>()
    private val lessonRepository by instance<LessonRepository>()
    private val homeworkRepository by instance<HomeworkRepository>()

    enum class Error {
        EMPTY_SUBJECT,
        EMPTY_DESCRIPTION
    }

    enum class Operation {
        LOADING,
        SAVING,
        DELETING
    }

    constructor(application: Application, semesterId: Long, subjectName: String? = null) :
            this(application, semesterId, null, subjectName)

    constructor(application: Application, semesterId: Long, homeworkId: Long) :
            this(application, semesterId, homeworkId, null)

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
            initialValue = immutableSortedSetOf()
        )
    val semesterDatesRange = semesterRepository.getFlow(semesterId)
        .filterNotNull()
        .onEach { isSemesterLoaded.value = true }
        .map { it.firstDay..it.lastDay }
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
        initialValue = null
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
                homeworkRepository.update(homeworkId, subjectName.value, description.value, deadline.value, semesterId)
            } else {
                homeworkRepository.insert(subjectName.value, description.value, deadline.value, semesterId)
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
