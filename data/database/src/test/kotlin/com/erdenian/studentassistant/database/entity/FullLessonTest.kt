package com.erdenian.studentassistant.database.entity

import com.erdenian.studentassistant.entity.Lesson
import com.erdenian.studentassistant.entity.immutableSortedSetOf
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

internal class FullLessonTest {

    @Test
    fun byWeekdayTest() {
        FullLesson(
            LessonEntity(
                "name",
                "type",
                LocalTime.MIDNIGHT,
                LocalTime.MIDNIGHT.plusHours(2),
                1L,
                10L
            ),
            listOf(TeacherEntity("teacher", 10L)),
            listOf(ClassroomEntity("classroom", 10L)),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
            emptyList()
        )
    }

    @Test
    fun byDatesTest() {
        FullLesson(
            LessonEntity(
                "name",
                "type",
                LocalTime.MIDNIGHT,
                LocalTime.MIDNIGHT.plusHours(2),
                1L,
                10L
            ),
            listOf(TeacherEntity("teacher", 10L)),
            listOf(ClassroomEntity("classroom", 10L)),
            null,
            listOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L))
        )
    }

    @Test
    fun noRepeatsTest() {
        assertThrows(IllegalArgumentException::class.java) {
            FullLesson(
                LessonEntity(
                    "name",
                    "type",
                    LocalTime.MIDNIGHT,
                    LocalTime.MIDNIGHT.plusHours(2),
                    1L,
                    10L
                ),
                listOf(TeacherEntity("teacher", 10L)),
                listOf(ClassroomEntity("classroom", 10L)),
                null,
                emptyList()
            )
        }
    }

    @Test
    fun multipleRepeatsTest() {
        assertThrows(IllegalArgumentException::class.java) {
            FullLesson(
                LessonEntity(
                    "name",
                    "type",
                    LocalTime.MIDNIGHT,
                    LocalTime.MIDNIGHT.plusHours(2),
                    1L,
                    10L
                ),
                listOf(TeacherEntity("teacher", 10L)),
                listOf(ClassroomEntity("classroom", 10L)),
                ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
                listOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L))
            )
        }
    }

    @Test
    fun propertiesTest() {
        val lesson1 = FullLesson(
            LessonEntity(
                "name",
                "type",
                LocalTime.MIDNIGHT,
                LocalTime.MIDNIGHT.plusHours(2),
                1L,
                10L
            ),
            listOf(TeacherEntity("teacher", 10L)),
            listOf(ClassroomEntity("classroom", 10L)),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
            emptyList()
        )
        assertEquals("name", lesson1.subjectName)
        assertEquals("type", lesson1.type)
        assertEquals(LocalTime.MIDNIGHT, lesson1.startTime)
        assertEquals(LocalTime.MIDNIGHT.plusHours(2), lesson1.endTime)
        assertEquals(1L, lesson1.semesterId)
        assertEquals(10L, lesson1.id)
        assertEquals(immutableSortedSetOf("teacher"), lesson1.teachers)
        assertEquals(immutableSortedSetOf("classroom"), lesson1.classrooms)
        assertEquals(DayOfWeek.MONDAY, (lesson1.lessonRepeat as Lesson.Repeat.ByWeekday).dayOfWeek)
        assertEquals(listOf(true), (lesson1.lessonRepeat as Lesson.Repeat.ByWeekday).weeks)

        val lesson2 = FullLesson(
            LessonEntity(
                "name",
                "type",
                LocalTime.MIDNIGHT,
                LocalTime.MIDNIGHT.plusHours(2),
                1L,
                10L
            ),
            listOf(TeacherEntity("teacher", 10L)),
            listOf(ClassroomEntity("classroom", 10L)),
            null,
            listOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L))
        )
        assertEquals(setOf(LocalDate.of(2020, 4, 25)), (lesson2.lessonRepeat as Lesson.Repeat.ByDates).dates)
    }
}
