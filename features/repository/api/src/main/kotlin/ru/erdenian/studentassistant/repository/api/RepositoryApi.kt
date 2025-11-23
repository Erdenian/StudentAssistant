package ru.erdenian.studentassistant.repository.api

interface RepositoryApi {
    val selectedSemesterRepository: SelectedSemesterRepository
    val semesterRepository: SemesterRepository
    val lessonRepository: LessonRepository
    val homeworkRepository: HomeworkRepository
    val settingsRepository: SettingsRepository
}
