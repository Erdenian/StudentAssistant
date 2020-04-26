package ru.erdenian.studentassistant.ui.homeworkeditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.repository.HomeworkRepository
import ru.erdenian.studentassistant.repository.LessonRepository
import ru.erdenian.studentassistant.repository.SemesterRepository
import ru.erdenian.studentassistant.utils.setIfEmpty

class HomeworkEditorViewModel(
    application: Application
) : AndroidViewModel(application), KodeinAware {

    override val kodein by kodein()
    private val semesterRepository by instance<SemesterRepository>()
    private val lessonRepository by instance<LessonRepository>()
    private val homeworkRepository by instance<HomeworkRepository>()

    enum class Error {
        EMPTY_DESCRIPTION
    }

    private val semesterIdPrivate = MutableLiveData<Long>()
    val semesterId: LiveData<Long> get() = semesterIdPrivate
    private var homework: Homework? = null

    fun init(semesterId: Long, subjectName: String?) {
        this.semesterIdPrivate.setIfEmpty(semesterId)
        subjectName?.let { this.subjectName.value = it }
    }

    fun init(semesterId: Long, homework: Homework?) {
        this.semesterIdPrivate.setIfEmpty(semesterId)

        this.homework = homework?.also { h ->
            subjectName.value = h.subjectName
            description.value = h.description
            deadline.value = h.deadline
        }
    }

    val existingSubjects = semesterId.switchMap { lessonRepository.getSubjects(it) }
    val semesterLastDay = semesterId
        .switchMap { semesterRepository.getLiveData(it) }
        .map { checkNotNull(it).lastDay }

    val subjectName: MutableLiveData<String> = MediatorLiveData<String>().apply {
        addSource(existingSubjects) { if (value !in it) value = it.firstOrNull() ?: "" }
    }
    val description = MutableLiveData("")
    val deadline = MutableLiveData(LocalDate.now())

    val error: LiveData<Error?> = MediatorLiveData<Error?>().apply {
        val observer = Observer<Any?> {
            value = if (description.value?.isBlank() != false) Error.EMPTY_DESCRIPTION else null
        }

        addSource(description, observer)
    }

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
                    checkNotNull(semesterId.value)
                )
            } ?: run {
                homeworkRepository.insert(
                    checkNotNull(subjectName.value),
                    checkNotNull(description.value),
                    checkNotNull(deadline.value),
                    checkNotNull(semesterId.value)
                )
            }
        }
    }

    fun delete() {
        viewModelScope.launch { homeworkRepository.delete(checkNotNull(homework).id) }
    }
}
