package com.erdenian.studentassistant.mediator.mediators

import com.erdenian.studentassistant.MainApplication
import com.erdenian.studentassistant.mediator.Mediator
import com.erdenian.studentassistant.mediator.componentRegistry
import com.erdenian.studentassistant.settings.SettingsApi
import com.erdenian.studentassistant.settings.SettingsApiComponentHolder
import com.erdenian.studentassistant.settings.SettingsDependencies

object SettingsMediator : Mediator<SettingsApi>() {

    override val apiComponentHolder by componentRegistry<SettingsApi, SettingsApiComponentHolder> {
        SettingsApiComponentHolder(object : SettingsDependencies {
            override val application get() = MainApplication.instance
            override val settingsRepository get() = RepositoryMediator.api.settingsRepository
        })
    }
}
