package com.erdenian.studentassistant

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.erdenian.studentassistant.di.MainComponent
import com.erdenian.studentassistant.homeworks.api.HomeworkEditorRoute
import com.erdenian.studentassistant.homeworks.api.HomeworksRoute
import com.erdenian.studentassistant.homeworks.di.HomeworksComponent
import com.erdenian.studentassistant.homeworks.homeworkeditor.HomeworkEditorScreen
import com.erdenian.studentassistant.homeworks.homeworks.HomeworksScreen
import com.erdenian.studentassistant.schedule.api.LessonEditorRoute
import com.erdenian.studentassistant.schedule.api.LessonInformationRoute
import com.erdenian.studentassistant.schedule.api.ScheduleEditorRoute
import com.erdenian.studentassistant.schedule.api.ScheduleRoute
import com.erdenian.studentassistant.schedule.api.SemesterEditorRoute
import com.erdenian.studentassistant.schedule.di.ScheduleComponent
import com.erdenian.studentassistant.schedule.lessoneditor.LessonEditorScreen
import com.erdenian.studentassistant.schedule.lessoninformation.LessonInformationScreen
import com.erdenian.studentassistant.schedule.schedule.ScheduleScreen
import com.erdenian.studentassistant.schedule.scheduleeditor.ScheduleEditorScreen
import com.erdenian.studentassistant.schedule.semestereditor.SemesterEditorScreen
import com.erdenian.studentassistant.settings.SettingsScreen
import com.erdenian.studentassistant.settings.api.SettingsRoute
import com.erdenian.studentassistant.settings.di.SettingsComponent
import com.erdenian.studentassistant.utils.SoftReferenceLazyComponentHolder

@Composable
internal fun StudentAssistantNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val scheduleComponentHolder = remember { SoftReferenceLazyComponentHolder(MainComponent::scheduleComponent) }
    val homeworksComponentHolder = remember { SoftReferenceLazyComponentHolder(MainComponent::homeworksComponent) }
    val settingsComponentHolder = remember { SoftReferenceLazyComponentHolder(MainComponent::settingsComponent) }

    NavHost(
        navController = navController,
        startDestination = ScheduleRoute,
        enterTransition = { fadeIn(tween()) },
        exitTransition = { fadeOut(tween()) },
        modifier = modifier,
    ) {
        composable<ScheduleRoute> {
            val viewModel = scheduleComponentHolder.viewModel(ScheduleComponent::scheduleViewModel)

            ScheduleScreen(
                viewModel = viewModel,
                navigateToAddSemester = { navController.navigate(SemesterEditorRoute()) },
                navigateToEditSchedule = { navController.navigate(ScheduleEditorRoute(it)) },
                navigateToShowLessonInformation = { navController.navigate(LessonInformationRoute(it)) },
            )
        }

        composable<HomeworksRoute> {
            val viewModel = homeworksComponentHolder.viewModel(HomeworksComponent::homeworksViewModel)

            HomeworksScreen(
                viewModel = viewModel,
                navigateToCreateHomework = { navController.navigate(HomeworkEditorRoute(it)) },
                navigateToEditHomework = { semesterId: Long, homeworkId: Long ->
                    navController.navigate(HomeworkEditorRoute(semesterId = semesterId, homeworkId = homeworkId))
                },
            )
        }

        composable<SettingsRoute> {
            val viewModel = settingsComponentHolder.viewModel(SettingsComponent::settingsViewModel)

            SettingsScreen(
                viewModel = viewModel,
            )
        }

        composable<LessonInformationRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<LessonInformationRoute>()
            val viewModel = scheduleComponentHolder.viewModel { lessonInformationViewModelFactory.get(route.lessonId) }

            LessonInformationScreen(
                viewModel = viewModel,
                navigateBack = { navController.popBackStack() },
                navigateToEditLesson = { semesterId, lessonId ->
                    navController.navigate(LessonEditorRoute(semesterId = semesterId, lessonId = lessonId))
                },
                navigateToEditHomework = { semesterId, homeworkId ->
                    navController.navigate(HomeworkEditorRoute(semesterId = semesterId, homeworkId = homeworkId))
                },
                navigateToCreateHomework = { semesterId, subjectName ->
                    navController.navigate(HomeworkEditorRoute(semesterId = semesterId, subjectName = subjectName))
                },
            )
        }

        composable<SemesterEditorRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<SemesterEditorRoute>()
            val viewModel = scheduleComponentHolder.viewModel { semesterEditorViewModelFactory.get(route.semesterId) }

            SemesterEditorScreen(
                viewModel = viewModel,
                navigateBack = { navController.popBackStack() },
            )
        }

        composable<ScheduleEditorRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ScheduleEditorRoute>()
            val viewModel = scheduleComponentHolder.viewModel { scheduleEditorViewModelFactory.get(route.semesterId) }

            ScheduleEditorScreen(
                viewModel = viewModel,
                navigateBack = { navController.popBackStack() },
                navigateToEditSemester = { navController.navigate(SemesterEditorRoute(it)) },
                navigateToEditLesson = { semesterId, lessonId, copy ->
                    navController.navigate(LessonEditorRoute(semesterId = semesterId, lessonId = lessonId, copy = copy))
                },
                navigateToCreateLesson = { semesterId, dayOfWeek ->
                    navController.navigate(LessonEditorRoute(semesterId = semesterId, dayOfWeekValue = dayOfWeek.value))
                },
            )
        }

        composable<LessonEditorRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<LessonEditorRoute>()
            val semesterId = route.semesterId
            val dayOfWeek = route.dayOfWeek
            val subjectName = route.subjectName
            val lessonId = route.lessonId
            val copy = route.copy

            val viewModel = scheduleComponentHolder.viewModel {
                val factory = lessonEditorViewModelFactory
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

        composable<HomeworkEditorRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<HomeworkEditorRoute>()
            val semesterId = route.semesterId
            val subjectName = route.subjectName
            val homeworkId = route.homeworkId

            val viewModel = homeworksComponentHolder.viewModel {
                val factory = homeworkEditorViewModelFactory
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
                    navController.navigate(LessonEditorRoute(semesterId = semesterId, subjectName = subjectName))
                },
            )
        }
    }
}
