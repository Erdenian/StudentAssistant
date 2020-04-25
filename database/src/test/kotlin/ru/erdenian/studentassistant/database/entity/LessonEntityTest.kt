package ru.erdenian.studentassistant.database.entity

import org.joda.time.LocalTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class LessonEntityTest {

    @Test
    fun subjectNameTest() {
        assertThrows<IllegalArgumentException> {
            LessonEntity("", "", LocalTime(18, 40), LocalTime(19, 10), 1L)
        }
        assertThrows<IllegalArgumentException> {
            LessonEntity("   ", "", LocalTime(18, 40), LocalTime(19, 10), 1L)
        }
    }

    @Test
    fun typeTest() {
        assertThrows<IllegalArgumentException> {
            LessonEntity("name", "    ", LocalTime(18, 40), LocalTime(19, 10), 1L)
        }
    }

    @Test
    fun teachersTest() {
        assertThrows<IllegalArgumentException> {
            LessonEntity("name", "", LocalTime(18, 40), LocalTime(19, 10), 1L)
        }
        assertThrows<IllegalArgumentException> {
            LessonEntity("name", "", LocalTime(18, 40), LocalTime(19, 10), 1L)
        }
    }

    @Test
    fun classroomsTest() {
        assertThrows<IllegalArgumentException> {
            LessonEntity("name", "", LocalTime(18, 40), LocalTime(19, 10), 1L)
        }
        assertThrows<IllegalArgumentException> {
            LessonEntity("name", "", LocalTime(18, 40), LocalTime(19, 10), 1L)
        }
    }

    @Test
    fun timeTest() {
        assertThrows<IllegalArgumentException> {
            LessonEntity("name", "", LocalTime(19, 10), LocalTime(18, 40), 1L)
        }
        assertThrows<IllegalArgumentException> {
            LessonEntity("name", "", LocalTime(18, 40), LocalTime(18, 40), 1L)
        }
    }
}
