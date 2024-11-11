package com.erdenian.studentassistant.schedule.api

import com.erdenian.studentassistant.navigation.Route
import java.time.DayOfWeek
import kotlinx.serialization.Serializable

sealed interface ScheduleRoute : Route {

    @Serializable
    data object Schedule : ScheduleRoute

    @Serializable
    data class SemesterEditor(val semesterId: Long? = null) : ScheduleRoute

    @Serializable
    data class ScheduleEditor(val semesterId: Long) : ScheduleRoute

    @Serializable
    data class LessonEditor(
        val semesterId: Long,
        val dayOfWeekValue: Int? = null,
        val subjectName: String? = null,
        val lessonId: Long? = null,
        val copy: Boolean? = null,
    ) : ScheduleRoute {
        val dayOfWeek get() = dayOfWeekValue?.let(DayOfWeek::of)
    }

    @Serializable
    data class LessonInformation(val lessonId: Long) : ScheduleRoute
}
