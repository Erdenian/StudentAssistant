package ru.erdenian.studentassistant.homeworks.homeworkeditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import ru.erdenian.studentassistant.entity.immutableSortedSetOf
import ru.erdenian.studentassistant.repository.HomeworkRepository
import ru.erdenian.studentassistant.repository.LessonRepository
import ru.erdenian.studentassistant.repository.SemesterRepository

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

    constructor(application: Application, semesterId: Long, subjectName: String? = null) :
            this(application, semesterId, null, subjectName)

    constructor(application: Application, semesterId: Long, homeworkId: Long) :
            this(application, semesterId, homeworkId, null)

    val subjectName = MutableStateFlow(subjectName ?: "")
    val description = MutableStateFlow("")
    val deadline = MutableStateFlow(LocalDate.now().plusWeeks(1))

    val existingSubjects = lessonRepository.getSubjects(semesterId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = immutableSortedSetOf()
        )
    val semesterDatesRange = semesterRepository.getFlow(semesterId)
        .filterNotNull()
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

    private val isLoadedPrivate = MutableStateFlow(homeworkId == null)
    val isLoaded = isLoadedPrivate.asStateFlow()

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
                isLoadedPrivate.value = true
            }
        }
    }

    fun save() {
        check(error.value == null)

        viewModelScope.launch {
            if (homeworkId != null) {
                homeworkRepository.update(homeworkId, subjectName.value, description.value, deadline.value, semesterId)
            } else {
                homeworkRepository.insert(subjectName.value, description.value, deadline.value, semesterId)
            }
            donePrivate.value = true
        }
    }

    fun delete() {
        viewModelScope.launch {
            homeworkRepository.delete(checkNotNull(homeworkId))
            donePrivate.value = true
        }
    }
}
