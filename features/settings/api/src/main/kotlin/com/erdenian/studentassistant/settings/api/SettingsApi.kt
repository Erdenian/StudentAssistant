package com.erdenian.studentassistant.settings.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface SettingsApi {
    fun NavGraphBuilder.composable(navController: NavHostController)
}
