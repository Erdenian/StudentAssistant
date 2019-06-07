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
import ru.erdenian.studentassistant.repository.ScheduleRepository
import ru.erdenian.studentassistant.repository.entity.HomeworkNew

class HomeworkEditorViewModel(application: Application) : AndroidViewModel(application) {

    enum class Error {
        EMPTY_DESCRIPTION
    }

    private val repository = ScheduleRepository(application)

    val semesterId = MutableLiveDataKtx<Long>()

    val subjectName: MutableLiveDataKtx<String> = MediatorLiveDataKtx<String>().apply {
        value = ""
        addSource(existingSubjects, Observer { if (value !in it) value = it.first() })
    }
    val description = MutableLiveDataKtx<String>().apply { value = "" }
    val deadline = MutableLiveDataKtx<LocalDate>().apply { value = LocalDate.now() }

    val error: LiveDataKtx<Error?> = MediatorLiveDataKtx<Error?>().apply {
        val onChanged = Observer<Any?> {
            value = when {
                description.value.isBlank() -> Error.EMPTY_DESCRIPTION
                else -> null
            }
        }

        addSource(subjectName, onChanged)
        addSource(description, onChanged)
        addSource(deadline, onChanged)
    }

    val existingSubjects = semesterId.switchMap { repository.getSubjects(it) }
    val semesterLastDay = semesterId.switchMap { id ->
        repository.getSemester(id)
    }.map { checkNotNull(it).lastDay }

    private var homework: HomeworkNew? = null

    fun setHomework(homework: HomeworkNew) {
        subjectName.value = homework.subjectName
        description.value = homework.description
        deadline.value = homework.deadline
    }

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
