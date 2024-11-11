package com.erdenian.studentassistant.repository.database.entity

import java.time.LocalDate
import org.junit.Assert.assertThrows
import org.junit.Test

internal class SemesterEntityTest {

    @Test
    fun nameTest() {
        assertThrows(IllegalArgumentException::class.java) {
            SemesterEntity("", LocalDate.of(2023, 2, 14), LocalDate.of(2023, 2, 15))
        }
        assertThrows(IllegalArgumentException::class.java) {
            SemesterEntity("    ", LocalDate.of(2023, 2, 14), LocalDate.of(2023, 2, 15))
        }
    }

    @Test
    fun datesTest() {
        assertThrows(IllegalArgumentException::class.java) {
            SemesterEntity("name", LocalDate.of(2023, 2, 15), LocalDate.of(2023, 2, 14))
        }
        SemesterEntity("name", LocalDate.of(2023, 2, 15), LocalDate.of(2023, 2, 15))
    }
}
