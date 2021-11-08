package ru.erdenian.studentassistant.homeworks.homeworks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.entity.immutableSortedSetOf
import ru.erdenian.studentassistant.entity.immutableSortedSetOfNotNull
import ru.erdenian.studentassistant.repository.HomeworkRepository
import ru.erdenian.studentassistant.repository.SelectedSemesterRepository
import ru.erdenian.studentassistant.repository.SemesterRepository

class HomeworksViewModel(application: Application) : AndroidViewModel(application), DIAware {

    override val di by closestDI()
    private val selectedSemesterRepository by instance<SelectedSemesterRepository>()
    private val semesterRepository by instance<SemesterRepository>()
    private val homeworkRepository by instance<HomeworkRepository>()

    enum class State {
        NO_SCHEDULE,
        NO_HOMEWORKS,
        HAS_HOMEWORKS
    }

    val selectedSemester = selectedSemesterRepository.selectedFlow
    val allSemesters = semesterRepository.allFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, immutableSortedSetOfNotNull(selectedSemester.value))

    fun selectSemester(semesterId: Long) = selectedSemesterRepository.selectSemester(semesterId)

    val overdue = homeworkRepository.overdueFlow.stateIn(viewModelScope, SharingStarted.Lazily, immutableSortedSetOf())
    val actual = homeworkRepository.actualFlow.stateIn(viewModelScope, SharingStarted.Lazily, immutableSortedSetOf())
    val past = homeworkRepository.pastFlow.stateIn(viewModelScope, SharingStarted.Lazily, immutableSortedSetOf())
    private val all = combine(overdue, actual, past) { overdue, actual, past -> overdue + actual + past }

    val state = combine(selectedSemester, all) { selectedSemester, all -> getState(selectedSemester, all) }
        .stateIn(viewModelScope, SharingStarted.Lazily, getState(selectedSemester.value, immutableSortedSetOf()))

    private fun getState(selectedSemester: Semester?, homeworks: Collection<Homework>) = when {
        (selectedSemester == null) -> State.NO_SCHEDULE
        homeworks.isEmpty() -> State.NO_HOMEWORKS
        else -> State.HAS_HOMEWORKS
    }

    fun deleteHomework(id: Long) {
        viewModelScope.launch { homeworkRepository.delete(id) }
    }
}
