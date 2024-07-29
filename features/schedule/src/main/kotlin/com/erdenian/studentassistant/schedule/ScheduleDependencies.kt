package com.erdenian.studentassistant.schedule

import android.app.Application
import com.erdenian.studentassistant.repository.HomeworkRepository
import com.erdenian.studentassistant.repository.LessonRepository
import com.erdenian.studentassistant.repository.SelectedSemesterRepository
import com.erdenian.studentassistant.repository.SemesterRepository
import com.erdenian.studentassistant.repository.SettingsRepository

interface ScheduleDependencies {
    val application: Application
    val selectedSemesterRepository: SelectedSemesterRepository
    val semesterRepository: SemesterRepository
    val lessonRepository: LessonRepository
    val homeworkRepository: HomeworkRepository
    val settingsRepository: SettingsRepository
}
