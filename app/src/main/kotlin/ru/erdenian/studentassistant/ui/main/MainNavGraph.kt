package ru.erdenian.studentassistant.ui.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.entity.Semester
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

    lateinit var semester: Semester
    lateinit var lesson: Lesson
    lateinit var homework: Homework

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

        val showLessonInformation: (lesson: Lesson) -> Unit = { lesson ->
            this@MainDirections.lesson = lesson
            navController.navigate("lesson_information/${lesson.id}")
        }

        val addSemester: () -> Unit = {
            navController.navigate("semester_editor")
        }

        val editSchedule: (semester: Semester) -> Unit = { semester ->
            this@MainDirections.semester = semester
            navController.navigate("schedule_editor/${semester.id}")
        }
    }

    val Homeworks = HomeworksDirections()

    inner class HomeworksDirections {

        val createHomework: (semesterId: Long) -> Unit = { semesterId ->
            navController.navigate("homework_editor?semester_id=$semesterId")
        }

        val editHomework: (homework: Homework) -> Unit = { homework ->
            this@MainDirections.homework = homework
            navController.navigate("homework_editor?homework_id=${homework.id}")
        }
    }

    val LessonInformation = LessonInformationDirections()

    inner class LessonInformationDirections {

        val editLesson: (lesson: Lesson) -> Unit = { lesson ->
            this@MainDirections.lesson = lesson
            navController.navigate("lesson_editor_edit/${lesson.id}")
        }

        val createHomework: (lesson: Lesson) -> Unit = { lesson ->
            this@MainDirections.lesson = lesson
            navController.navigate("homework_editor?lesson_id=${lesson.id}")
        }

        val editHomework: (homework: Homework) -> Unit = { homework ->
            this@MainDirections.homework = homework
            navController.navigate("homework_editor?homework_id=${homework.id}")
        }
    }

    val LessonsEditor = LessonsEditorDirections()

    inner class LessonsEditorDirections {

        val editSemester: (semester: Semester) -> Unit = { semester ->
            this@MainDirections.semester = semester
            navController.navigate("semester_editor?semester_id=${semester.id}")
        }

        val createLesson: (semesterId: Long, weekday: Int) -> Unit = { semesterId, weekday ->
            navController.navigate("lesson_editor_create?semester_id=$semesterId&weekday=$weekday")
        }

        val editLesson: (lesson: Lesson) -> Unit = { lesson ->
            this@MainDirections.lesson = lesson
            navController.navigate("lesson_editor_edit/${lesson.id}")
        }

        val copyLesson: (lesson: Lesson) -> Unit = { lesson ->
            this@MainDirections.lesson = lesson
            navController.navigate("lesson_editor_copy/${lesson.id}")
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
            val lesson = directions.lesson
            check(lesson.id == lessonId)

            val viewModel = viewModel { LessonInformationViewModel(it, lesson) }
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
            val semester = if ((semesterId != null) && (directions.semester.id == semesterId)) directions.semester else null

            val viewModel = viewModel { SemesterEditorViewModel(it, semester) }

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
            val semester = directions.semester
            check(semester.id == semesterId)

            val viewModel = viewModel { ScheduleEditorViewModel(it, semester) }
            val directions = directions.LessonsEditor

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
            route = "lesson_editor_edit/{lesson_id}",
            arguments = listOf(
                navArgument("lesson_id") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val lessonId = checkNotNull(backStackEntry.arguments?.getLong("lesson_id", -1L)?.takeIf { it >= 0 })
            val lesson = directions.lesson
            check(lesson.id == lessonId)

            val viewModel = viewModel { LessonEditorViewModel(it, lesson, false) }

            LessonEditorScreen(
                viewModel = viewModel,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "lesson_editor_copy/{lesson_id}",
            arguments = listOf(
                navArgument("lesson_id") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val lessonId = checkNotNull(backStackEntry.arguments?.getLong("lesson_id", -1L)?.takeIf { it >= 0 })
            val lesson = directions.lesson
            check(lesson.id == lessonId)

            val viewModel = viewModel { LessonEditorViewModel(it, lesson, true) }

            LessonEditorScreen(
                viewModel = viewModel,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "homework_editor?semester_id={semester_id}&lesson_id={lesson_id}&homework_id={homework_id}",
            arguments = listOf(
                navArgument("semester_id") {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument("lesson_id") {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument("homework_id") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val arguments = checkNotNull(backStackEntry.arguments)
            val semesterId = arguments.getLong("semester_id", -1L).takeIf { it >= 0 }
            val lessonId = arguments.getLong("lesson_id", -1L).takeIf { it >= 0 }
            val homeworkId = arguments.getLong("homework_id", -1L).takeIf { it >= 0 }
            val lesson = if ((lessonId != null) && (directions.lesson.id == lessonId)) directions.lesson else null
            val homework = if ((homeworkId != null) && (directions.homework.id == homeworkId)) directions.homework else null

            val viewModel = viewModel { application ->
                when {
                    (semesterId != null) -> HomeworkEditorViewModel(application, semesterId)
                    (lesson != null) -> HomeworkEditorViewModel(application, lesson)
                    (homework != null) -> HomeworkEditorViewModel(application, homework)
                    else -> throw IllegalArgumentException("Wrong HomeworkEditor arguments")
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
