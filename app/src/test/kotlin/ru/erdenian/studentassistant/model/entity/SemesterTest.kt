package ru.erdenian.studentassistant.model.entity

import org.joda.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.erdenian.studentassistant.entity.Semester

internal class SemesterTest {

    @Test
    fun nameTest() {
        assertThrows<IllegalArgumentException> {
            Semester("", LocalDate.now().minusDays(1), LocalDate.now())
        }
        assertThrows<IllegalArgumentException> {
            Semester("    ", LocalDate.now().minusDays(1), LocalDate.now())
        }
    }

    @Test
    fun datesTest() {
        assertThrows<IllegalArgumentException> {
            Semester("name", LocalDate.now(), LocalDate.now().minusDays(1))
        }
        assertThrows<IllegalArgumentException> {
            Semester("name", LocalDate.now(), LocalDate.now())
        }
    }

    @Test
    fun lengthTest() {
        assertEquals(
            11,
            Semester("name", LocalDate.now().minusDays(10), LocalDate.now()).length
        )
    }

    @Test
    fun getWeekNumberTest() {
        assertEquals(
            2,
            Semester(
                "name",
                LocalDate(2019, 7, 12),
                LocalDate(2019, 7, 31)
            ).getWeekNumber(LocalDate(2019, 7, 22))
        )
        assertEquals(
            2,
            Semester(
                "name",
                LocalDate(2019, 7, 12),
                LocalDate(2019, 7, 31)
            ).getWeekNumber(LocalDate(2019, 7, 28))
        )
    }
}
