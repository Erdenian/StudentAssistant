package com.erdenian.studentassistant.mediator.mediators

import com.erdenian.studentassistant.MainApplication
import com.erdenian.studentassistant.mediator.Mediator
import com.erdenian.studentassistant.mediator.componentRegistry
import com.erdenian.studentassistant.settings.SettingsApi
import com.erdenian.studentassistant.settings.SettingsApiHolder
import com.erdenian.studentassistant.settings.SettingsDependencies

object SettingsMediator : Mediator<SettingsApi>() {

    override val apiHolder by componentRegistry<SettingsApi, SettingsApiHolder> {
        SettingsApiHolder(object : SettingsDependencies {
            override val application get() = MainApplication.instance
            override val settingsRepository get() = RepositoryMediator.api.settingsRepository
        })
    }
}
