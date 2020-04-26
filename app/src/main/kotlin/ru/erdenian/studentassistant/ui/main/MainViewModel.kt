package ru.erdenian.studentassistant.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.repository.HomeworkRepository
import ru.erdenian.studentassistant.repository.LessonRepository
import ru.erdenian.studentassistant.repository.SelectedSemesterRepository
import ru.erdenian.studentassistant.repository.SemesterRepository

class MainViewModel(application: Application) : AndroidViewModel(application), KodeinAware {

    override val kodein by kodein()
    private val selectedSemesterRepository by instance<SelectedSemesterRepository>()
    private val semesterRepository by instance<SemesterRepository>()
    private val lessonRepository by instance<LessonRepository>()
    private val homeworkRepository by instance<HomeworkRepository>()

    val allSemesters = semesterRepository.allLiveData

    val selectedSemester = selectedSemesterRepository.selectedLiveData
    fun selectSemester(semester: Semester) = selectedSemesterRepository.selectSemester(semester)

    val hasLessons = lessonRepository.hasLessonsLiveData

    fun getLessons(day: LocalDate) = lessonRepository.getAllLiveData(day)

    val actualHomeworks = homeworkRepository.actualLiveData

    val pastHomeworks = homeworkRepository.pastLiveData

    fun deleteHomework(id: Long) {
        viewModelScope.launch { homeworkRepository.delete(id) }
    }
}
