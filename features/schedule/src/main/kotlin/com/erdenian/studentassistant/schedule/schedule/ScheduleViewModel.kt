package com.erdenian.studentassistant.schedule.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.erdenian.studentassistant.entity.immutableSortedSetOfNotNull
import com.erdenian.studentassistant.repository.LessonRepository
import com.erdenian.studentassistant.repository.SelectedSemesterRepository
import com.erdenian.studentassistant.repository.SemesterRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

internal class ScheduleViewModel @Inject constructor(
    application: Application,
    private val selectedSemesterRepository: SelectedSemesterRepository,
    semesterRepository: SemesterRepository,
    private val lessonRepository: LessonRepository
) : AndroidViewModel(application) {

    val selectedSemester = selectedSemesterRepository.selectedFlow
    val allSemesters = semesterRepository.allFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), immutableSortedSetOfNotNull(selectedSemester.value))

    fun selectSemester(semesterId: Long) = selectedSemesterRepository.selectSemester(semesterId)

    fun getLessons(day: LocalDate) = lessonRepository.getAllFlow(day)
}
