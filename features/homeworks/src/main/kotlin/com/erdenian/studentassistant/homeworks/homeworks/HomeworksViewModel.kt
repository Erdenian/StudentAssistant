package com.erdenian.studentassistant.homeworks.homeworks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.erdenian.studentassistant.entity.Homework
import com.erdenian.studentassistant.entity.ImmutableSortedSet
import com.erdenian.studentassistant.entity.immutableSortedSetOfNotNull
import com.erdenian.studentassistant.entity.toImmutableSortedSet
import com.erdenian.studentassistant.repository.HomeworkRepository
import com.erdenian.studentassistant.repository.SelectedSemesterRepository
import com.erdenian.studentassistant.repository.SemesterRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeworksViewModel @Inject constructor(
    application: Application,
    private val selectedSemesterRepository: SelectedSemesterRepository,
    semesterRepository: SemesterRepository,
    private val homeworkRepository: HomeworkRepository,
) : AndroidViewModel(application) {

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
            deletedHomeworksIds,
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
