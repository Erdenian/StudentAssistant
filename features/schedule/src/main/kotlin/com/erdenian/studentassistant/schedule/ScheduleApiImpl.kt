package com.erdenian.studentassistant.schedule

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.erdenian.studentassistant.homeworks.api.HomeworksRoute
import com.erdenian.studentassistant.schedule.api.ScheduleApi
import com.erdenian.studentassistant.schedule.api.ScheduleRoute
import com.erdenian.studentassistant.schedule.di.ScheduleComponentHolder
import com.erdenian.studentassistant.schedule.lessoneditor.LessonEditorScreen
import com.erdenian.studentassistant.schedule.lessoninformation.LessonInformationScreen
import com.erdenian.studentassistant.schedule.schedule.ScheduleScreen
import com.erdenian.studentassistant.schedule.scheduleeditor.ScheduleEditorScreen
import com.erdenian.studentassistant.schedule.semestereditor.SemesterEditorScreen
import javax.inject.Inject

public fun createScheduleApi(dependencies: ScheduleDependencies): ScheduleApi =
    ScheduleComponentHolder.create(dependencies).api

internal class ScheduleApiImpl @Inject constructor() : ScheduleApi {
    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable<ScheduleRoute.Schedule> {
            val viewModel = viewModel { ScheduleComponentHolder.instance.scheduleViewModel }

            ScheduleScreen(
                viewModel = viewModel,
                navigateToAddSemester = { navController.navigate(ScheduleRoute.SemesterEditor()) },
                navigateToEditSchedule = { navController.navigate(ScheduleRoute.ScheduleEditor(it)) },
                navigateToShowLessonInformation = { navController.navigate(ScheduleRoute.LessonInformation(it)) },
            )
        }

        composable<ScheduleRoute.SemesterEditor> { backStackEntry ->
            val route = backStackEntry.toRoute<ScheduleRoute.SemesterEditor>()
            val viewModel = viewModel {
                ScheduleComponentHolder.instance.semesterEditorViewModelFactory.get(route.semesterId)
            }

            SemesterEditorScreen(
                viewModel = viewModel,
                navigateBack = { navController.popBackStack() },
            )
        }

        composable<ScheduleRoute.ScheduleEditor> { backStackEntry ->
            val route = backStackEntry.toRoute<ScheduleRoute.ScheduleEditor>()
            val viewModel = viewModel {
                ScheduleComponentHolder.instance.scheduleEditorViewModelFactory.get(route.semesterId)
            }

            ScheduleEditorScreen(
                viewModel = viewModel,
                navigateBack = { navController.popBackStack() },
                navigateToEditSemester = { navController.navigate(ScheduleRoute.SemesterEditor(it)) },
                navigateToEditLesson = { semesterId, lessonId, copy ->
                    navController.navigate(
                        ScheduleRoute.LessonEditor(semesterId = semesterId, lessonId = lessonId, copy = copy),
                    )
                },
                navigateToCreateLesson = { semesterId, dayOfWeek ->
                    navController.navigate(
                        ScheduleRoute.LessonEditor(semesterId = semesterId, dayOfWeekValue = dayOfWeek.value),
                    )
                },
            )
        }

        composable<ScheduleRoute.LessonEditor> { backStackEntry ->
            val route = backStackEntry.toRoute<ScheduleRoute.LessonEditor>()
            val semesterId = route.semesterId
            val dayOfWeek = route.dayOfWeek
            val subjectName = route.subjectName
            val lessonId = route.lessonId
            val copy = route.copy

            val viewModel = viewModel {
                val factory = ScheduleComponentHolder.instance.lessonEditorViewModelFactory
                when {
                    (dayOfWeek != null) -> factory.get(semesterId, dayOfWeek)
                    (subjectName != null) -> factory.get(semesterId, subjectName)
                    (lessonId != null) -> factory.get(semesterId, lessonId, copy == true)
                    else -> throw IllegalArgumentException("Wrong LessonEditor arguments")
                }
            }

            LessonEditorScreen(
                viewModel = viewModel,
                navigateBack = { navController.popBackStack() },
            )
        }

        composable<ScheduleRoute.LessonInformation> { backStackEntry ->
            val route = backStackEntry.toRoute<ScheduleRoute.LessonInformation>()
            val viewModel = viewModel {
                ScheduleComponentHolder.instance.lessonInformationViewModelFactory.get(route.lessonId)
            }

            LessonInformationScreen(
                viewModel = viewModel,
                navigateBack = { navController.popBackStack() },
                navigateToEditLesson = { semesterId, lessonId ->
                    navController.navigate(ScheduleRoute.LessonEditor(semesterId = semesterId, lessonId = lessonId))
                },
                navigateToEditHomework = { semesterId, homeworkId ->
                    navController.navigate(
                        HomeworksRoute.HomeworkEditor(semesterId = semesterId, homeworkId = homeworkId),
                    )
                },
                navigateToCreateHomework = { semesterId, subjectName ->
                    navController.navigate(
                        HomeworksRoute.HomeworkEditor(semesterId = semesterId, subjectName = subjectName),
                    )
                },
            )
        }
    }
}
