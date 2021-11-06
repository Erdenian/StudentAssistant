package ru.erdenian.studentassistant.ui.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.erdenian.studentassistant.ui.main.homeworkeditor.HomeworkEditorScreen
import ru.erdenian.studentassistant.ui.main.homeworkeditor.HomeworkEditorViewModel
import ru.erdenian.studentassistant.ui.main.homeworks.HomeworksScreen
import ru.erdenian.studentassistant.ui.main.homeworks.HomeworksViewModel
import ru.erdenian.studentassistant.ui.main.lessoneditor.LessonEditorScreen
import ru.erdenian.studentassistant.ui.main.lessoneditor.LessonEditorViewModel
import ru.erdenian.studentassistant.ui.main.lessoninformation.LessonInformationScreen
import ru.erdenian.studentassistant.ui.main.lessoninformation.LessonInformationViewModel
import ru.erdenian.studentassistant.ui.main.schedule.ScheduleScreen
import ru.erdenian.studentassistant.ui.main.schedule.ScheduleViewModel
import ru.erdenian.studentassistant.ui.main.scheduleeditor.ScheduleEditorScreen
import ru.erdenian.studentassistant.ui.main.scheduleeditor.ScheduleEditorViewModel
import ru.erdenian.studentassistant.ui.main.semestereditor.SemesterEditorScreen
import ru.erdenian.studentassistant.ui.main.semestereditor.SemesterEditorViewModel
import ru.erdenian.studentassistant.ui.main.settings.SettingsScreen
import ru.erdenian.studentassistant.ui.main.settings.SettingsViewModel
import ru.erdenian.studentassistant.utils.viewModel

object MainRoutes {

    const val SCHEDULE = "schedule"
    const val HOMEWORKS = "homeworks"
    const val SETTINGS = "settings"
}

class MainDirections(private val navController: NavHostController) {

    val schedule: () -> Unit = {
        if (navController.currentBackStackEntry?.destination?.route != MainRoutes.SCHEDULE) {
            navController.navigate(MainRoutes.SCHEDULE) {
                launchSingleTop = true
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                restoreState = true
            }
        }
    }

    val homeworks: () -> Unit = {
        if (navController.currentBackStackEntry?.destination?.route != MainRoutes.HOMEWORKS) {
            navController.navigate(MainRoutes.HOMEWORKS) {
                launchSingleTop = true
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                restoreState = true
            }
        }
    }

    val settings: () -> Unit = {
        if (navController.currentBackStackEntry?.destination?.route != MainRoutes.SETTINGS) {
            navController.navigate(MainRoutes.SETTINGS) {
                launchSingleTop = true
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                restoreState = true
            }
        }
    }

    val Schedule = ScheduleDirections()

    inner class ScheduleDirections {

        val showLessonInformation: (lessonId: Long) -> Unit = { lessonId ->
            navController.navigate("lesson_information/$lessonId")
        }

        val addSemester: () -> Unit = {
            navController.navigate("semester_editor")
        }

        val editSchedule: (semesterId: Long) -> Unit = { semesterId ->
            navController.navigate("schedule_editor/$semesterId")
        }
    }

    val Homeworks = HomeworksDirections()

    inner class HomeworksDirections {

        val createHomework: (semesterId: Long) -> Unit = { semesterId ->
            navController.navigate("homework_editor?semester_id=$semesterId")
        }

        val editHomework: (semesterId: Long, homeworkId: Long) -> Unit = { semesterId, homeworkId ->
            navController.navigate("homework_editor?semester_id=$semesterId&homework_id=$homeworkId")
        }
    }

    val LessonInformation = LessonInformationDirections()

