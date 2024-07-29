package com.erdenian.studentassistant.schedule

import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.screenModule
import com.erdenian.studentassistant.mediator.ApiComponentHolder
import com.erdenian.studentassistant.schedule.api.ScheduleScreen
import com.erdenian.studentassistant.schedule.di.DaggerScheduleComponent
import com.erdenian.studentassistant.schedule.lessoneditor.LessonEditorScreen
import com.erdenian.studentassistant.schedule.lessoninformation.LessonInformationScreen
import com.erdenian.studentassistant.schedule.scheduleeditor.ScheduleEditorScreen
import com.erdenian.studentassistant.schedule.semestereditor.SemesterEditorScreen
import javax.inject.Inject

interface ScheduleApi {
    val screenModule: ScreenRegistry.() -> Unit
}

internal class ScheduleApiImpl @Inject constructor() : ScheduleApi {
    override val screenModule = screenModule {
        register<ScheduleScreen.Schedule> { com.erdenian.studentassistant.schedule.schedule.ScheduleScreen() }
        register<ScheduleScreen.SemesterEditor> { SemesterEditorScreen(it) }
        register<ScheduleScreen.ScheduleEditor> { ScheduleEditorScreen(it) }
        register<ScheduleScreen.LessonEditor> { LessonEditorScreen(it) }
        register<ScheduleScreen.LessonInformation> { LessonInformationScreen(it) }
    }
}

class ScheduleApiComponentHolder(dependencies: ScheduleDependencies) : ApiComponentHolder<ScheduleApi>(
    DaggerScheduleComponent.factory().create(dependencies)
)
