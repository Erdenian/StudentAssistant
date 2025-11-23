package ru.erdenian.studentassistant.repository.database.entity

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import ru.erdenian.studentassistant.repository.api.entity.Lesson

internal class FullLessonTest {

    @Test
    fun byWeekdayTest() {
        FullLesson(
            LessonEntity("name", "type", LocalTime.of(10, 0), LocalTime.of(12, 0), 1L, 10L),
            listOf(TeacherEntity("teacher", 10L)),
            listOf(ClassroomEntity("classroom", 10L)),
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true)),
            emptySet(),
        )
    }

    @Test
    fun byDatesTest() {
        FullLesson(
            LessonEntity("name", "type", LocalTime.of(10, 0), LocalTime.of(12, 0), 1L, 10L),
            listOf(TeacherEntity("teacher", 10L)),
            listOf(ClassroomEntity("classroom", 10L)),
            null,
            setOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L)),
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
                emptySet(),
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
                setOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L)),
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
            emptySet(),
        ).toLesson()
        assertEquals("name", lesson1.subjectName)
        assertEquals("type", lesson1.type)
        assertEquals(LocalTime.of(10, 0), lesson1.startTime)
        assertEquals(LocalTime.of(12, 0), lesson1.endTime)
        assertEquals(1L, lesson1.semesterId)
        assertEquals(10L, lesson1.id)
        assertEquals(listOf("teacher"), lesson1.teachers)
        assertEquals(listOf("classroom"), lesson1.classrooms)
        assertEquals(DayOfWeek.MONDAY, (lesson1.lessonRepeat as Lesson.Repeat.ByWeekday).dayOfWeek)
        assertEquals(listOf(true), (lesson1.lessonRepeat as Lesson.Repeat.ByWeekday).weeks)

        val lesson2 = FullLesson(
            LessonEntity("name", "type", LocalTime.of(10, 0), LocalTime.of(12, 0), 1L, 10L),
            listOf(TeacherEntity("teacher", 10L)),
            listOf(ClassroomEntity("classroom", 10L)),
            null,
            setOf(ByDateEntity(LocalDate.of(2020, 4, 25), 10L)),
        ).toLesson()
        assertEquals(setOf(LocalDate.of(2020, 4, 25)), (lesson2.lessonRepeat as Lesson.Repeat.ByDates).dates)
    }
}
