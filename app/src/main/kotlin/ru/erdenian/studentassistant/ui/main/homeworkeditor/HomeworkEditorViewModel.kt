package ru.erdenian.studentassistant.ui.main.homeworkeditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.repository.HomeworkRepository
import ru.erdenian.studentassistant.repository.LessonRepository
import ru.erdenian.studentassistant.repository.SemesterRepository

class HomeworkEditorViewModel private constructor(
    application: Application,
    val semesterId: Long,
    private val homework: Homework?,
    lesson: Lesson?
) : AndroidViewModel(application), DIAware {

    override val di by closestDI()
    private val semesterRepository by instance<SemesterRepository>()
    private val lessonRepository by instance<LessonRepository>()
    private val homeworkRepository by instance<HomeworkRepository>()

    enum class Error {
        EMPTY_SUBJECT,
        EMPTY_DESCRIPTION
    }

    constructor(application: Application, semesterId: Long) : this(application, semesterId, null, null)
    constructor(application: Application, lesson: Lesson) : this(application, lesson.semesterId, null, lesson)
    constructor(application: Application, homework: Homework) : this(application, homework.semesterId, homework, null)

    val existingSubjects = lessonRepository.getSubjects(semesterId)
    val semesterLastDay = semesterRepository.getLiveData(semesterId).map { checkNotNull(it).lastDay }

    val subjectName = MutableLiveData(lesson?.subjectName ?: homework?.subjectName ?: "")
    val description = MutableLiveData(homework?.description ?: "")
    val deadline = MutableLiveData(homework?.deadline ?: LocalDate.now().plusWeeks(1))

    val error: LiveData<Error?> = MediatorLiveData<Error?>().apply {
        val observer = Observer<Any?> {
            value = when {
                subjectName.value.isNullOrBlank() -> Error.EMPTY_SUBJECT
                description.value.isNullOrBlank() -> Error.EMPTY_DESCRIPTION
                else -> null
            }
        }

        addSource(subjectName, observer)
        addSource(description, observer)
    }

    val isEditing get() = (homework != null)

    val lessonExists get() = checkNotNull(subjectName.value) in checkNotNull(existingSubjects.value)

    private val donePrivate = MutableLiveData(false)
    val done: LiveData<Boolean> get() = donePrivate

    fun save() {
        check(error.value == null)

        viewModelScope.launch {
            homework?.let { homework ->
                homeworkRepository.update(
                    homework.id,
                    checkNotNull(subjectName.value),
                    checkNotNull(description.value),
                    checkNotNull(deadline.value),
                    semesterId
                )
            } ?: run {
                homeworkRepository.insert(
                    checkNotNull(subjectName.value),
                    checkNotNull(description.value),
                    checkNotNull(deadline.value),
                    semesterId
                )
            }
            donePrivate.value = true
        }
    }

    fun delete() {
        viewModelScope.launch {
            homeworkRepository.delete(checkNotNull(homework).id)
            donePrivate.value = true
        }
    }
}
