package com.erdenian.studentassistant.repository.database.entity

import com.erdenian.studentassistant.repository.api.entity.Lesson
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
            LessonEntity("name", "type", LocalTime.of(10, 0), LocalTime.of(12, 0), 1L, 10L),
            listOf(TeacherEntity("teacher", 10L)),
            listOf(ClassroomEntity("classroom", 10L)),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
            emptyList(),
        )
    }

    @Test
    fun byDatesTest() {
        FullLesson(
            LessonEntity("name", "type", LocalTime.of(10, 0), LocalTime.of(12, 0), 1L, 10L),
            listOf(TeacherEntity("teacher", 10L)),
            listOf(ClassroomEntity("classroom", 10L)),
            null,
            listOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L)),
        )
    }

    @Test
    fun noRepeatsTest() {
        assertThrows(IllegalArgumentException::class.java) {
            FullLesson(
                LessonEntity("name", "type", LocalTime.of(10, 0), LocalTime.of(12, 0), 1L, 10L),
                listOf(TeacherEntity("teacher", 10L)),
                listOf(ClassroomEntity("classroom", 10L)),
                null,
                emptyList(),
            )
        }
    }

    @Test
    fun multipleRepeatsTest() {
        assertThrows(IllegalArgumentException::class.java) {
            FullLesson(
                LessonEntity("name", "type", LocalTime.of(10, 0), LocalTime.of(12, 0), 1L, 10L),
                listOf(TeacherEntity("teacher", 10L)),
                listOf(ClassroomEntity("classroom", 10L)),
                ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
                listOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L)),
            )
        }
    }

    @Test
    fun propertiesTest() {
        val lesson1 = FullLesson(
            LessonEntity(
                "name", "type", LocalTime.of(10, 0), LocalTime.of(12, 0), 1L, 10L,
            ),
            listOf(TeacherEntity("teacher", 10L)),
            listOf(ClassroomEntity("classroom", 10L)),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
            emptyList(),
        )
        assertEquals("name", lesson1.lesson.subjectName)
        assertEquals("type", lesson1.lesson.type)
        assertEquals(LocalTime.of(10, 0), lesson1.lesson.startTime)
        assertEquals(LocalTime.of(12, 0), lesson1.lesson.endTime)
        assertEquals(1L, lesson1.lesson.semesterId)
        assertEquals(10L, lesson1.lesson.id)
        assertEquals(listOf("teacher"), lesson1.teachers)
        assertEquals(listOf("classroom"), lesson1.classrooms)
        assertEquals(DayOfWeek.MONDAY, (lesson1.toLesson().lessonRepeat as Lesson.Repeat.ByWeekday).dayOfWeek)
        assertEquals(listOf(true), (lesson1.toLesson().lessonRepeat as Lesson.Repeat.ByWeekday).weeks)

        val lesson2 = FullLesson(
            LessonEntity("name", "type", LocalTime.of(10, 0), LocalTime.of(12, 0), 1L, 10L),
            listOf(TeacherEntity("teacher", 10L)),
            listOf(ClassroomEntity("classroom", 10L)),
            null,
            listOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L)),
        )
        assertEquals(setOf(LocalDate.of(2020, 4, 25)), (lesson2.toLesson().lessonRepeat as Lesson.Repeat.ByDates).dates)
    }
}
