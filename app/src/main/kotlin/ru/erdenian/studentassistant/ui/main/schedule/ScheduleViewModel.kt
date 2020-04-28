package ru.erdenian.studentassistant.ui.main.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import org.joda.time.LocalDate
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import ru.erdenian.studentassistant.repository.LessonRepository
import ru.erdenian.studentassistant.repository.SelectedSemesterRepository

class ScheduleViewModel(application: Application) : AndroidViewModel(application), KodeinAware {

    override val kodein by kodein()
    private val selectedSemesterRepository by instance<SelectedSemesterRepository>()
    private val lessonRepository by instance<LessonRepository>()

    val selectedSemester = selectedSemesterRepository.selectedLiveData

    fun getLessons(day: LocalDate) = lessonRepository.getAllLiveData(day)
}
