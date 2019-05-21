package ru.erdenian.studentassistant.ui.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.erdenian.studentassistant.repository.ScheduleRepository

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val scheduleRepository = ScheduleRepository(application)

    val allSemesters = scheduleRepository.getAllSemesters()
    val semestersNames = scheduleRepository.getSemestersNames()
}