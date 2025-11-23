package ru.erdenian.studentassistant.settings.di

import dagger.Component
import javax.inject.Singleton
import ru.erdenian.studentassistant.settings.SettingsDependencies
import ru.erdenian.studentassistant.settings.api.SettingsApi
import ru.erdenian.studentassistant.settings.ui.SettingsViewModel

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
