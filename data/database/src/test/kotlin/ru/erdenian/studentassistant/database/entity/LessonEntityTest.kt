package ru.erdenian.studentassistant.database.entity

import java.time.LocalTime
import org.junit.Assert.assertThrows
import org.junit.Test

internal class LessonEntityTest {

    @Test
    fun subjectNameTest() {
        assertThrows(IllegalArgumentException::class.java) {
            LessonEntity("", "", LocalTime.of(18, 40), LocalTime.of(19, 10), 1L)
        }
        assertThrows(IllegalArgumentException::class.java) {
            LessonEntity("   ", "", LocalTime.of(18, 40), LocalTime.of(19, 10), 1L)
        }
    }

    @Test
    fun typeTest() {
        assertThrows(IllegalArgumentException::class.java) {
            LessonEntity("name", "    ", LocalTime.of(18, 40), LocalTime.of(19, 10), 1L)
        }
    }

    @Test
    fun timeTest() {
        assertThrows(IllegalArgumentException::class.java) {
            LessonEntity("name", "", LocalTime.of(19, 10), LocalTime.of(18, 40), 1L)
        }
        assertThrows(IllegalArgumentException::class.java) {
            LessonEntity("name", "", LocalTime.of(18, 40), LocalTime.of(18, 40), 1L)
        }
    }
}
