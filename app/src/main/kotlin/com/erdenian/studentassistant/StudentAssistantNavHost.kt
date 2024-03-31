package com.erdenian.studentassistant

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.erdenian.studentassistant.di.MainComponent
import com.erdenian.studentassistant.homeworks.di.HomeworksComponent
import com.erdenian.studentassistant.homeworks.homeworkeditor.HomeworkEditorScreen
import com.erdenian.studentassistant.homeworks.homeworks.HomeworksScreen
import com.erdenian.studentassistant.schedule.di.ScheduleComponent
import com.erdenian.studentassistant.schedule.lessoneditor.LessonEditorScreen
import com.erdenian.studentassistant.schedule.lessoninformation.LessonInformationScreen
import com.erdenian.studentassistant.schedule.schedule.ScheduleScreen
import com.erdenian.studentassistant.schedule.scheduleeditor.ScheduleEditorScreen
import com.erdenian.studentassistant.schedule.semestereditor.SemesterEditorScreen
import com.erdenian.studentassistant.settings.SettingsScreen
import com.erdenian.studentassistant.settings.di.SettingsComponent
import com.erdenian.studentassistant.utils.SoftReferenceLazyComponentHolder
import java.time.DayOfWeek

@Composable
internal fun StudentAssistantNavHost(
    navController: NavHostController,
    navGraph: StudentAssistantNavGraph,
    modifier: Modifier = Modifier
) = NavHost(
    navController = navController,
    startDestination = MainRoutes.SCHEDULE,
    enterTransition = { fadeIn(tween()) },
    exitTransition = { fadeOut(tween()) },
    modifier = modifier,
    builder = navGraph::build
)

internal object MainRoutes {

    const val SCHEDULE = "schedule"
    const val HOMEWORKS = "homeworks"
    const val SETTINGS = "settings"
}

internal class StudentAssistantNavGraph(private val navController: NavHostController) {

    fun build(navGraphBuilder: NavGraphBuilder) = composables.forEach { it(navGraphBuilder) }

    private val composables = mutableListOf<NavGraphBuilder.() -> Unit>()

    private fun args(vararg arguments: Pair<String, Any?>) =
        arguments.asSequence().filter { it.second != null }.joinToString("&") { "${it.first}=${it.second}" }

    // region DI

    private val scheduleComponentHolder = SoftReferenceLazyComponentHolder(MainComponent::scheduleComponent)
    private val homeworksComponentHolder = SoftReferenceLazyComponentHolder(MainComponent::homeworksComponent)
    private val settingsComponentHolder = SoftReferenceLazyComponentHolder(MainComponent::settingsComponent)

    // endregion

    // region Schedule

