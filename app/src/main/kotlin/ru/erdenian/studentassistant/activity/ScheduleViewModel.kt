package ru.erdenian.studentassistant.activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.erdenian.studentassistant.repository.ScheduleRepository

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val scheduleRepository =
        ScheduleRepository(application)

    val allSemesters get() = scheduleRepository.allSemesters
    val semestersNames get() = scheduleRepository.getSemestersNames()
}