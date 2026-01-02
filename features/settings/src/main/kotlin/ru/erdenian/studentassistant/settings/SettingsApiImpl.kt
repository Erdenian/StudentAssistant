package ru.erdenian.studentassistant.settings

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import javax.inject.Inject
import javax.inject.Singleton
import ru.erdenian.studentassistant.settings.api.SettingsApi
import ru.erdenian.studentassistant.settings.api.SettingsRoute
import ru.erdenian.studentassistant.settings.di.SettingsComponentHolder
import ru.erdenian.studentassistant.settings.ui.SettingsScreen

public fun createSettingsApi(dependencies: SettingsDependencies): SettingsApi =
    SettingsComponentHolder.create(dependencies).api

@Singleton
internal class SettingsApiImpl @Inject constructor() : SettingsApi {
    override fun addToGraph(scope: EntryProviderScope<NavKey>) {
        scope.entry<SettingsRoute.Settings> { SettingsScreen() }
    }
}
