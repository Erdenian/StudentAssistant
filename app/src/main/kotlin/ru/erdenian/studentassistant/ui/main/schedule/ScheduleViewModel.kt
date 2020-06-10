package ru.erdenian.studentassistant.ui.main.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import org.joda.time.LocalDate
import org.kodein.di.DIAware
import org.kodein.di.android.x.di
import org.kodein.di.instance
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.repository.LessonRepository
import ru.erdenian.studentassistant.repository.SelectedSemesterRepository
import ru.erdenian.studentassistant.repository.SemesterRepository

class ScheduleViewModel(application: Application) : AndroidViewModel(application), DIAware {

    override val di by di()
    private val selectedSemesterRepository by instance<SelectedSemesterRepository>()
    private val semesterRepository by instance<SemesterRepository>()
    private val lessonRepository by instance<LessonRepository>()

    val selectedSemester = selectedSemesterRepository.selectedLiveData
    val allSemesters = semesterRepository.allLiveData

    fun selectSemester(semester: Semester) = selectedSemesterRepository.selectSemester(semester)

    fun getLessons(day: LocalDate) = lessonRepository.getAllLiveData(day)
}
