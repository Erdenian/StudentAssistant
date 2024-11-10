package com.erdenian.studentassistant.settings

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.erdenian.studentassistant.settings.api.SettingsApi
import com.erdenian.studentassistant.settings.api.SettingsRoute
import com.erdenian.studentassistant.settings.di.SettingsComponentHolder
import com.erdenian.studentassistant.settings.ui.SettingsScreen
import javax.inject.Inject

public fun createSettingsApi(dependencies: SettingsDependencies): SettingsApi =
    SettingsComponentHolder.create(dependencies).api

internal class SettingsApiImpl @Inject constructor() : SettingsApi {
    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable<SettingsRoute.Settings> {
            val viewModel = viewModel { SettingsComponentHolder.instance.settingsViewModel }

            SettingsScreen(viewModel = viewModel)
        }
    }
}