    fun navigateToSchedule(restoreState: Boolean) {
        if (navController.currentBackStackEntry?.destination?.route != MainRoutes.SCHEDULE) {
            navController.navigate(MainRoutes.SCHEDULE) {
                launchSingleTop = true
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = restoreState
                }
                this.restoreState = restoreState
            }
        }
    }

    init {
        composables.add {
            composable(MainRoutes.SCHEDULE) {
                val viewModel = scheduleComponentHolder.viewModel(ScheduleComponent::scheduleViewModel)

                ScheduleScreen(
                    viewModel = viewModel,
                    navigateToAddSemester = ::navigateToSemesterEditor,
                    navigateToEditSchedule = ::navigateToScheduleEditor,
                    navigateToShowLessonInformation = ::navigateToLessonInformation
                )
            }
        }
    }

    // endregion

    // region Homeworks

    fun navigateToHomeworks(restoreState: Boolean) {
        if (navController.currentBackStackEntry?.destination?.route != MainRoutes.HOMEWORKS) {
            navController.navigate(MainRoutes.HOMEWORKS) {
                launchSingleTop = true
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = restoreState
                }
                this.restoreState = restoreState
            }
        }
    }

    init {
        composables.add {
            composable(MainRoutes.HOMEWORKS) {
                val viewModel = homeworksComponentHolder.viewModel(HomeworksComponent::homeworksViewModel)

                HomeworksScreen(
                    viewModel = viewModel,
                    navigateToCreateHomework = ::navigateToHomeworkEditor,
                    navigateToEditHomework = ::navigateToHomeworkEditor
                )
            }
        }
    }

    // endregion

    // region Settings

    fun navigateToSettings(restoreState: Boolean) {
        if (navController.currentBackStackEntry?.destination?.route != MainRoutes.SETTINGS) {
            navController.navigate(MainRoutes.SETTINGS) {
                launchSingleTop = true
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = restoreState
                }
                this.restoreState = restoreState
            }
        }
    }

    init {
        composables.add {
            composable(MainRoutes.SETTINGS) {
                val viewModel = settingsComponentHolder.viewModel(SettingsComponent::settingsViewModel)

                SettingsScreen(
                    viewModel = viewModel
                )
            }
        }
    }

    // endregion

    // region Lesson Information

    private fun navigateToLessonInformation(lessonId: Long) = navController.navigate("lessons/$lessonId")

    init {
        composables.add {
            composable(
                route = "lessons/{lesson_id}",
                arguments = listOf(
                    navArgument("lesson_id") {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->
                val lessonId = checkNotNull(backStackEntry.arguments?.getLong("lesson_id", -1L)?.takeIf { it >= 0 })
                val viewModel = scheduleComponentHolder.viewModel { lessonInformationViewModelFactory.get(lessonId) }

                LessonInformationScreen(
                    viewModel = viewModel,
                    navigateBack = { navController.popBackStack() },
                    navigateToEditLesson = { semester, lesson ->
                        navigateToLessonEditor(semester, lessonId = lesson)
                    },
                    navigateToEditHomework = { semesterId, homeworkId ->
                        navigateToHomeworkEditor(semesterId, homeworkId = homeworkId)
                    },
                    navigateToCreateHomework = { semesterId, subjectName ->
                        navigateToHomeworkEditor(semesterId, subjectName = subjectName)
                    }
                )
            }
        }
    }

    // endregion

    // region Semester Editor

    private fun navigateToSemesterEditor(semesterId: Long? = null) = navController.navigate(
        "semester_editor?${args("semester_id" to semesterId)}"
    )

    init {
        composables.add {
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
                val viewModel = scheduleComponentHolder.viewModel { semesterEditorViewModelFactory.get(semesterId) }

                SemesterEditorScreen(
                    viewModel = viewModel,
                    navigateBack = { navController.popBackStack() }
                )
            }
        }
    }

    // endregion

    // region Schedule Editor

    private fun navigateToScheduleEditor(semesterId: Long) = navController.navigate("schedule_editor/$semesterId")

    init {
        composables.add {
            composable(
                route = "schedule_editor/{semester_id}",
                arguments = listOf(
                    navArgument("semester_id") {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->
                val semesterId = checkNotNull(backStackEntry.arguments?.getLong("semester_id", -1L)?.takeIf { it >= 0 })
                val viewModel = scheduleComponentHolder.viewModel { scheduleEditorViewModelFactory.get(semesterId) }

                ScheduleEditorScreen(
                    viewModel = viewModel,
                    navigateBack = { navController.popBackStack() },
                    navigateToEditSemester = ::navigateToSemesterEditor,
                    navigateToEditLesson = { semester, lesson, copy ->
                        navigateToLessonEditor(semester, lessonId = lesson, copy = copy)
                    },
                    navigateToCreateLesson = { semester, dayOfWeek ->
                        navigateToLessonEditor(semester, dayOfWeek = dayOfWeek)
                    }
                )
            }
        }
    }

    // endregion

    // region Lesson Editor

    private fun navigateToLessonEditor(
        semesterId: Long,
        dayOfWeek: DayOfWeek? = null,
        subjectName: String? = null,
        lessonId: Long? = null,
        copy: Boolean? = null
    ) {
        val arguments = args(
            "day_of_week" to dayOfWeek?.value,
            "subject_name" to subjectName,
            "lesson_id" to lessonId,
            "copy" to copy
        )
        navController.navigate("lesson_editor/$semesterId?$arguments")
    }

    init {
        composables.add {
            composable(
                route = "lesson_editor/{semester_id}?day_of_week={day_of_week}&subject_name={subject_name}&lesson_id={lesson_id}&copy={copy}",
                arguments = listOf(
                    navArgument("semester_id") {
                        type = NavType.LongType
                    },
                    navArgument("day_of_week") {
                        type = NavType.IntType
                        defaultValue = -1
                    },
                    navArgument("subject_name") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("lesson_id") {
                        type = NavType.LongType
                        defaultValue = -1
                    },
                    navArgument("copy") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) { backStackEntry ->
                val arguments = checkNotNull(backStackEntry.arguments)
                val semesterId = checkNotNull(arguments.getLong("semester_id", -1L).takeIf { it >= 0 })
                val dayOfWeek = arguments.getInt("day_of_week", -1).takeIf { it >= 0 }?.let(DayOfWeek::of)
                val subjectName = arguments.getString("subject_name")
                val lessonId = arguments.getLong("lesson_id", -1L).takeIf { it >= 0 }
                val copy = arguments.getBoolean("copy")

                val viewModel = scheduleComponentHolder.viewModel {
                    val factory = lessonEditorViewModelFactory
                    when {
                        (dayOfWeek != null) -> factory.get(semesterId, dayOfWeek)
                        (subjectName != null) -> factory.get(semesterId, subjectName)
                        (lessonId != null) -> factory.get(semesterId, lessonId, copy)
                        else -> throw IllegalArgumentException("Wrong LessonEditor arguments")
                    }
                }

                LessonEditorScreen(
                    viewModel = viewModel,
                    navigateBack = { navController.popBackStack() }
                )
            }
        }
    }

    // endregion

    // region Homework Editor

    private fun navigateToHomeworkEditor(semesterId: Long, homeworkId: Long? = null, subjectName: String? = null) {
        val arguments = args(
            "semester_id" to semesterId,
            "homework_id" to homeworkId,
            "subject_name" to subjectName
        )
        navController.navigate("homework_editor?$arguments")
    }

    init {
        composables.add {
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
                    navigateToCreateLesson = { semester, subject ->
                        navigateToLessonEditor(semester, subjectName = subject)
                    }
                )
            }
        }
    }

    // endregion
}
