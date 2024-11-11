package com.erdenian.studentassistant.repostory.api.entity

import com.erdenian.studentassistant.repository.api.entity.Lesson
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test

class LessonTest {

    @Test
    fun byWeekdayTest() {
        val repeat = Lesson.Repeat.ByWeekday(DayOfWeek.THURSDAY, listOf(true, false, true))

        assertEquals(true, repeat.repeatsOnDay(LocalDate.of(2023, 2, 16), 0))
        assertEquals(false, repeat.repeatsOnDay(LocalDate.of(2023, 2, 16), 1))
        assertEquals(true, repeat.repeatsOnDay(LocalDate.of(2023, 2, 16), 2))
        assertEquals(false, repeat.repeatsOnDay(LocalDate.of(2023, 2, 17), 0))

        assertEquals(false, repeat.repeatsOnDayOfWeek(DayOfWeek.WEDNESDAY))
        assertEquals(true, repeat.repeatsOnDayOfWeek(DayOfWeek.THURSDAY))
        assertEquals(false, repeat.repeatsOnDayOfWeek(DayOfWeek.FRIDAY))
    }

    @Test
    fun byDatesTest() {
        val repeat = Lesson.Repeat.ByDates(setOf(LocalDate.of(2023, 2, 16)))

        assertEquals(true, repeat.repeatsOnDay(LocalDate.of(2023, 2, 16), 0))
        assertEquals(true, repeat.repeatsOnDay(LocalDate.of(2023, 2, 16), 1))
        assertEquals(true, repeat.repeatsOnDay(LocalDate.of(2023, 2, 16), 0))
        assertEquals(false, repeat.repeatsOnDay(LocalDate.of(2023, 2, 17), 0))
        assertEquals(false, repeat.repeatsOnDay(LocalDate.of(2023, 2, 15), 0))

        assertEquals(true, repeat.repeatsOnDate(LocalDate.of(2023, 2, 16)))
        assertEquals(false, repeat.repeatsOnDate(LocalDate.of(2023, 2, 17)))
        assertEquals(false, repeat.repeatsOnDate(LocalDate.of(2023, 2, 15)))
    }

    @Test
    fun compareToEqualsTest() {
        val lesson1 = Lesson(
            "subjectName1",
            "type1",
            listOf("teacher1"),
            listOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        val lesson2 = Lesson(
            "subjectName1",
            "type1",
            listOf("teacher1"),
            listOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        assertEquals(0, lesson1.compareTo(lesson2))
    }

    @Test
    fun compareToStartTimeTest() {
        val lesson1 = Lesson(
            "subjectName1",
            "type1",
            listOf("teacher1"),
            listOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        val lesson2 = Lesson(
            "subjectName1",
            "type1",
            listOf("teacher1"),
            listOf("classroom1"),
            LocalTime.of(10, 0),
            LocalTime.of(10, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        assertEquals(-1, lesson1.compareTo(lesson2))
        assertEquals(1, lesson2.compareTo(lesson1))
    }

    @Test
    fun compareToEndTimeTest() {
        val lesson1 = Lesson(
            "subjectName1",
            "type1",
            listOf("teacher1"),
            listOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        val lesson2 = Lesson(
            "subjectName1",
            "type1",
            listOf("teacher1"),
            listOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(11, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        assertEquals(-1, lesson1.compareTo(lesson2))
        assertEquals(1, lesson2.compareTo(lesson1))
    }

    @Test
    fun compareToSubjectNameTest() {
        val lesson1 = Lesson(
            "subjectName1",
            "type1",
            listOf("teacher1"),
            listOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        val lesson2 = Lesson(
            "subjectName2",
            "type1",
            listOf("teacher1"),
            listOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        assertEquals(-1, lesson1.compareTo(lesson2))
        assertEquals(1, lesson2.compareTo(lesson1))
    }

    @Test
    fun compareToTypeTest() {
        val lesson1 = Lesson(
            "subjectName1",
            "type1",
            listOf("teacher1"),
            listOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        val lesson2 = Lesson(
            "subjectName1",
            "type2",
            listOf("teacher1"),
            listOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        assertEquals(-1, lesson1.compareTo(lesson2))
        assertEquals(1, lesson2.compareTo(lesson1))
    }

    @Test
    fun compareToIdTest() {
        val lesson1 = Lesson(
            "subjectName1",
            "type1",
            listOf("teacher1"),
            listOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)),
            1L,
            1L,
        )
        val lesson2 = Lesson(
            "subjectName1",
            "type1",
            listOf("teacher1"),
            listOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)),
            1L,
            11L,
        )
        assertEquals(-1, lesson1.compareTo(lesson2))
        assertEquals(1, lesson2.compareTo(lesson1))
    }

    @Test
    fun compareToSemesterIdTest() {
        val lesson1 = Lesson(
            "subjectName1",
            "type1",
            listOf("teacher1"),
            listOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)),
            1L,
            10L,
        )
        val lesson2 = Lesson(
            "subjectName1",
            "type1",
            listOf("teacher1"),
            listOf("classroom1"),
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            Lesson.Repeat.ByWeekday(DayOfWeek.MONDAY, listOf(true)),
            2L,
            10L,
        )
        assertEquals(-1, lesson1.compareTo(lesson2))
        assertEquals(1, lesson2.compareTo(lesson1))
    }
}
