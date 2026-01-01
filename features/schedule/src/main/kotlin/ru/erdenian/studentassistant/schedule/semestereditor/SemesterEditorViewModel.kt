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
import ru.erdenian.studentassistant.utils.Default

/**
 * ViewModel для экрана создания/редактирования расписания.
 *
 * @param semesterId ID расписания для редактирования. Если null, создается новое расписание.
 */
internal class SemesterEditorViewModel @AssistedInject constructor(
    application: Application,
    repositoryApi: RepositoryApi,
    @Assisted private val semesterId: Long?,
) : AndroidViewModel(application) {

    private val semesterRepository = repositoryApi.semesterRepository
    private val lessonRepository = repositoryApi.lessonRepository

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
    private var initialFirstDay: LocalDate? = null

    private val donePrivate = MutableStateFlow(false)
    val done = donePrivate.asStateFlow()

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
                    initialFirstDay = semester.firstDay
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

    /**
     * Поток ошибки валидации.
     */
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
    }.stateIn(viewModelScope, SharingStarted.Default, null)

    val isEditing = (semesterId != null)

    private val showWeekShiftDialogPrivate = MutableStateFlow(false)

    /** Поток управления видимостью диалога предупреждения о сдвиге недель. */
    val showWeekShiftDialog = showWeekShiftDialogPrivate.asStateFlow()

    /**
     * Сохраняет изменения или создает новое расписание.
     *
     * Если дата начала расписания изменилась и в нем есть нерегулярные занятия,
     * может потребоваться подтверждение пользователя (через [showWeekShiftDialog]).
     *
     * @param confirmWeekShift подтверждает ли пользователь сохранение, несмотря на сдвиг недель.
     */
    fun save(confirmWeekShift: Boolean = false) {
        check(error.value == null)
        operationPrivate.value = Operation.SAVING
        viewModelScope.launch {
            if (semesterId != null) {
                if (!confirmWeekShift &&
                    (initialFirstDay?.monday() != firstDay.value.monday()) &&
                    lessonRepository.hasNonRecurringLessons(semesterId)
                ) {
                    showWeekShiftDialogPrivate.value = true
                    operationPrivate.value = null
                    return@launch
                }

                semesterRepository.update(
                    id = semesterId, name = name.value, firstDay = firstDay.value, lastDay = lastDay.value,
                )
            } else {
                semesterRepository.insert(name = name.value, firstDay = firstDay.value, lastDay = lastDay.value)
            }
            donePrivate.value = true
        }
    }

    fun dismissWeekShiftDialog() {
        showWeekShiftDialogPrivate.value = false
    }

    private fun LocalDate.monday(): LocalDate = this.minusDays(this.dayOfWeek.value.toLong() - 1L)
}
