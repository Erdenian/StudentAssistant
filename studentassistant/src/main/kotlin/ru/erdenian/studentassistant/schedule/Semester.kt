package ru.erdenian.studentassistant.schedule

import com.google.common.collect.ComparisonChain
import com.google.common.collect.ImmutableSortedSet
import org.joda.time.Days
import org.joda.time.LocalDate

data class Semester(val name: String, val firstDay: LocalDate, val lastDay: LocalDate,
                    val lessons: ImmutableSortedSet<Lesson> = ImmutableSortedSet.of(),
                    val homeworks: ImmutableSortedSet<Homework> = ImmutableSortedSet.of(),
                    val id: Long = System.nanoTime()) : Comparable<Semester> {

    val length = Days.daysBetween(firstDay, lastDay).days + 1

    fun getLesson(id: Long): Lesson? {
        return lessons.asList()[getLessonIndex(id) ?: return null]
    }

    fun getLessonIndex(id: Long): Int? {
        for ((i, lesson) in lessons.withIndex())
            if (lesson.id == id) return i
        return null
    }

    fun getLessons(day: LocalDate): List<Lesson> {
        val weekNumber: Int
        try {
            weekNumber = getWeekNumber(day)
        } catch (iae: IllegalArgumentException) {
            return emptyList()
        }
        return lessons.filter { it.repeatsOnDay(day, weekNumber) }
    }

    fun getLessons(weekday: Int): List<Lesson> {
        return lessons.filter { it.repeatsOnWeekday(weekday) }
    }

    private fun getWeekNumber(day: LocalDate): Int {
        if (day.isBefore(firstDay) || day.isAfter(lastDay)) {
            throw IllegalArgumentException("Переданный день не принадлежит семестру: $day")
        }
        return Days.daysBetween(firstDay.minusDays(firstDay.dayOfWeek - 1), day).days / 7
    }

    override fun compareTo(other: Semester): Int {
        return ComparisonChain.start()
                .compare(lastDay, other.lastDay)
                .compare(firstDay, other.firstDay)
                .compare(name, other.name)
                .result()
    }
}
