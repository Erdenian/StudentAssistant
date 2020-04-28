package ru.erdenian.studentassistant.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.repository.SelectedSemesterRepository
import ru.erdenian.studentassistant.repository.SemesterRepository

class MainViewModel(application: Application) : AndroidViewModel(application), KodeinAware {

    override val kodein by kodein()
    private val selectedSemesterRepository by instance<SelectedSemesterRepository>()
    private val semesterRepository by instance<SemesterRepository>()

    val allSemesters = semesterRepository.allLiveData

    val selectedSemester = selectedSemesterRepository.selectedLiveData
    fun selectSemester(semester: Semester) = selectedSemesterRepository.selectSemester(semester)
}
