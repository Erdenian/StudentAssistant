package ru.erdenian.studentassistant.database.entity

import java.time.LocalTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class LessonEntityTest {

    @Test
    fun subjectNameTest() {
        assertThrows<IllegalArgumentException> {
            LessonEntity("", "", LocalTime.of(18, 40), LocalTime.of(19, 10), 1L)
        }
        assertThrows<IllegalArgumentException> {
            LessonEntity("   ", "", LocalTime.of(18, 40), LocalTime.of(19, 10), 1L)
        }
    }

    @Test
    fun typeTest() {
        assertThrows<IllegalArgumentException> {
            LessonEntity("name", "    ", LocalTime.of(18, 40), LocalTime.of(19, 10), 1L)
        }
    }

    @Test
    fun timeTest() {
        assertThrows<IllegalArgumentException> {
            LessonEntity("name", "", LocalTime.of(19, 10), LocalTime.of(18, 40), 1L)
        }
        assertThrows<IllegalArgumentException> {
            LessonEntity("name", "", LocalTime.of(18, 40), LocalTime.of(18, 40), 1L)
        }
    }
}
