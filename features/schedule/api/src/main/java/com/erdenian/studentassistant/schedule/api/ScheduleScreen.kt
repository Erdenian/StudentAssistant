package com.erdenian.studentassistant.schedule.api

import cafe.adriel.voyager.core.registry.ScreenProvider
import java.time.DayOfWeek

sealed class ScheduleScreen : ScreenProvider {

    data object Schedule : ScheduleScreen()

    data class SemesterEditor(val semesterId: Long? = null) : ScheduleScreen()

    data class ScheduleEditor(val semesterId: Long) : ScheduleScreen()

    data class LessonEditor internal constructor(
        val semesterId: Long,
        val lessonId: Long? = null,
        val copy: Boolean = false,
        val dayOfWeek: DayOfWeek? = null,
        val subjectName: String? = null
    ) : ScheduleScreen() {

        constructor(semesterId: Long, dayOfWeek: DayOfWeek) : this(
            semesterId,
            lessonId = null,
            copy = false,
            dayOfWeek = dayOfWeek,
            subjectName = null
        )

        constructor(semesterId: Long, subjectName: String) : this(
            semesterId,
            lessonId = null,
            copy = false,
            dayOfWeek = null,
            subjectName = subjectName
        )

        constructor(semesterId: Long, lessonId: Long, copy: Boolean = false) : this(
            semesterId,
            lessonId = lessonId,
            copy = copy,
            dayOfWeek = null,
            subjectName = null
        )
    }

    data class LessonInformation(val lessonId: Long) : ScheduleScreen()
}
