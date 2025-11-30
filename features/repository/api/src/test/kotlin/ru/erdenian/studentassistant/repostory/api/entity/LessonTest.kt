package ru.erdenian.studentassistant.repostory.api.entity

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.erdenian.studentassistant.repository.api.entity.Lesson

internal class LessonTest {

    @Test
    fun byWeekdayTest() {
        val repeat = Lesson.Repeat.ByWeekday(DayOfWeek.THURSDAY, listOf(true, false, true))
        assertEquals(DayOfWeek.THURSDAY, repeat.dayOfWeek)
        assertEquals(listOf(true, false, true), repeat.weeks)
    }

    @Test
    fun byDatesTest() {
        val dates = setOf(LocalDate.of(2023, 2, 16))
        val repeat = Lesson.Repeat.ByDates(dates)
        assertEquals(dates, repeat.dates)
    }

    @Test
    fun compareToTest() {
        val base = Lesson(
            "subject", "type", listOf("t"), listOf("c"),
            LocalTime.of(9, 0), LocalTime.of(10, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)), 1L, 10L,
        )

        assertEquals(0, base.compareTo(base.copy()))

        // startTime
        assertTrue(base.compareTo(base.copy(startTime = LocalTime.of(9, 1))) < 0)
        assertTrue(base.copy(startTime = LocalTime.of(9, 1)).compareTo(base) > 0)

        // endTime
        assertTrue(base.compareTo(base.copy(endTime = LocalTime.of(10, 31))) < 0)
        assertTrue(base.copy(endTime = LocalTime.of(10, 31)).compareTo(base) > 0)

        // subjectName
        assertTrue(base.compareTo(base.copy(subjectName = "subject_")) < 0)
        assertTrue(base.copy(subjectName = "subject_").compareTo(base) > 0)

        // type
        assertTrue(base.compareTo(base.copy(type = "type_")) < 0)
        assertTrue(base.copy(type = "type_").compareTo(base) > 0)

        // teachers
        assertTrue(base.compareTo(base.copy(teachers = listOf("t_"))) < 0)
        assertTrue(base.copy(teachers = listOf("t_")).compareTo(base) > 0)

        // classrooms
        assertTrue(base.compareTo(base.copy(classrooms = listOf("c_"))) < 0)
        assertTrue(base.copy(classrooms = listOf("c_")).compareTo(base) > 0)

        // lessonRepeat
        assertTrue(
            base.compareTo(
                base.copy(
                    lessonRepeat = Lesson.Repeat.ByWeekday(
                        DayOfWeek.TUESDAY,
                        listOf(true),
                    ),
                ),
            ) < 0,
        )
        assertTrue(
            base.copy(lessonRepeat = Lesson.Repeat.ByWeekday(DayOfWeek.TUESDAY, listOf(true))).compareTo(base) > 0,
        )

        // id
        assertTrue(base.compareTo(base.copy(id = 11L)) < 0)
        assertTrue(base.copy(id = 11L).compareTo(base) > 0)

        // semesterId
        assertTrue(base.compareTo(base.copy(semesterId = 2L)) < 0)
        assertTrue(base.copy(semesterId = 2L).compareTo(base) > 0)
    }
}
