package ru.erdenian.studentassistant.di.features

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import ru.erdenian.studentassistant.di.MainComponent
import ru.erdenian.studentassistant.navigation.NavGraphContributor
import ru.erdenian.studentassistant.settings.SettingsDependencies
import ru.erdenian.studentassistant.settings.api.SettingsApi
import ru.erdenian.studentassistant.settings.createSettingsApi

@Module
internal interface SettingsModule {

    @Binds
    fun dependencies(dependencies: MainComponent): SettingsDependencies

    companion object {

        @Provides
        fun api(dependencies: SettingsDependencies) = createSettingsApi(dependencies)

        @Provides
        @IntoSet
        fun navGraphContributor(api: SettingsApi): NavGraphContributor = api.navGraphContributor
    }
}
