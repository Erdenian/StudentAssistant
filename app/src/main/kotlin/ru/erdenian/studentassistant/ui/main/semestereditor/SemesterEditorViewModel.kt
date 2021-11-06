package ru.erdenian.studentassistant.ui.main.semestereditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import ru.erdenian.studentassistant.repository.SemesterRepository

class SemesterEditorViewModel(
    application: Application,
    private val semesterId: Long?
) : AndroidViewModel(application), DIAware {

    override val di by closestDI()
    private val semesterRepository by instance<SemesterRepository>()

    enum class Error {
        EMPTY_NAME,
        SEMESTER_EXISTS,
        WRONG_DATES
    }

    private val semestersRanges = listOf(
        DateTimeConstants.FEBRUARY..DateTimeConstants.MAY,
        DateTimeConstants.SEPTEMBER..DateTimeConstants.DECEMBER
    )

    val name = MutableStateFlow("")
    val firstDay: MutableStateFlow<LocalDate>
    val lastDay: MutableStateFlow<LocalDate>

    private var initialName: String? = null

    init {
        val today = LocalDate.now().withDayOfMonth(1)
        val range = semestersRanges.find { today.monthOfYear <= it.last } ?: semestersRanges.first()
        firstDay = MutableStateFlow(today.withMonthOfYear(range.first))
        lastDay = MutableStateFlow(today.withMonthOfYear(range.last).dayOfMonth().withMaximumValue())

        if (semesterId != null) {
            viewModelScope.launch {
                val semester = semesterRepository.get(semesterId)
                if (semester != null) {
                    name.value = semester.name
                    initialName = semester.name

                    firstDay.value = semester.firstDay
                    lastDay.value = semester.lastDay
                } else {
                    donePrivate.value = true
                }
            }
        }
    }

    private val semestersNames = semesterRepository.namesFlow

    val error = combine(name, firstDay, lastDay, semestersNames) { name, firstDay, lastDay, semestersNames ->
        when {
            name.isBlank() -> Error.EMPTY_NAME
            (semesterId == null) && semestersNames.contains(name) -> Error.SEMESTER_EXISTS
            (semesterId != null) && (name != initialName) && semestersNames.contains(name) -> Error.SEMESTER_EXISTS
            (firstDay >= lastDay) -> Error.WRONG_DATES
            else -> null
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val isEditing = (semesterId != null)

    private val donePrivate = MutableStateFlow(false)
    val done = donePrivate.asStateFlow()

    fun save() {
        check(error.value == null)
        viewModelScope.launch {
            semesterId?.let { id ->
                semesterRepository.update(id, name.value, firstDay.value, lastDay.value)
            } ?: semesterRepository.insert(name.value, firstDay.value, lastDay.value)
            donePrivate.value = true
        }
    }
}
