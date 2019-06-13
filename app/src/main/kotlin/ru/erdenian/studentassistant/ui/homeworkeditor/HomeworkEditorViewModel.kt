package ru.erdenian.studentassistant.ui.homeworkeditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import com.shopify.livedataktx.LiveDataKtx
import com.shopify.livedataktx.MediatorLiveDataKtx
import com.shopify.livedataktx.MutableLiveDataKtx
import com.shopify.livedataktx.map
import com.shopify.livedataktx.switchMap
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.extensions.asLiveData
import ru.erdenian.studentassistant.repository.ScheduleRepository
import ru.erdenian.studentassistant.repository.entity.HomeworkNew

class HomeworkEditorViewModel(application: Application) : AndroidViewModel(application) {

    enum class Error {
        EMPTY_DESCRIPTION
    }

    private val repository = ScheduleRepository(application)

    private val semesterId = MutableLiveDataKtx<Long>()
    private var homework: HomeworkNew? = null

    fun init(semesterId: Long, subjectName: String?) {
        this.semesterId.value = semesterId
        subjectName?.let { this.subjectName.value = it }
    }

    fun init(semesterId: Long, homework: HomeworkNew?) {
        this.semesterId.value = semesterId

        this.homework = homework?.also { h ->
            subjectName.value = h.subjectName
            description.value = h.description
            deadline.value = h.deadline
        }
    }

    val subjectName: MutableLiveDataKtx<String> = MediatorLiveDataKtx<String>().apply {
        addSource(existingSubjects, Observer { if (safeValue !in it) value = it.first() })
    }
    val description = MutableLiveDataKtx<String>().apply { value = "" }
    val deadline = MutableLiveDataKtx<LocalDate>().apply { value = LocalDate.now() }

    val error: LiveDataKtx<Error?> = MediatorLiveDataKtx<Error?>().apply {
        val onChanged = Observer<Any?> {
            value = when {
                description.safeValue?.isBlank() == true -> Error.EMPTY_DESCRIPTION
                else -> null
            }
        }

        addSource(description, onChanged)
    }

    val existingSubjects = semesterId.asLiveData.switchMap { repository.getSubjects(it) }
    val semesterLastDay = semesterId.asLiveData.switchMap { id ->
        repository.getSemester(id)
    }.map { checkNotNull(it).lastDay }

    suspend fun save(): Long {
        check(error.value == null)

        val oldHomework = homework
        val newHomework = HomeworkNew(
            subjectName.value,
            description.value,
            deadline.value,
            semesterId.value
        ).run { oldHomework?.let { copy(id = it.id) } ?: this }

        repository.insert(newHomework)
        return newHomework.id
    }

    suspend fun delete() {
        homework?.let { repository.delete(it) }
    }
}