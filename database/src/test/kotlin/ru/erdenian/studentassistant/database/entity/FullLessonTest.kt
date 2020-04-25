package ru.erdenian.studentassistant.database.entity

import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class FullLessonTest {

    @Test
    fun byWeekdayTest() {
        FullLesson(
            LessonEntity(
                "name", "type",
                LocalTime.MIDNIGHT, LocalTime.MIDNIGHT.plusHours(2),
                1L, 10L
            ),
            listOf(TeacherEntity("teacher", 10L)),
            listOf(ClassroomEntity("classroom", 10L)),
            ByWeekdayEntity(DateTimeConstants.MONDAY, listOf(true)),
            emptyList()
        )
    }

    @Test
    fun byDatesTest() {
        FullLesson(
            LessonEntity(
                "name", "type",
                LocalTime.MIDNIGHT, LocalTime.MIDNIGHT.plusHours(2),
                1L, 10L
            ),
            listOf(TeacherEntity("teacher", 10L)),
            listOf(ClassroomEntity("classroom", 10L)),
            null,
            listOf(ByDateEntity(LocalDate(2020, 4, 25), 10L))
        )
    }

    @Test
    fun noRepeatsTest() {
        assertThrows<IllegalArgumentException> {
            FullLesson(
                LessonEntity(
                    "name", "type",
                    LocalTime.MIDNIGHT, LocalTime.MIDNIGHT.plusHours(2),
                    1L, 10L
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
                    "name", "type",
                    LocalTime.MIDNIGHT, LocalTime.MIDNIGHT.plusHours(2),
                    1L, 10L
                ),
                listOf(TeacherEntity("teacher", 10L)),
                listOf(ClassroomEntity("classroom", 10L)),
                ByWeekdayEntity(DateTimeConstants.MONDAY, listOf(true)),
                listOf(ByDateEntity(LocalDate(2020, 4, 25), 10L))
            )
        }
    }
}
