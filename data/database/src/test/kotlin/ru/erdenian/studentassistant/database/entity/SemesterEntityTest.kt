package ru.erdenian.studentassistant.database.entity

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

internal class SemesterEntityTest {

    @Test
    fun nameTest() {
        assertThrows(IllegalArgumentException::class.java) {
            SemesterEntity("", LocalDate.now().minusDays(1), LocalDate.now())
        }
        assertThrows(IllegalArgumentException::class.java) {
            SemesterEntity("    ", LocalDate.now().minusDays(1), LocalDate.now())
        }
    }

    @Test
    fun datesTest() {
        assertThrows(IllegalArgumentException::class.java) {
            SemesterEntity("name", LocalDate.now(), LocalDate.now().minusDays(1))
        }
        assertThrows(IllegalArgumentException::class.java) {
            SemesterEntity("name", LocalDate.now(), LocalDate.now())
        }
    }

    @Test
    fun lengthTest() {
        assertEquals(
            11,
            SemesterEntity("name", LocalDate.now().minusDays(10), LocalDate.now()).length
        )
    }

    @Test
    fun getWeekNumberTest() {
        assertEquals(
            2,
            SemesterEntity(
                "name",
                LocalDate.of(2019, 7, 12),
                LocalDate.of(2019, 7, 31)
            ).getWeekNumber(LocalDate.of(2019, 7, 22))
        )
        assertEquals(
            2,
            SemesterEntity(
                "name",
                LocalDate.of(2019, 7, 12),
                LocalDate.of(2019, 7, 31)
            ).getWeekNumber(LocalDate.of(2019, 7, 28))
        )
    }
}
