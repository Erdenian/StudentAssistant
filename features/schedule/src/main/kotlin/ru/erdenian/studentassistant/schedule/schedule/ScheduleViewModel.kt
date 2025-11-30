package ru.erdenian.studentassistant.schedule.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import ru.erdenian.studentassistant.repository.api.RepositoryApi
import ru.erdenian.studentassistant.utils.Default

internal class ScheduleViewModel @Inject constructor(
    application: Application,
    repositoryApi: RepositoryApi,
) : AndroidViewModel(application) {

    private val selectedSemesterRepository = repositoryApi.selectedSemesterRepository
    private val semesterRepository = repositoryApi.semesterRepository
    private val lessonRepository = repositoryApi.lessonRepository

    val selectedSemester = selectedSemesterRepository.selectedFlow
    val allSemesters = semesterRepository.allFlow
        .stateIn(viewModelScope, SharingStarted.Default, listOfNotNull(selectedSemester.value))

    fun selectSemester(semesterId: Long) = selectedSemesterRepository.selectSemester(semesterId)

    fun getLessons(day: LocalDate) = lessonRepository.getAllFlow(day)
}
