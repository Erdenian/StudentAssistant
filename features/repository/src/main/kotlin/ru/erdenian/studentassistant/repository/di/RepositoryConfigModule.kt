package ru.erdenian.studentassistant.repository.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Named
import kotlinx.coroutines.CoroutineScope
import ru.erdenian.studentassistant.repository.RepositoryConfig

@Module
internal object RepositoryConfigModule {

    @Provides
    @Named("application")
    fun provideApplicationCoroutineScope(repositoryConfig: RepositoryConfig): CoroutineScope =
        repositoryConfig.applicationCoroutineScope

    @Provides
    @Named("settings")
    fun provideSettingsPreferences(
        application: Application,
        repositoryConfig: RepositoryConfig,
    ): SharedPreferences = application.getSharedPreferences(
        repositoryConfig.settingsPreferencesName,
        Context.MODE_PRIVATE,
    )
}
