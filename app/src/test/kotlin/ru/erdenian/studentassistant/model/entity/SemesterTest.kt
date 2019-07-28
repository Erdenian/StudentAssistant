package ru.erdenian.studentassistant.model.entity

import org.joda.time.LocalDate
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class SemesterTest {

    @Test
    fun nameTest() {
        assertThrows<IllegalArgumentException> {
            Semester("", LocalDate.now().minusDays(1), LocalDate.now())
        }
    }
}
