package com.erdenian.studentassistant.settings.di

import com.erdenian.studentassistant.settings.SettingsApiImpl
import com.erdenian.studentassistant.settings.api.SettingsApi
import dagger.Binds
import dagger.Module

@Module
internal interface SettingsApiModule {
    @Binds
    fun api(impl: SettingsApiImpl): SettingsApi
}
