package ru.erdenian.studentassistant.repository.entity

import org.joda.time.LocalDate
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SemesterTest {

    @BeforeEach
    fun setUp() {
        println("setup")
    }

    @Test
    fun test() {
        assertEquals(
            "name",
            Semester("name", LocalDate.now().minusDays(1), LocalDate.now()).name
        )
    }

    @AfterEach
    fun tearDown() {
        println("teardown")
    }
}
