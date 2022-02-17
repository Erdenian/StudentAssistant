package com.erdenian.studentassistant.schedule.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.erdenian.studentassistant.entity.immutableSortedSetOfNotNull
import com.erdenian.studentassistant.repository.LessonRepository
import com.erdenian.studentassistant.repository.SelectedSemesterRepository
import com.erdenian.studentassistant.repository.SemesterRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance

class ScheduleViewModel(application: Application) : AndroidViewModel(application), DIAware {

    override val di by closestDI()
    private val selectedSemesterRepository by instance<SelectedSemesterRepository>()
    private val semesterRepository by instance<SemesterRepository>()
    private val lessonRepository by instance<LessonRepository>()

    val selectedSemester = selectedSemesterRepository.selectedFlow
    val allSemesters = semesterRepository.allFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), immutableSortedSetOfNotNull(selectedSemester.value))

    fun selectSemester(semesterId: Long) = selectedSemesterRepository.selectSemester(semesterId)

    fun getLessons(day: LocalDate) = lessonRepository.getAllFlow(day)
}
