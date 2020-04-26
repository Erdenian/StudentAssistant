package ru.erdenian.studentassistant.ui.semestereditor

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
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.repository.SemesterRepository

class SemesterEditorViewModel(
    application: Application
) : AndroidViewModel(application), KodeinAware {

    override val kodein by kodein()
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

    private var semester: Semester? = null

    fun init(semester: Semester?) {
        this.semester = semester?.also { s ->
            name.value = s.name
            firstDay.value = s.firstDay
            lastDay.value = s.lastDay
        }
    }

    val name = MutableLiveData("")
    val firstDay = MutableLiveData<LocalDate>()
    val lastDay = MutableLiveData<LocalDate>()

    init {
        val today = LocalDate.now().withDayOfMonth(1)
        val range = ranges.find { today.monthOfYear <= it.last } ?: ranges.first()
        firstDay.value = today.withMonthOfYear(range.first)
        lastDay.value = today.withMonthOfYear(range.last).dayOfMonth().withMaximumValue()
    }

    private val semestersNames = semesterRepository.namesLiveData

    val error: LiveData<Error?> = MediatorLiveData<Error?>().apply {
        val onChanged = Observer<Any?> {
            val semester = semester
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

    private val savedPrivate = MutableLiveData(false)
    val saved: LiveData<Boolean> get() = savedPrivate

    fun save() {
        check(error.value == null)
        viewModelScope.launch {
            semester?.let {
                semesterRepository.update(
                    it.id, checkNotNull(name.value), checkNotNull(firstDay.value), checkNotNull(lastDay.value)
                )
            } ?: semesterRepository.insert(
                checkNotNull(name.value), checkNotNull(firstDay.value), checkNotNull(lastDay.value)
            )
            savedPrivate.value = true
        }
    }
}
