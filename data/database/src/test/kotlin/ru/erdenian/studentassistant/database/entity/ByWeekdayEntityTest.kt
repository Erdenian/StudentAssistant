package ru.erdenian.studentassistant.database.entity

import java.time.DayOfWeek
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ByWeekdayEntityTest {

    @Test
    fun byWeekdayTest() {
        assertThrows<IllegalArgumentException> {
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(false), -1L)
        }
        assertThrows<IllegalArgumentException> {
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(), -1L)
        }
    }
}
