package ru.erdenian.studentassistant.repostory.api.entity

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.erdenian.studentassistant.repository.api.entity.Homework

class HomeworkTest {

    @Test
    fun compareToEqualsTest() {
        val homework1 = Homework(
            subjectName = "homework1",
            description = "description1",
            deadline = LocalDate.of(2023, 2, 13),
            isDone = false,
            semesterId = 1L,
            id = 10L,
        )
        val homework2 = Homework(
            subjectName = "homework1",
            description = "description1",
            deadline = LocalDate.of(2023, 2, 13),
            isDone = false,
            semesterId = 1L,
            id = 10L,
        )
        assertEquals(0, homework1.compareTo(homework2))
    }

    @Test
    fun compareToIsDoneTest() {
        val homework1 = Homework(
            subjectName = "homework1",
            description = "description1",
            deadline = LocalDate.of(2023, 2, 13),
            isDone = false,
            semesterId = 1L,
            id = 10L,
        )
        val homework2 = Homework(
            subjectName = "homework1",
            description = "description1",
            deadline = LocalDate.of(2023, 2, 13),
            isDone = true,
            semesterId = 1L,
            id = 10L,
        )
        assertEquals(-1, homework1.compareTo(homework2))
        assertEquals(1, homework2.compareTo(homework1))
    }

    @Test
    fun compareToDeadlineTest() {
        val homework1 = Homework(
            subjectName = "homework1",
            description = "description1",
            deadline = LocalDate.of(2023, 2, 13),
            isDone = false,
            semesterId = 1L,
            id = 10L,
        )
        val homework2 = Homework(
            subjectName = "homework1",
            description = "description1",
            deadline = LocalDate.of(2023, 2, 14),
            isDone = false,
            semesterId = 1L,
            id = 10L,
        )
        assertEquals(-1, homework1.compareTo(homework2))
        assertEquals(1, homework2.compareTo(homework1))
    }

    @Test
    fun compareToSubjectNameTest() {
        val homework1 = Homework(
            subjectName = "homework1",
            description = "description1",
            deadline = LocalDate.of(2023, 2, 13),
            isDone = false,
            semesterId = 1L,
            id = 10L,
        )
        val homework2 = Homework(
            subjectName = "homework2",
            description = "description1",
            deadline = LocalDate.of(2023, 2, 13),
            isDone = false,
            semesterId = 1L,
            id = 10L,
        )
        assertEquals(-1, homework1.compareTo(homework2))
        assertEquals(1, homework2.compareTo(homework1))
    }

    @Test
    fun compareToDescriptionTest() {
        val homework1 = Homework(
            subjectName = "homework1",
            description = "description1",
            deadline = LocalDate.of(2023, 2, 13),
            isDone = false,
            semesterId = 1L,
            id = 10L,
        )
        val homework2 = Homework(
            subjectName = "homework1",
            description = "description2",
            deadline = LocalDate.of(2023, 2, 13),
            isDone = false,
            semesterId = 1L,
            id = 10L,
        )
        assertEquals(-1, homework1.compareTo(homework2))
        assertEquals(1, homework2.compareTo(homework1))
    }

    @Test
    fun compareToIdTest() {
        val homework1 = Homework(
            subjectName = "homework1",
            description = "description1",
            deadline = LocalDate.of(2023, 2, 13),
            isDone = false,
            semesterId = 1L,
            id = 10L,
        )
        val homework2 = Homework(
            subjectName = "homework1",
            description = "description1",
            deadline = LocalDate.of(2023, 2, 13),
            isDone = false,
            semesterId = 1L,
            id = 11L,
        )
        assertEquals(-1, homework1.compareTo(homework2))
        assertEquals(1, homework2.compareTo(homework1))
    }

    @Test
    fun compareToSemesterIdTest() {
        val homework1 = Homework(
            subjectName = "homework1",
            description = "description1",
            deadline = LocalDate.of(2023, 2, 13),
            isDone = false,
            semesterId = 1L,
            id = 10L,
        )
        val homework2 = Homework(
            subjectName = "homework1",
            description = "description1",
            deadline = LocalDate.of(2023, 2, 13),
            isDone = false,
            semesterId = 2L,
            id = 10L,
        )
        assertEquals(-1, homework1.compareTo(homework2))
        assertEquals(1, homework2.compareTo(homework1))
    }
}
