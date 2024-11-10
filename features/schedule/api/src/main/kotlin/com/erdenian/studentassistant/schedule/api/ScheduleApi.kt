package com.erdenian.studentassistant.schedule.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface ScheduleApi {
    fun NavGraphBuilder.composable(navController: NavHostController)
}
