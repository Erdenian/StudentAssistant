package ru.erdenian.studentassistant.database.entity

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ByWeekdayEntityTest {

    @Test
    fun byWeekdayTest() {
        assertThrows<IllegalArgumentException> {
            ByWeekdayEntity(100, listOf(true), -1L)
        }
        assertThrows<IllegalArgumentException> {
            ByWeekdayEntity(0, listOf(true), -1L)
        }
        assertThrows<IllegalArgumentException> {
            ByWeekdayEntity(1, listOf(false), -1L)
        }
        assertThrows<IllegalArgumentException> {
            ByWeekdayEntity(1, listOf(), -1L)
        }
    }
}
