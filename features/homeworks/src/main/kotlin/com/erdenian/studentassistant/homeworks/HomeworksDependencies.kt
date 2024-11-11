package com.erdenian.studentassistant.homeworks

import android.app.Application
import com.erdenian.studentassistant.repository.HomeworkRepository
import com.erdenian.studentassistant.repository.LessonRepository
import com.erdenian.studentassistant.repository.SelectedSemesterRepository
import com.erdenian.studentassistant.repository.SemesterRepository

public interface HomeworksDependencies {
    public val application: Application
    public val selectedSemesterRepository: SelectedSemesterRepository
    public val semesterRepository: SemesterRepository
    public val homeworkRepository: HomeworkRepository
    public val lessonRepository: LessonRepository
}
