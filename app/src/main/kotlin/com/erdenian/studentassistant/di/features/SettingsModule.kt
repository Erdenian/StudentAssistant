package com.erdenian.studentassistant.di.features

import com.erdenian.studentassistant.di.MainComponent
import com.erdenian.studentassistant.settings.SettingsDependencies
import com.erdenian.studentassistant.settings.createSettingsApi
import dagger.Module
import dagger.Provides

@Module
internal class SettingsModule {

    @Provides
    fun dependencies(dependencies: MainComponent): SettingsDependencies = dependencies

    @Provides
    fun api(dependencies: SettingsDependencies) = createSettingsApi(dependencies)
}
