package com.erdenian.studentassistant.homeworks

import android.app.Application
import com.erdenian.studentassistant.repository.HomeworkRepository
import com.erdenian.studentassistant.repository.LessonRepository
import com.erdenian.studentassistant.repository.SelectedSemesterRepository
import com.erdenian.studentassistant.repository.SemesterRepository

interface HomeworksDependencies {
    val application: Application
    val selectedSemesterRepository: SelectedSemesterRepository
    val semesterRepository: SemesterRepository
    val lessonRepository: LessonRepository
    val homeworkRepository: HomeworkRepository
}
