package com.erdenian.studentassistant.repository.di

import android.app.Application
import android.content.Context
import com.erdenian.studentassistant.repository.RepositoryConfig
import com.erdenian.studentassistant.repository.database.dao.SemesterDao
import com.erdenian.studentassistant.repository.impl.SelectedSemesterRepositoryImpl
import com.erdenian.studentassistant.repository.impl.SettingsRepositoryImpl
import dagger.Module
import dagger.Provides

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
