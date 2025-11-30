package ru.erdenian.studentassistant.schedule.api

import androidx.navigation3.runtime.NavKey
import java.time.DayOfWeek
import kotlinx.serialization.Serializable
import ru.erdenian.studentassistant.repository.api.entity.Lesson

sealed interface ScheduleRoute : NavKey {

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
    data class LessonInformation(val lesson: Lesson) : ScheduleRoute
}
