package com.erdenian.studentassistant.settings

import androidx.navigation.NavGraphBuilder
import com.erdenian.studentassistant.navigation.composableAnimated
import com.erdenian.studentassistant.settings.api.SettingsApi
import com.erdenian.studentassistant.settings.api.SettingsRoute
import com.erdenian.studentassistant.settings.di.SettingsComponentHolder
import com.erdenian.studentassistant.settings.ui.SettingsScreen
import javax.inject.Inject

public fun createSettingsApi(dependencies: SettingsDependencies): SettingsApi =
    SettingsComponentHolder.create(dependencies).api

internal class SettingsApiImpl @Inject constructor() : SettingsApi {
    override fun addToGraph(builder: NavGraphBuilder) {
        builder.composableAnimated<SettingsRoute.Settings> { SettingsScreen() }
    }
}
