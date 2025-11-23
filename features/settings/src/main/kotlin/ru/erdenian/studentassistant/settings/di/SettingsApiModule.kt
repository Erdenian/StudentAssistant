package ru.erdenian.studentassistant.settings.di

import dagger.Binds
import dagger.Module
import ru.erdenian.studentassistant.settings.SettingsApiImpl
import ru.erdenian.studentassistant.settings.api.SettingsApi

@Module
internal interface SettingsApiModule {
    @Binds
    fun api(impl: SettingsApiImpl): SettingsApi
}
