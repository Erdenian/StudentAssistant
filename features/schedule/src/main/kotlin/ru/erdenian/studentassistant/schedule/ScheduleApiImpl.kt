package ru.erdenian.studentassistant.schedule

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import javax.inject.Inject
import ru.erdenian.studentassistant.schedule.api.ScheduleApi
import ru.erdenian.studentassistant.schedule.api.ScheduleRoute
import ru.erdenian.studentassistant.schedule.di.ScheduleComponentHolder
import ru.erdenian.studentassistant.schedule.lessoneditor.LessonEditorScreen
import ru.erdenian.studentassistant.schedule.lessoninformation.LessonInformationScreen
import ru.erdenian.studentassistant.schedule.schedule.ScheduleScreen
import ru.erdenian.studentassistant.schedule.scheduleeditor.ScheduleEditorScreen
import ru.erdenian.studentassistant.schedule.semestereditor.SemesterEditorScreen

public fun createScheduleApi(dependencies: ScheduleDependencies): ScheduleApi =
    ScheduleComponentHolder.create(dependencies).api

internal class ScheduleApiImpl @Inject constructor() : ScheduleApi {
    override fun addToGraph(scope: EntryProviderScope<NavKey>) {
        scope.entry<ScheduleRoute.Schedule> { ScheduleScreen() }
        scope.entry<ScheduleRoute.SemesterEditor> { SemesterEditorScreen(it) }
        scope.entry<ScheduleRoute.ScheduleEditor> { ScheduleEditorScreen(it) }
        scope.entry<ScheduleRoute.LessonEditor> { LessonEditorScreen(it) }
        scope.entry<ScheduleRoute.LessonInformation> { LessonInformationScreen(it) }
    }
}
