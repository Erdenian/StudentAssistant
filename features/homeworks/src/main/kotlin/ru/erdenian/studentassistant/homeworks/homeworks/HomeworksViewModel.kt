package ru.erdenian.studentassistant.homeworks.homeworks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.entity.ImmutableSortedSet
import ru.erdenian.studentassistant.entity.immutableSortedSetOfNotNull
import ru.erdenian.studentassistant.entity.toImmutableSortedSet
import ru.erdenian.studentassistant.repository.HomeworkRepository
import ru.erdenian.studentassistant.repository.SelectedSemesterRepository
import ru.erdenian.studentassistant.repository.SemesterRepository

class HomeworksViewModel(application: Application) : AndroidViewModel(application), DIAware {

    override val di by closestDI()
    private val selectedSemesterRepository by instance<SelectedSemesterRepository>()
    private val semesterRepository by instance<SemesterRepository>()
    private val homeworkRepository by instance<HomeworkRepository>()

    enum class Operation {
        DELETING_HOMEWORK
    }

    private val operationPrivate = MutableStateFlow<Operation?>(null)
    val operation = operationPrivate.asStateFlow()

    val selectedSemester = selectedSemesterRepository.selectedFlow
    val allSemesters = semesterRepository.allFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), immutableSortedSetOfNotNull(selectedSemester.value))

    fun selectSemester(semesterId: Long) = selectedSemesterRepository.selectSemester(semesterId)

    private val deletedHomeworksIds = MutableStateFlow(emptySet<Long>())
    private fun Flow<ImmutableSortedSet<Homework>>.stateWithDeleted() =
        combine(
            this.onEach { deletedHomeworksIds.value = emptySet() },
            deletedHomeworksIds
        ) { homeworks, deletedIds ->
            if (deletedIds.isEmpty()) homeworks
            else homeworks.asSequence().filter { it.id !in deletedIds }.toImmutableSortedSet()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val overdue = homeworkRepository.overdueFlow.stateWithDeleted()
    val actual = homeworkRepository.actualFlow.stateWithDeleted()
    val past = homeworkRepository.pastFlow.stateWithDeleted()

    fun deleteHomework(id: Long) {
        operationPrivate.value = Operation.DELETING_HOMEWORK
        viewModelScope.launch {
            homeworkRepository.delete(id)
            deletedHomeworksIds.value += id
            operationPrivate.value = null
        }
    }
}
