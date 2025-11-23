package ru.erdenian.studentassistant.repository.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.erdenian.studentassistant.repository.RepositoryConfig
import ru.erdenian.studentassistant.repository.database.dao.SemesterDao
import ru.erdenian.studentassistant.repository.impl.SelectedSemesterRepositoryImpl
import ru.erdenian.studentassistant.repository.impl.SettingsRepositoryImpl

@Module
internal class RepositoryConfigModule {

    @Provides
    fun selectedSemesterRepositoryImpl(
        repositoryConfig: RepositoryConfig,
        semesterDao: SemesterDao,
    ) = SelectedSemesterRepositoryImpl(repositoryConfig.applicationCoroutineScope, semesterDao)

    @Provides
    fun settingsRepositoryImpl(
        application: Application,
        repositoryConfig: RepositoryConfig,
    ) = SettingsRepositoryImpl(
        application.getSharedPreferences(repositoryConfig.settingsPreferencesName, Context.MODE_PRIVATE),
    )
}
