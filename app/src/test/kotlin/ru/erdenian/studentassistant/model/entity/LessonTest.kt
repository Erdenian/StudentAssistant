package ru.erdenian.studentassistant.model.entity

import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.entity.LessonRepeat
import ru.erdenian.studentassistant.entity.immutableSortedSetOf

internal class LessonTest {

    private val lessonRepeat = LessonRepeat.ByDates(immutableSortedSetOf(LocalDate.now()))
    private val semesterId = 1L

    @Test
    fun subjectNameTest() {
        assertThrows<IllegalArgumentException> {
            Lesson(
                "",
                startTime = LocalTime(18, 40),
                endTime = LocalTime(19, 10),
                lessonRepeat = lessonRepeat,
                semesterId = semesterId,
                id = 1L
            )
        }
        assertThrows<IllegalArgumentException> {
            Lesson(
                "   ",
                startTime = LocalTime(18, 40),
                endTime = LocalTime(19, 10),
                lessonRepeat = lessonRepeat,
                semesterId = semesterId,
                id = 1L
            )
        }
    }

    @Test
    fun typeTest() {
        assertThrows<IllegalArgumentException> {
            Lesson(
                "name",
                type = "",
                startTime = LocalTime(18, 40),
                endTime = LocalTime(19, 10),
                lessonRepeat = lessonRepeat,
                semesterId = semesterId,
                id = 1L
            )
        }
        assertThrows<IllegalArgumentException> {
            Lesson(
                "name",
                type = "    ",
                startTime = LocalTime(18, 40),
                endTime = LocalTime(19, 10),
                lessonRepeat = lessonRepeat,
                semesterId = semesterId,
                id = 1L
            )
        }
    }

    @Test
    fun teachersTest() {
        assertThrows<IllegalArgumentException> {
            Lesson(
                "name",
                teachers = immutableSortedSetOf(""),
                startTime = LocalTime(18, 40),
                endTime = LocalTime(19, 10),
                lessonRepeat = lessonRepeat,
                semesterId = semesterId,
                id = 1L
            )
        }
        assertThrows<IllegalArgumentException> {
            Lesson(
                "name",
                teachers = immutableSortedSetOf("    "),
                startTime = LocalTime(18, 40),
                endTime = LocalTime(19, 10),
                lessonRepeat = lessonRepeat,
                semesterId = semesterId,
                id = 1L
            )
        }
    }

    @Test
    fun classroomsTest() {
        assertThrows<IllegalArgumentException> {
            Lesson(
                "name",
                classrooms = immutableSortedSetOf(""),
                startTime = LocalTime(18, 40),
                endTime = LocalTime(19, 10),
                lessonRepeat = lessonRepeat,
                semesterId = semesterId,
                id = 1L
            )
        }
        assertThrows<IllegalArgumentException> {
            Lesson(
                "name",
                classrooms = immutableSortedSetOf("    "),
                startTime = LocalTime(18, 40),
                endTime = LocalTime(19, 10),
                lessonRepeat = lessonRepeat,
                semesterId = semesterId,
                id = 1L
            )
        }
    }

    @Test
    fun timeTest() {
        assertThrows<IllegalArgumentException> {
            Lesson(
                "name",
                startTime = LocalTime(19, 10),
                endTime = LocalTime(18, 40),
                lessonRepeat = lessonRepeat,
                semesterId = semesterId,
                id = 1L
            )
        }
        assertThrows<IllegalArgumentException> {
            Lesson(
                "name",
                startTime = LocalTime(18, 40),
                endTime = LocalTime(18, 40),
                lessonRepeat = lessonRepeat,
                semesterId = semesterId,
                id = 1L
            )
        }
    }
}
