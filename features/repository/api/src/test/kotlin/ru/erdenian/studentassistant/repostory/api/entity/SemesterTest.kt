package ru.erdenian.studentassistant.repostory.api.entity

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.erdenian.studentassistant.repository.api.entity.Semester

class SemesterTest {

    @Test
    fun lengthTest() {
        assertEquals(
            1,
            Semester(
                name = "semester1",
                firstDay = LocalDate.of(2023, 2, 13),
                lastDay = LocalDate.of(2023, 2, 13),
                id = 1L,
            ).length,
        )
        assertEquals(
            2,
            Semester(
                name = "semester1",
                firstDay = LocalDate.of(2023, 2, 13),
                lastDay = LocalDate.of(2023, 2, 14),
                id = 1L,
            ).length,
        )
        assertEquals(
            365,
            Semester(
                name = "semester1",
                firstDay = LocalDate.of(2023, 2, 13),
                lastDay = LocalDate.of(2024, 2, 12),
                id = 1L,
            ).length,
        )
    }

    @Test
    fun dateRangeTest() {
        val start = LocalDate.of(2023, 2, 13)
        val end = LocalDate.of(2023, 5, 31)
        val semester = Semester(name = "semester1", firstDay = start, lastDay = end, id = 1L)

        assertEquals(start..end, semester.dateRange)
    }

    @Test
    fun getWeekNumberTest() {
        val semester1 = Semester(
            name = "semester1",
            firstDay = LocalDate.of(2023, 2, 13),
            lastDay = LocalDate.of(2024, 2, 12),
            id = 1L,
        )
        assertEquals(-1, semester1.getWeekNumber(LocalDate.of(2023, 2, 12)))
        assertEquals(0, semester1.getWeekNumber(LocalDate.of(2023, 2, 13)))
        assertEquals(0, semester1.getWeekNumber(LocalDate.of(2023, 2, 19)))
        assertEquals(1, semester1.getWeekNumber(LocalDate.of(2023, 2, 20)))
        assertEquals(1, semester1.getWeekNumber(LocalDate.of(2023, 2, 26)))

        val semester2 = Semester(
            name = "semester2",
            firstDay = LocalDate.of(2023, 2, 15),
            lastDay = LocalDate.of(2024, 2, 14),
            id = 1L,
        )
        assertEquals(-1, semester2.getWeekNumber(LocalDate.of(2023, 2, 12)))
        assertEquals(0, semester2.getWeekNumber(LocalDate.of(2023, 2, 13)))
        assertEquals(0, semester2.getWeekNumber(LocalDate.of(2023, 2, 19)))
        assertEquals(1, semester2.getWeekNumber(LocalDate.of(2023, 2, 20)))
        assertEquals(1, semester2.getWeekNumber(LocalDate.of(2023, 2, 26)))
    }

    @Test
    fun compareToEqualsTest() {
        val semester1 = Semester(
            name = "semester1",
            firstDay = LocalDate.of(2023, 2, 13),
            lastDay = LocalDate.of(2024, 2, 13),
            id = 1L,
        )
        val semester2 = Semester(
            name = "semester1",
            firstDay = LocalDate.of(2023, 2, 13),
            lastDay = LocalDate.of(2024, 2, 13),
            id = 1L,
        )
        assertEquals(0, semester1.compareTo(semester2))
    }

    @Test
    fun compareToLastDayTest() {
        val semester1 = Semester(
            name = "semester1",
            firstDay = LocalDate.of(2023, 2, 13),
            lastDay = LocalDate.of(2024, 2, 13),
            id = 1L,
        )
        val semester2 = Semester(
            name = "semester1",
            firstDay = LocalDate.of(2023, 2, 13),
            lastDay = LocalDate.of(2024, 2, 14),
            id = 1L,
        )
        assertEquals(-1, semester1.compareTo(semester2))
        assertEquals(1, semester2.compareTo(semester1))
    }

    @Test
    fun compareToFirstDayTest() {
        val semester1 = Semester(
            name = "semester1",
            firstDay = LocalDate.of(2023, 2, 13),
            lastDay = LocalDate.of(2024, 2, 13),
            id = 1L,
        )
        val semester2 = Semester(
            name = "semester1",
            firstDay = LocalDate.of(2023, 2, 14),
            lastDay = LocalDate.of(2024, 2, 13),
            id = 1L,
        )
        assertEquals(-1, semester1.compareTo(semester2))
        assertEquals(1, semester2.compareTo(semester1))
    }

    @Test
    fun compareToSubjectNameTest() {
        val semester1 = Semester(
            name = "semester1",
            firstDay = LocalDate.of(2023, 2, 13),
            lastDay = LocalDate.of(2024, 2, 13),
            id = 1L,
        )
        val semester2 = Semester(
            name = "semester2",
            firstDay = LocalDate.of(2023, 2, 13),
            lastDay = LocalDate.of(2024, 2, 13),
            id = 1L,
        )
        assertEquals(-1, semester1.compareTo(semester2))
        assertEquals(1, semester2.compareTo(semester1))
    }

    @Test
    fun compareToIdTest() {
        val semester1 = Semester(
            name = "semester1",
            firstDay = LocalDate.of(2023, 2, 13),
            lastDay = LocalDate.of(2024, 2, 13),
            id = 1L,
        )
        val semester2 = Semester(
            name = "semester1",
            firstDay = LocalDate.of(2023, 2, 13),
            lastDay = LocalDate.of(2024, 2, 13),
            id = 2L,
        )
        assertEquals(-1, semester1.compareTo(semester2))
        assertEquals(1, semester2.compareTo(semester1))
    }
}
