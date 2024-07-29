package com.erdenian.studentassistant.settings.di

import com.erdenian.studentassistant.settings.SettingsApi
import com.erdenian.studentassistant.settings.SettingsApiImpl
import dagger.Binds
import dagger.Module

@Module
internal interface SettingsApiModule {
    @Binds
    fun homeworksApi(impl: SettingsApiImpl): SettingsApi
}
