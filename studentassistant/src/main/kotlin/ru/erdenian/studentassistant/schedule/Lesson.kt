package ru.erdenian.studentassistant.schedule

import com.google.common.collect.ComparisonChain
import com.google.common.collect.ImmutableSortedSet
import org.joda.time.LocalTime

data class Lesson(val subjectName: String, val type: String, val teachers: ImmutableSortedSet<String>,
                  val classrooms: ImmutableSortedSet<String>, val startTime: LocalTime, val endTime: LocalTime,
                  val lessonRepeat: LessonRepeat, val id: Long = -1) : Comparable<Lesson> {

    init {
        if (subjectName.isBlank()) throw IllegalArgumentException("Отсутствует название предмета")
        if (!startTime.isBefore(endTime)) throw IllegalArgumentException("Неверно заданы даты: $startTime - $endTime")
    }

    override fun compareTo(other: Lesson) = ComparisonChain.start()
            .compare(startTime, other.startTime)
            .compare(endTime, other.endTime)
            .compare(id, other.id)
            .result()
}
