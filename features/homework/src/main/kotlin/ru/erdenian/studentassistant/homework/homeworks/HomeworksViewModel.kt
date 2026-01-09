package ru.erdenian.studentassistant.homework.homeworks

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
import ru.erdenian.studentassistant.analytics.api.AnalyticsApi
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.repository.api.entity.Homework
import ru.erdenian.studentassistant.utils.Default

/**
 * ViewModel для главного экрана домашних заданий.
 */
internal class HomeworksViewModel @Inject constructor(
    application: Application,
    repositoryApi: RepositoryApi,
    analyticsApi: AnalyticsApi,
) : AndroidViewModel(application) {

    private val selectedSemesterRepository = repositoryApi.selectedSemesterRepository
    private val semesterRepository = repositoryApi.semesterRepository
    private val homeworkRepository = repositoryApi.homeworkRepository
    private val analytics = analyticsApi.analytics

    enum class Operation {
        DELETING_HOMEWORK,
    }

    private val operationPrivate = MutableStateFlow<Operation?>(null)
    val operation = operationPrivate.asStateFlow()

    val selectedSemester = selectedSemesterRepository.selectedFlow
    val allSemesters = semesterRepository.allFlow
        .stateIn(viewModelScope, SharingStarted.Default, listOfNotNull(selectedSemester.value))

    /**
     * Выбирает расписание для отображения заданий.
     *
     * @param semesterId идентификатор расписания.
     */
    fun selectSemester(semesterId: Long) {
        selectedSemesterRepository.selectSemester(semesterId)
        analytics.logEvent("semester_switched")
    }

    /**
     * Поток просроченных домашних заданий.
     */
    val overdue = homeworkRepository.overdueFlow.asStateFlowWithLoader()

    /**
     * Поток актуальных домашних заданий.
     */
    val actual = homeworkRepository.actualFlow.asStateFlowWithLoader()

    /**
     * Поток выполненных или прошедших домашних заданий.
     */
    val past = homeworkRepository.pastFlow.asStateFlowWithLoader()

    /**
     * Отправляет событие аналитики при нажатии на кнопку добавления задания.
     */
    fun logAddHomeworkClicked() {
        analytics.logEvent("homework_add_clicked")
    }

    /**
     * Отправляет событие аналитики при нажатии на конкретное задание.
     */
    fun logHomeworkClicked(homework: Homework) {
        analytics.logEvent(
            name = "homework_clicked",
            params = mapOf("subject_name" to homework.subjectName),
        )
    }

    /**
     * Удаляет домашнее задание.
     */
    fun deleteHomework(id: Long) {
        operationPrivate.value = Operation.DELETING_HOMEWORK
        viewModelScope.launch {
            homeworkRepository.delete(id)
            analytics.logEvent("homework_deleted")
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
