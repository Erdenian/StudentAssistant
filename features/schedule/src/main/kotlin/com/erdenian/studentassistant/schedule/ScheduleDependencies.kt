package com.erdenian.studentassistant.schedule

import android.app.Application
import com.erdenian.studentassistant.repository.HomeworkRepository
import com.erdenian.studentassistant.repository.LessonRepository
import com.erdenian.studentassistant.repository.SelectedSemesterRepository
import com.erdenian.studentassistant.repository.SemesterRepository
import com.erdenian.studentassistant.repository.SettingsRepository

public interface ScheduleDependencies {
    public val application: Application
    public val selectedSemesterRepository: SelectedSemesterRepository
    public val semesterRepository: SemesterRepository
    public val lessonRepository: LessonRepository
    public val homeworkRepository: HomeworkRepository
    public val settingsRepository: SettingsRepository
}
