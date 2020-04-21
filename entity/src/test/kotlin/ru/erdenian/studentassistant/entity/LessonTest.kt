package ru.erdenian.studentassistant.entity

import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class LessonTest {

    private val lessonRepeat = LessonRepeat.ByDates(immutableSortedSetOf(LocalDate.now()))
    private val semesterId = 1L

    @Test
    fun subjectNameTest() {
        assertThrows<IllegalArgumentException> {
            Lesson(
                "", "",
                immutableSortedSetOf(), immutableSortedSetOf(),
                LocalTime(18, 40), LocalTime(19, 10),
                lessonRepeat, semesterId
            )
        }
        assertThrows<IllegalArgumentException> {
            Lesson(
                "   ", "",
                immutableSortedSetOf(), immutableSortedSetOf(),
                LocalTime(18, 40), LocalTime(19, 10),
                lessonRepeat, semesterId
            )
        }
    }

    @Test
    fun typeTest() {
        assertThrows<IllegalArgumentException> {
            Lesson(
                "name", "    ",
                immutableSortedSetOf(), immutableSortedSetOf(),
                LocalTime(18, 40), LocalTime(19, 10),
                lessonRepeat, semesterId
            )
        }
    }

    @Test
    fun teachersTest() {
        assertThrows<IllegalArgumentException> {
            Lesson(
                "name", "",
                immutableSortedSetOf(""), immutableSortedSetOf(),
                LocalTime(18, 40), LocalTime(19, 10),
                lessonRepeat, semesterId
            )
        }
        assertThrows<IllegalArgumentException> {
            Lesson(
                "name", "",
                immutableSortedSetOf("    "), immutableSortedSetOf(),
                LocalTime(18, 40), LocalTime(19, 10),
                lessonRepeat, semesterId
            )
        }
    }

    @Test
    fun classroomsTest() {
        assertThrows<IllegalArgumentException> {
            Lesson(
                "name", "",
                immutableSortedSetOf(), immutableSortedSetOf(""),
                LocalTime(18, 40), LocalTime(19, 10),
                lessonRepeat, semesterId
            )
        }
        assertThrows<IllegalArgumentException> {
            Lesson(
                "name", "",
                immutableSortedSetOf(), immutableSortedSetOf("    "),
                LocalTime(18, 40), LocalTime(19, 10),
                lessonRepeat, semesterId
            )
        }
    }

    @Test
    fun timeTest() {
        assertThrows<IllegalArgumentException> {
            Lesson(
                "name", "",
                immutableSortedSetOf(), immutableSortedSetOf(),
                LocalTime(19, 10), LocalTime(18, 40),
                lessonRepeat, semesterId
            )
        }
        assertThrows<IllegalArgumentException> {
            Lesson(
                "name", "",
                immutableSortedSetOf(), immutableSortedSetOf(),
                LocalTime(18, 40), LocalTime(18, 40),
                lessonRepeat, semesterId
            )
        }
    }
}
