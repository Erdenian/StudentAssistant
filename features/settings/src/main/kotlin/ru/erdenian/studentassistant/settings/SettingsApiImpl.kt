package ru.erdenian.studentassistant.settings

import androidx.navigation.NavGraphBuilder
import javax.inject.Inject
import ru.erdenian.studentassistant.navigation.composableAnimated
import ru.erdenian.studentassistant.settings.api.SettingsApi
import ru.erdenian.studentassistant.settings.api.SettingsRoute
import ru.erdenian.studentassistant.settings.di.SettingsComponentHolder
import ru.erdenian.studentassistant.settings.ui.SettingsScreen

public fun createSettingsApi(dependencies: SettingsDependencies): SettingsApi =
    SettingsComponentHolder.create(dependencies).api

internal class SettingsApiImpl @Inject constructor() : SettingsApi {
    override fun addToGraph(builder: NavGraphBuilder) {
        builder.composableAnimated<SettingsRoute.Settings> { SettingsScreen() }
    }
}
