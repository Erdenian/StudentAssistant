package ru.erdenian.studentassistant.model.entity

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.erdenian.studentassistant.model.immutableSortedSetOf

internal class LessonRepeatTest {

    @Test
    fun byWeekdayTest() {
        assertThrows<IllegalArgumentException> {
            LessonRepeat.ByWeekday(100, listOf(true))
        }
        assertThrows<IllegalArgumentException> {
            LessonRepeat.ByWeekday(0, listOf(true))
        }
        assertThrows<IllegalArgumentException> {
            LessonRepeat.ByWeekday(1, listOf(false))
        }
        assertThrows<IllegalArgumentException> {
            LessonRepeat.ByWeekday(1, listOf())
        }
    }

    @Test
    fun byDatesTest() {
        assertThrows<IllegalArgumentException> {
            LessonRepeat.ByDates(immutableSortedSetOf())
        }
    }
}
