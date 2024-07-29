package com.erdenian.studentassistant.settings.di

import com.erdenian.studentassistant.mediator.ApiProvider
import com.erdenian.studentassistant.settings.SettingsApi
import com.erdenian.studentassistant.settings.SettingsDependencies
import com.erdenian.studentassistant.settings.SettingsViewModel
import dagger.Component

@Component(
    modules = [SettingsApiModule::class],
    dependencies = [SettingsDependencies::class]
)
interface SettingsComponent : ApiProvider<SettingsApi> {

    @Component.Factory
    interface Factory {
        fun create(dependencies: SettingsDependencies): SettingsComponent
    }

    override val api: SettingsApi

    val settingsViewModel: SettingsViewModel
}
