package com.erdenian.studentassistant.homeworks

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.erdenian.studentassistant.homeworks.api.HomeworksApi
import com.erdenian.studentassistant.homeworks.api.HomeworksRoute
import com.erdenian.studentassistant.homeworks.di.HomeworksComponentHolder
import com.erdenian.studentassistant.homeworks.homeworkeditor.HomeworkEditorScreen
import com.erdenian.studentassistant.homeworks.homeworks.HomeworksScreen
import com.erdenian.studentassistant.schedule.api.ScheduleRoute
import javax.inject.Inject

public fun createHomeworksApi(dependencies: HomeworksDependencies): HomeworksApi =
    HomeworksComponentHolder.create(dependencies).api

internal class HomeworksApiImpl @Inject constructor() : HomeworksApi {
    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable<HomeworksRoute.Homeworks> {
            val viewModel = viewModel { HomeworksComponentHolder.instance.homeworksViewModel }

            HomeworksScreen(
                viewModel = viewModel,
                navigateToCreateHomework = { navController.navigate(HomeworksRoute.HomeworkEditor(it)) },
                navigateToEditHomework = { semesterId: Long, homeworkId: Long ->
                    navController.navigate(
                        HomeworksRoute.HomeworkEditor(semesterId = semesterId, homeworkId = homeworkId),
                    )
                },
            )
        }

        composable<HomeworksRoute.HomeworkEditor> { backStackEntry ->
            val route = backStackEntry.toRoute<HomeworksRoute.HomeworkEditor>()
            val semesterId = route.semesterId
            val subjectName = route.subjectName
            val homeworkId = route.homeworkId

            val viewModel = viewModel {
                val factory = HomeworksComponentHolder.instance.homeworkEditorViewModelFactory
                when {
                    (homeworkId != null) -> factory.get(semesterId, homeworkId)
                    (subjectName != null) -> factory.get(semesterId, subjectName)
                    else -> factory.get(semesterId)
                }
            }

            HomeworkEditorScreen(
                viewModel = viewModel,
                navigateBack = { navController.popBackStack() },
                navigateToCreateLesson = { semesterId, subjectName ->
                    navController.navigate(
                        ScheduleRoute.LessonEditor(semesterId = semesterId, subjectName = subjectName),
                    )
                },
            )
        }
    }
}
