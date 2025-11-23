package ru.erdenian.studentassistant.schedule.semestereditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.Month
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.erdenian.studentassistant.repository.api.RepositoryApi

internal class SemesterEditorViewModel @AssistedInject constructor(
    application: Application,
    repositoryApi: RepositoryApi,
    @Assisted private val semesterId: Long?,
) : AndroidViewModel(application) {

    private val semesterRepository = repositoryApi.semesterRepository

    @AssistedFactory
    interface Factory {
        fun get(semesterId: Long?): SemesterEditorViewModel
    }

    enum class Error {
        EMPTY_NAME,
        SEMESTER_EXISTS,
        WRONG_DATES,
    }

    enum class Operation {
        LOADING,
        SAVING,
    }

    private val isSemesterLoaded = MutableStateFlow(false)
    private val areNamesLoaded = MutableStateFlow(false)
    private val operationPrivate = MutableStateFlow<Operation?>(Operation.LOADING)
    val operation = operationPrivate.asStateFlow()

    private val semestersRanges = listOf(
        Month.FEBRUARY..Month.MAY,
        Month.SEPTEMBER..Month.DECEMBER,
    )

    val name = MutableStateFlow("")
    val firstDay: MutableStateFlow<LocalDate>
    val lastDay: MutableStateFlow<LocalDate>

    private var initialName: String? = null

    init {
        val today = LocalDate.now().withDayOfMonth(1)
        val range = semestersRanges.find { today.month <= it.endInclusive } ?: semestersRanges.first()
        firstDay = MutableStateFlow(today.withMonth(range.start.value))
        lastDay = MutableStateFlow(
            today.withMonth(range.endInclusive.value).withDayOfMonth(range.endInclusive.maxLength()),
        )

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
                isSemesterLoaded.value = true
            }
        } else {
            isSemesterLoaded.value = true
        }

        viewModelScope.launch {
            combine(isSemesterLoaded, areNamesLoaded) { semester, names -> semester && names }.filter { it }.first()
            if (operationPrivate.value == Operation.LOADING) operationPrivate.value = null
        }
    }

    val error = combine(
        flow = name,
        flow2 = firstDay,
        flow3 = lastDay,
        flow4 = semesterRepository.namesFlow.onEach { areNamesLoaded.value = true },
    ) { name, firstDay, lastDay, semestersNames ->
        when {
            name.isBlank() -> Error.EMPTY_NAME
            (semesterId == null) && semestersNames.contains(name) -> Error.SEMESTER_EXISTS
            (semesterId != null) && (name != initialName) && semestersNames.contains(name) -> Error.SEMESTER_EXISTS
            (firstDay >= lastDay) -> Error.WRONG_DATES
            else -> null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val isEditing = (semesterId != null)

    private val donePrivate = MutableStateFlow(false)
    val done = donePrivate.asStateFlow()

    fun save() {
        check(error.value == null)
        operationPrivate.value = Operation.SAVING
        viewModelScope.launch {
            semesterId?.let { id ->
                semesterRepository.update(
                    id = id, name = name.value, firstDay = firstDay.value, lastDay = lastDay.value,
                )
            } ?: semesterRepository.insert(name = name.value, firstDay = firstDay.value, lastDay = lastDay.value)
            donePrivate.value = true
            operationPrivate.value = null
        }
    }
}
