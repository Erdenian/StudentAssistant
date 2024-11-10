package com.erdenian.studentassistant.schedule.api

import java.time.DayOfWeek
import kotlinx.serialization.Serializable

@Serializable
data class LessonEditorRoute(
    val semesterId: Long,
    val dayOfWeekValue: Int? = null,
    val subjectName: String? = null,
    val lessonId: Long? = null,
    val copy: Boolean? = null,
) {
    val dayOfWeek get() = dayOfWeekValue?.let(DayOfWeek::of)
}
