package com.erdenian.studentassistant.repository.di

import android.app.Application
import android.content.Context
import com.erdenian.studentassistant.database.dao.HomeworkDao
import com.erdenian.studentassistant.database.dao.LessonDao
import com.erdenian.studentassistant.database.dao.SemesterDao
import com.erdenian.studentassistant.database.di.DatabaseModule
import com.erdenian.studentassistant.repository.HomeworkRepository
import com.erdenian.studentassistant.repository.LessonRepository
import com.erdenian.studentassistant.repository.SelectedSemesterRepository
import com.erdenian.studentassistant.repository.SemesterRepository
import com.erdenian.studentassistant.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.Reusable
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope

@Module(includes = [DatabaseModule::class])
class RepositoryModule(
    private val applicationCoroutineScope: CoroutineScope,
    private val settingsPreferencesName: String
) {

    @Singleton
    @Provides
    fun selectedSemesterRepository(
        semesterDao: SemesterDao
    ) = SelectedSemesterRepository(applicationCoroutineScope, semesterDao)

    @Reusable
    @Provides
    fun settingsRepository(application: Application) =
        SettingsRepository(application.getSharedPreferences(settingsPreferencesName, Context.MODE_PRIVATE))

    @Reusable
    @Provides
    fun semesterRepository(
        semesterDao: SemesterDao,
        selectedSemesterRepository: SelectedSemesterRepository
    ) = SemesterRepository(semesterDao, selectedSemesterRepository)

    @Reusable
    @Provides
    fun lessonRepository(
        lessonDao: LessonDao,
        selectedSemesterRepository: SelectedSemesterRepository,
        settingsRepository: SettingsRepository
    ) = LessonRepository(lessonDao, selectedSemesterRepository, settingsRepository)

    @Reusable
    @Provides
    fun homeworkRepository(
        homeworkDao: HomeworkDao,
        selectedSemesterRepository: SelectedSemesterRepository
    ) = HomeworkRepository(homeworkDao, selectedSemesterRepository)
}
