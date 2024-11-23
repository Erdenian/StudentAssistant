package com.erdenian.studentassistant.repostory.api.entity

import com.erdenian.studentassistant.repository.api.entity.Semester
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class SemesterTest {

    @Test
    fun lengthTest() {
        assertEquals(1, Semester("semester1", LocalDate.of(2023, 2, 13), LocalDate.of(2023, 2, 13), 1L).length)
        assertEquals(2, Semester("semester1", LocalDate.of(2023, 2, 13), LocalDate.of(2023, 2, 14), 1L).length)
        assertEquals(365, Semester("semester1", LocalDate.of(2023, 2, 13), LocalDate.of(2024, 2, 12), 1L).length)
    }

    @Test
    fun getWeekNumberTest() {
        val semester1 = Semester("semester1", LocalDate.of(2023, 2, 13), LocalDate.of(2024, 2, 12), 1L)
        assertEquals(-1, semester1.getWeekNumber(LocalDate.of(2023, 2, 12)))
        assertEquals(0, semester1.getWeekNumber(LocalDate.of(2023, 2, 13)))
        assertEquals(0, semester1.getWeekNumber(LocalDate.of(2023, 2, 19)))
        assertEquals(1, semester1.getWeekNumber(LocalDate.of(2023, 2, 20)))
        assertEquals(1, semester1.getWeekNumber(LocalDate.of(2023, 2, 26)))

        val semester2 = Semester("semester2", LocalDate.of(2023, 2, 15), LocalDate.of(2024, 2, 14), 1L)
        assertEquals(-1, semester2.getWeekNumber(LocalDate.of(2023, 2, 12)))
        assertEquals(0, semester2.getWeekNumber(LocalDate.of(2023, 2, 13)))
        assertEquals(0, semester2.getWeekNumber(LocalDate.of(2023, 2, 19)))
        assertEquals(1, semester2.getWeekNumber(LocalDate.of(2023, 2, 20)))
        assertEquals(1, semester2.getWeekNumber(LocalDate.of(2023, 2, 26)))
    }

    @Test
    fun compareToEqualsTest() {
        val semester1 = Semester("semester1", LocalDate.of(2023, 2, 13), LocalDate.of(2024, 2, 13), 1L)
        val semester2 = Semester("semester1", LocalDate.of(2023, 2, 13), LocalDate.of(2024, 2, 13), 1L)
        assertEquals(0, semester1.compareTo(semester2))
    }

    @Test
    fun compareToLastDayTest() {
        val semester1 = Semester("semester1", LocalDate.of(2023, 2, 13), LocalDate.of(2024, 2, 13), 1L)
        val semester2 = Semester("semester1", LocalDate.of(2023, 2, 13), LocalDate.of(2024, 2, 14), 1L)
        assertEquals(-1, semester1.compareTo(semester2))
        assertEquals(1, semester2.compareTo(semester1))
    }

    @Test
    fun compareToFirstDayTest() {
        val semester1 = Semester("semester1", LocalDate.of(2023, 2, 13), LocalDate.of(2024, 2, 13), 1L)
        val semester2 = Semester("semester1", LocalDate.of(2023, 2, 14), LocalDate.of(2024, 2, 13), 1L)
        assertEquals(-1, semester1.compareTo(semester2))
        assertEquals(1, semester2.compareTo(semester1))
    }

    @Test
    fun compareToSubjectNameTest() {
        val semester1 = Semester("semester1", LocalDate.of(2023, 2, 13), LocalDate.of(2024, 2, 13), 1L)
        val semester2 = Semester("semester2", LocalDate.of(2023, 2, 13), LocalDate.of(2024, 2, 13), 1L)
        assertEquals(-1, semester1.compareTo(semester2))
        assertEquals(1, semester2.compareTo(semester1))
    }

    @Test
    fun compareToIdTest() {
        val semester1 = Semester("semester1", LocalDate.of(2023, 2, 13), LocalDate.of(2024, 2, 13), 1L)
        val semester2 = Semester("semester1", LocalDate.of(2023, 2, 13), LocalDate.of(2024, 2, 13), 2L)
        assertEquals(-1, semester1.compareTo(semester2))
        assertEquals(1, semester2.compareTo(semester1))
    }
}