    inner class LessonInformationDirections {

        val editLesson: (semesterId: Long, lessonId: Long) -> Unit = { semesterId, lessonId ->
            navController.navigate("lesson_editor_edit/$semesterId/$lessonId")
        }

        val createHomework: (semesterId: Long, subjectName: String) -> Unit = { semesterId, subjectName ->
            navController.navigate("homework_editor?semester_id=$semesterId&subject_name=$subjectName")
        }

        val editHomework: (semesterId: Long, homeworkId: Long) -> Unit = { semesterId, homeworkId ->
            navController.navigate("homework_editor?semester_id=$semesterId&homework_id=$homeworkId")
        }
    }

    val ScheduleEditor = ScheduleEditorDirections()

    inner class ScheduleEditorDirections {

        val editSemester: (semesterId: Long) -> Unit = { semesterId: Long ->
            navController.navigate("semester_editor?semester_id=${semesterId}")
        }

        val createLesson: (semesterId: Long, weekday: Int) -> Unit = { semesterId, weekday ->
            navController.navigate("lesson_editor_create?semester_id=$semesterId&weekday=$weekday")
        }

        val editLesson: (semesterId: Long, lessonId: Long) -> Unit = { semesterId, lessonId ->
            navController.navigate("lesson_editor_edit/$semesterId/$lessonId")
        }

        val copyLesson: (semesterId: Long, lessonId: Long) -> Unit = { semesterId, lessonId ->
            navController.navigate("lesson_editor_copy/$semesterId/$lessonId")
        }
    }

    val HomeworkEditor = HomeworkEditorDirections()

    inner class HomeworkEditorDirections {

        val createLesson: (semesterId: Long, subjectName: String) -> Unit = { semesterId, subjectName ->
            navController.navigate("lesson_editor_create?semester_id=$semesterId&subject_name=$subjectName")
        }
    }
}

