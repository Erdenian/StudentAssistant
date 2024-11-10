package com.erdenian.studentassistant.homeworks.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface HomeworksApi {
    fun NavGraphBuilder.composable(navController: NavHostController)
}
