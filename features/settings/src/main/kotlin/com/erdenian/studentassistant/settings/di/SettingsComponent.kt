package com.erdenian.studentassistant.settings.di

import com.erdenian.studentassistant.settings.SettingsDependencies
import com.erdenian.studentassistant.settings.api.SettingsApi
import com.erdenian.studentassistant.settings.ui.SettingsViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [SettingsApiModule::class],
    dependencies = [SettingsDependencies::class],
)
internal interface SettingsComponent {

    @Component.Factory
    interface Factory {
        fun create(dependencies: SettingsDependencies): SettingsComponent
    }

    val api: SettingsApi

    val settingsViewModel: SettingsViewModel
}
