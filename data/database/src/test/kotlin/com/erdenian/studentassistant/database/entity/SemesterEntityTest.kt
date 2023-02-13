package com.erdenian.studentassistant.database.entity

import java.time.LocalDate
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
        SemesterEntity("name", LocalDate.now(), LocalDate.now())
    }
}
