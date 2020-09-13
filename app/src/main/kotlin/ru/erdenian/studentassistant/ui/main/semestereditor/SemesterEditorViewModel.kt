package ru.erdenian.studentassistant.ui.main.semestereditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import org.kodein.di.DIAware
import org.kodein.di.android.x.di
import org.kodein.di.instance
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.repository.SemesterRepository

class SemesterEditorViewModel(
    application: Application,
    private val semester: Semester?
) : AndroidViewModel(application), DIAware {

    override val di by di()
    private val semesterRepository by instance<SemesterRepository>()

    enum class Error {
        EMPTY_NAME,
        SEMESTER_EXISTS,
        WRONG_DATES
    }

    private val ranges = listOf(
        DateTimeConstants.FEBRUARY..DateTimeConstants.MAY,
        DateTimeConstants.SEPTEMBER..DateTimeConstants.DECEMBER
    )

    val name = MutableLiveData("")
    val firstDay = MutableLiveData<LocalDate>()
    val lastDay = MutableLiveData<LocalDate>()

    init {
        semester?.also { s ->
            name.value = s.name
            firstDay.value = s.firstDay
            lastDay.value = s.lastDay
        }

        val today = LocalDate.now().withDayOfMonth(1)
        val range = ranges.find { today.monthOfYear <= it.last } ?: ranges.first()
        firstDay.value = today.withMonthOfYear(range.first)
        lastDay.value = today.withMonthOfYear(range.last).dayOfMonth().withMaximumValue()
    }

    private val semestersNames = semesterRepository.namesLiveData

    val error: LiveData<Error?> = MediatorLiveData<Error?>().apply {
        val onChanged = Observer<Any?> {
            val name = name.value ?: ""
            val names = semestersNames.value ?: emptyList()
            val firstDay = firstDay.value
            val lastDay = lastDay.value
            value = when {
                name.isBlank() -> Error.EMPTY_NAME
                (semester == null) && names.contains(name) -> Error.SEMESTER_EXISTS
                (semester != null) && (name != semester.name) && names.contains(name) -> Error.SEMESTER_EXISTS
                (firstDay != null) && (lastDay != null) && (firstDay > lastDay) -> Error.WRONG_DATES
                else -> null
            }
        }

        addSource(name, onChanged)
        addSource(firstDay, onChanged)
        addSource(lastDay, onChanged)
        addSource(semestersNames, onChanged)
    }

    val isEditing get() = (semester != null)

    private val savedPrivate = MutableLiveData(false)
    val saved: LiveData<Boolean> get() = savedPrivate

    fun save() {
        check(error.value == null)
        viewModelScope.launch {
            semester?.id?.let { id ->
                semesterRepository.update(
                    id,
                    checkNotNull(name.value),
                    checkNotNull(firstDay.value),
                    checkNotNull(lastDay.value)
                )
            } ?: semesterRepository.insert(
                checkNotNull(name.value),
                checkNotNull(firstDay.value),
                checkNotNull(lastDay.value)
            )
            savedPrivate.value = true
        }
    }
}
