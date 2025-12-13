package ru.erdenian.studentassistant.homeworks.homeworks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.utils.Default

internal class HomeworksViewModel @Inject constructor(
    application: Application,
    repositoryApi: RepositoryApi,
) : AndroidViewModel(application) {

    private val selectedSemesterRepository = repositoryApi.selectedSemesterRepository
    private val semesterRepository = repositoryApi.semesterRepository
    private val homeworkRepository = repositoryApi.homeworkRepository

    enum class Operation {
        DELETING_HOMEWORK,
    }

    private val operationPrivate = MutableStateFlow<Operation?>(null)
    val operation = operationPrivate.asStateFlow()

    val selectedSemester = selectedSemesterRepository.selectedFlow
    val allSemesters = semesterRepository.allFlow
        .stateIn(viewModelScope, SharingStarted.Default, listOfNotNull(selectedSemester.value))

    fun selectSemester(semesterId: Long) = selectedSemesterRepository.selectSemester(semesterId)

    val overdue = homeworkRepository.overdueFlow.asStateFlowWithLoader()
    val actual = homeworkRepository.actualFlow.asStateFlowWithLoader()
    val past = homeworkRepository.pastFlow.asStateFlowWithLoader()

    fun deleteHomework(id: Long) {
        operationPrivate.value = Operation.DELETING_HOMEWORK
        viewModelScope.launch {
            homeworkRepository.delete(id)
            operationPrivate.value = null
        }
    }

    private fun <T> Flow<T>.asStateFlowWithLoader() = selectedSemester.flatMapLatest {
        flow {
            emit(null)
            emitAll(this@asStateFlowWithLoader)
        }
    }.stateIn(viewModelScope, SharingStarted.Default, null)
}