@Composable
fun MainNavGraph(
    navController: NavHostController,
    directions: MainDirections
) {
    @Suppress("NAME_SHADOWING")
    NavHost(
        navController = navController,
        startDestination = MainRoutes.SCHEDULE
    ) {
        composable(MainRoutes.SCHEDULE) {
            val viewModel = viewModel<ScheduleViewModel>()
            val directions = directions.Schedule

            ScheduleScreen(
                viewModel = viewModel,
                navigateToAddSemester = directions.addSemester,
                navigateToEditSchedule = directions.editSchedule,
                navigateToShowLessonInformation = directions.showLessonInformation
            )
        }

        composable(MainRoutes.HOMEWORKS) {
            val viewModel = viewModel<HomeworksViewModel>()
            val directions = directions.Homeworks

            HomeworksScreen(
                viewModel = viewModel,
                navigateToCreateHomework = directions.createHomework,
                navigateToEditHomework = directions.editHomework
            )
        }

        composable(MainRoutes.SETTINGS) {
            val viewModel = viewModel<SettingsViewModel>()

            SettingsScreen(
                viewModel = viewModel
            )
        }

        composable(
            route = "lesson_information/{lesson_id}",
            arguments = listOf(
                navArgument("lesson_id") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val lessonId = checkNotNull(backStackEntry.arguments?.getLong("lesson_id", -1L)?.takeIf { it >= 0 })
            val viewModel = viewModel { LessonInformationViewModel(it, lessonId) }
            val directions = directions.LessonInformation

            LessonInformationScreen(
                viewModel = viewModel,
                navigateBack = { navController.popBackStack() },
                navigateToEditLesson = directions.editLesson,
                navigateToEditHomework = directions.editHomework,
                navigateToCreateHomework = directions.createHomework
            )
        }

        composable(
            route = "semester_editor?semester_id={semester_id}",
            arguments = listOf(
                navArgument("semester_id") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val semesterId = backStackEntry.arguments?.getLong("semester_id", -1L)?.takeIf { it >= 0 }
            val viewModel = viewModel { SemesterEditorViewModel(it, semesterId) }

            SemesterEditorScreen(
                viewModel = viewModel,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "schedule_editor/{semester_id}",
            arguments = listOf(
                navArgument("semester_id") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val semesterId = checkNotNull(backStackEntry.arguments?.getLong("semester_id", -1L)?.takeIf { it >= 0 })
            val viewModel = viewModel { ScheduleEditorViewModel(it, semesterId) }
            val directions = directions.ScheduleEditor

            ScheduleEditorScreen(
                viewModel = viewModel,
                navigateBack = { navController.popBackStack() },
                navigateToEditSemester = directions.editSemester,
                navigateToEditLesson = directions.editLesson,
                navigateToCopyLesson = directions.copyLesson,
                navigateToCreateLesson = directions.createLesson
            )
        }

        composable(
            route = "lesson_editor_create?semester_id={semester_id}&weekday={weekday}&subject_name={subject_name}",
            arguments = listOf(
                navArgument("semester_id") {
                    type = NavType.LongType
                },
                navArgument("weekday") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument("subject_name") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val arguments = checkNotNull(backStackEntry.arguments)
            val semesterId = checkNotNull(arguments.getLong("semester_id", -1L).takeIf { it >= 0 })
            val weekday = arguments.getInt("weekday", -1).takeIf { it >= 0 }
            val subjectName = arguments.getString("subject_name")

            val viewModel = viewModel { application ->
                when {
                    (weekday != null) -> LessonEditorViewModel(application, semesterId, weekday)
                    (subjectName != null) -> LessonEditorViewModel(application, semesterId, subjectName)
                    else -> throw IllegalArgumentException("Wrong LessonEditor arguments")
                }
            }

            LessonEditorScreen(
                viewModel = viewModel,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "lesson_editor_edit/{semester_id}/{lesson_id}",
            arguments = listOf(
                navArgument("semester_id") {
                    type = NavType.LongType
                },
                navArgument("lesson_id") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val semesterId = checkNotNull(backStackEntry.arguments?.getLong("semester_id", -1L)?.takeIf { it >= 0 })
            val lessonId = checkNotNull(backStackEntry.arguments?.getLong("lesson_id", -1L)?.takeIf { it >= 0 })
            val viewModel = viewModel { LessonEditorViewModel(it, semesterId, lessonId, false) }

            LessonEditorScreen(
                viewModel = viewModel,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "lesson_editor_copy/{semester_id}/{lesson_id}",
            arguments = listOf(
                navArgument("semester_id") {
                    type = NavType.LongType
                },
                navArgument("lesson_id") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val semesterId = checkNotNull(backStackEntry.arguments?.getLong("semester_id", -1L)?.takeIf { it >= 0 })
            val lessonId = checkNotNull(backStackEntry.arguments?.getLong("lesson_id", -1L)?.takeIf { it >= 0 })
            val viewModel = viewModel { LessonEditorViewModel(it, semesterId, lessonId, true) }

            LessonEditorScreen(
                viewModel = viewModel,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "homework_editor?semester_id={semester_id}&subject_name={subject_name}&homework_id={homework_id}",
            arguments = listOf(
                navArgument("semester_id") {
                    type = NavType.LongType
                },
                navArgument("subject_name") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("homework_id") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val arguments = checkNotNull(backStackEntry.arguments)
            val semesterId = checkNotNull(arguments.getLong("semester_id", -1L).takeIf { it >= 0 })
            val subjectName = arguments.getString("subject_name")
            val homeworkId = arguments.getLong("homework_id", -1L).takeIf { it >= 0 }

            val viewModel = viewModel { application ->
                when {
                    (homeworkId != null) -> HomeworkEditorViewModel(application, semesterId, homeworkId)
                    (subjectName != null) -> HomeworkEditorViewModel(application, semesterId, subjectName)
                    else -> HomeworkEditorViewModel(application, semesterId)
                }
            }
            val directions = directions.HomeworkEditor

            HomeworkEditorScreen(
                viewModel = viewModel,
                navigateBack = { navController.popBackStack() },
                navigateToCreateLesson = directions.createLesson
            )
        }
    }
}
