package ru.erdenian.studentassistant.database.entity

import java.time.DayOfWeek
import org.junit.Assert.assertThrows
import org.junit.Test

internal class ByWeekdayEntityTest {

    @Test
    fun byWeekdayTest() {
        assertThrows(IllegalArgumentException::class.java) {
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(false), -1L)
        }
        assertThrows(IllegalArgumentException::class.java) {
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(), -1L)
        }
    }
}
