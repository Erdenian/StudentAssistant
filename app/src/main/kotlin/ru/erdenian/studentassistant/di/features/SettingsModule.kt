package ru.erdenian.studentassistant.di.features

import dagger.Module
import dagger.Provides
import ru.erdenian.studentassistant.di.MainComponent
import ru.erdenian.studentassistant.settings.SettingsDependencies
import ru.erdenian.studentassistant.settings.createSettingsApi

@Module
internal class SettingsModule {

    @Provides
    fun dependencies(dependencies: MainComponent): SettingsDependencies = dependencies

    @Provides
    fun api(dependencies: SettingsDependencies) = createSettingsApi(dependencies)
}
