package com.erdenian.studentassistant.database

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test

@Suppress("MagicNumber")
internal class ConvertersTest {

    private val converters = Converters

    @Test
    fun dayOfWeekTest() {
        val original = DayOfWeek.entries + null
        val converted = original.map { converters.dayOfWeekToInt(it) }
        val restored = converted.map { converters.intToDayOfWeek(it) }

        assertEquals(original, restored)
        assertEquals(converted, (1..7).toList() + null)
    }

    @Test
    fun localDateTest() {
        val original = listOf(LocalDate.MIN, LocalDate.MAX, LocalDate.of(2023, 1, 1), null)
        val converted = original.map { converters.localDateToLong(it) }
        val restored = converted.map { converters.longToLocalDate(it) }

        assertEquals(original, restored)
        assertEquals(converted, listOf(-365_243_219_162L, 365_241_780_471L, 19_358L, null))
    }

    @Test
    fun localTimeTest() {
        val original = listOf(LocalTime.MIN, LocalTime.MAX, LocalTime.of(23, 25, 35, 69), null)
        val converted = original.map { converters.localTimeToInt(it) }
        val restored = converted.map { converters.intToLocalTime(it) }

        assertEquals(original, restored)
        assertEquals(converted, listOf(0L, 86_399_999_999_999L, 84_335_000_000_069L, null))
    }

    @Test
    fun booleanListTest() {
        val original = listOf(
            emptyList(), listOf(true), listOf(false), listOf(true, false), listOf(true, false, true), null,
        )
        val converted = original.map { converters.booleanListToString(it) }
        val restored = converted.map { converters.stringToBooleanList(it) }

        assertEquals(original, restored)
        assertEquals(converted, listOf("", "1", "0", "10", "101", null))
    }
}
