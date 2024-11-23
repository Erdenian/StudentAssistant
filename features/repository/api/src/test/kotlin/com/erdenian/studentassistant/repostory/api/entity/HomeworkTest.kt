package com.erdenian.studentassistant.repostory.api.entity

import com.erdenian.studentassistant.repository.api.entity.Homework
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeworkTest {

    @Test
    fun compareToEqualsTest() {
        val homework1 = Homework("homework1", "description1", LocalDate.of(2023, 2, 13), false, 1L, 10L)
        val homework2 = Homework("homework1", "description1", LocalDate.of(2023, 2, 13), false, 1L, 10L)
        assertEquals(0, homework1.compareTo(homework2))
    }

    @Test
    fun compareToIsDoneTest() {
        val homework1 = Homework("homework1", "description1", LocalDate.of(2023, 2, 13), false, 1L, 10L)
        val homework2 = Homework("homework1", "description1", LocalDate.of(2023, 2, 13), true, 1L, 10L)
        assertEquals(-1, homework1.compareTo(homework2))
        assertEquals(1, homework2.compareTo(homework1))
    }

    @Test
    fun compareToDeadlineTest() {
        val homework1 = Homework("homework1", "description1", LocalDate.of(2023, 2, 13), false, 1L, 10L)
        val homework2 = Homework("homework1", "description1", LocalDate.of(2023, 2, 14), false, 1L, 10L)
        assertEquals(-1, homework1.compareTo(homework2))
        assertEquals(1, homework2.compareTo(homework1))
    }

    @Test
    fun compareToSubjectNameTest() {
        val homework1 = Homework("homework1", "description1", LocalDate.of(2023, 2, 13), false, 1L, 10L)
        val homework2 = Homework("homework2", "description1", LocalDate.of(2023, 2, 13), false, 1L, 10L)
        assertEquals(-1, homework1.compareTo(homework2))
        assertEquals(1, homework2.compareTo(homework1))
    }

    @Test
    fun compareToDescriptionTest() {
        val homework1 = Homework("homework1", "description1", LocalDate.of(2023, 2, 13), false, 1L, 10L)
        val homework2 = Homework("homework1", "description2", LocalDate.of(2023, 2, 13), false, 1L, 10L)
        assertEquals(-1, homework1.compareTo(homework2))
        assertEquals(1, homework2.compareTo(homework1))
    }

    @Test
    fun compareToIdTest() {
        val homework1 = Homework("homework1", "description1", LocalDate.of(2023, 2, 13), false, 1L, 10L)
        val homework2 = Homework("homework1", "description1", LocalDate.of(2023, 2, 13), false, 1L, 11L)
        assertEquals(-1, homework1.compareTo(homework2))
        assertEquals(1, homework2.compareTo(homework1))
    }

    @Test
    fun compareToSemesterIdTest() {
        val homework1 = Homework("homework1", "description1", LocalDate.of(2023, 2, 13), false, 1L, 10L)
        val homework2 = Homework("homework1", "description1", LocalDate.of(2023, 2, 13), false, 2L, 10L)
        assertEquals(-1, homework1.compareTo(homework2))
        assertEquals(1, homework2.compareTo(homework1))
    }
}
