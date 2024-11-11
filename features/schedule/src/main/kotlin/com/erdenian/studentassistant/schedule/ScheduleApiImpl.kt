package com.erdenian.studentassistant.schedule

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
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
    override fun NavGraphBuilder.composable() {
        composable<ScheduleRoute.Schedule> { ScheduleScreen() }
        composable<ScheduleRoute.SemesterEditor> { SemesterEditorScreen(it.toRoute()) }
        composable<ScheduleRoute.ScheduleEditor> { ScheduleEditorScreen(it.toRoute()) }
        composable<ScheduleRoute.LessonEditor> { LessonEditorScreen(it.toRoute()) }
        composable<ScheduleRoute.LessonInformation> { LessonInformationScreen(it.toRoute()) }
    }
}
