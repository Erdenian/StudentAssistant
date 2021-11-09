package ru.erdenian.studentassistant.database.entity

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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
        assertThrows<IllegalArgumentException> {
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
        assertThrows<IllegalArgumentException> {
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
}
