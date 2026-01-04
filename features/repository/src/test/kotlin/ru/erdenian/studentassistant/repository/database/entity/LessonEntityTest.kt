package ru.erdenian.studentassistant.repository.database.entity

import java.time.LocalTime
import org.junit.Assert.assertThrows
import org.junit.Test

internal class LessonEntityTest {

    @Test
    fun subjectNameTest() {
        assertThrows(IllegalArgumentException::class.java) {
            LessonEntity(
                subjectName = "",
                type = "type",
                startTime = LocalTime.of(18, 40),
                endTime = LocalTime.of(19, 10),
                semesterId = 1L,
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            LessonEntity(
                subjectName = "   ",
                type = "type",
                startTime = LocalTime.of(18, 40),
                endTime = LocalTime.of(19, 10),
                semesterId = 1L,
            )
        }
    }

    @Test
    fun typeTest() {
        assertThrows(IllegalArgumentException::class.java) {
            LessonEntity(
                subjectName = "name",
                type = "",
                startTime = LocalTime.of(18, 40),
                endTime = LocalTime.of(19, 10),
                semesterId = 1L,
            )
            LessonEntity(
                subjectName = "name",
                type = "    ",
                startTime = LocalTime.of(18, 40),
                endTime = LocalTime.of(19, 10),
                semesterId = 1L,
            )
        }
    }

    @Test
    fun timeTest() {
        assertThrows(IllegalArgumentException::class.java) {
            LessonEntity(
                subjectName = "name",
                type = "type",
                startTime = LocalTime.of(19, 10),
                endTime = LocalTime.of(18, 40),
                semesterId = 1L,
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            LessonEntity(
                subjectName = "name",
                type = "type",
                startTime = LocalTime.of(18, 40),
                endTime = LocalTime.of(18, 40),
                semesterId = 1L,
            )
        }
    }

    @Test
    fun successfulTest() {
        LessonEntity(
            subjectName = "name",
            type = "type",
            startTime = LocalTime.of(18, 40),
            endTime = LocalTime.of(19, 10),
            semesterId = 1L,
        )
    }
}
