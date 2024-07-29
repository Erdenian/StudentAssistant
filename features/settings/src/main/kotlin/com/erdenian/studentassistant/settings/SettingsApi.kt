package com.erdenian.studentassistant.settings

import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.screenModule
import com.erdenian.studentassistant.mediator.ApiComponentHolder
import com.erdenian.studentassistant.settings.api.SettingsScreen
import com.erdenian.studentassistant.settings.di.DaggerSettingsComponent
import javax.inject.Inject

interface SettingsApi {
    val screenModule: ScreenRegistry.() -> Unit
}

internal class SettingsApiImpl @Inject constructor() : SettingsApi {
    override val screenModule = screenModule {
        register<SettingsScreen.Settings> { com.erdenian.studentassistant.settings.settings.SettingsScreen() }
    }
}

class SettingsApiComponentHolder(dependencies: SettingsDependencies) : ApiComponentHolder<SettingsApi>(
    DaggerSettingsComponent.factory().create(dependencies)
)
