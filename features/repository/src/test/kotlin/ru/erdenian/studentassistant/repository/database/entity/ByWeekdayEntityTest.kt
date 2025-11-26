package ru.erdenian.studentassistant.repository.database.entity

import java.time.DayOfWeek
import org.junit.Assert.assertThrows
import org.junit.Test

internal class ByWeekdayEntityTest {

    @Test
    fun byWeekdayTest() {
        assertThrows(IllegalArgumentException::class.java) {
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf(false))
        }
        assertThrows(IllegalArgumentException::class.java) {
            ByWeekdayEntity(DayOfWeek.MONDAY, listOf())
        }
        ByWeekdayEntity(DayOfWeek.MONDAY, listOf(true))
    }
}
