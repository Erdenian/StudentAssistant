package ru.erdenian.studentassistant.schedule

import org.joda.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeworkTest {

    val homework = Homework("aaa", "bbb", LocalDate.now())

    @Test
    fun getSubjectName() {
        assertEquals("aaa", homework.subjectName)
    }

    @Test
    fun getDescription() {
        assertEquals("bbb", homework.description)
    }

    @Test
    fun getDeadline() {
        assertEquals(LocalDate.now(), homework.deadline)
    }

    @Test
    fun getId() {
        assertEquals(-1, homework.id)
    }

    @Test(expected = IllegalArgumentException::class)
    fun illegalNameTest() {
        Homework("", "s", LocalDate.now())
    }

    @Test(expected = IllegalArgumentException::class)
    fun illegalDescriptionTest() {
        Homework("sss", " ", LocalDate.now())
    }
}