package ru.erdenian.studentassistant.model.entity

import org.joda.time.LocalDate
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class HomeworkTest {

    private val semesterId = 1L

    @Test
    fun subjectNameTest() {
        assertThrows<IllegalArgumentException> {
            Homework(
                "",
                "description",
                LocalDate.now(),
                semesterId
            )
        }
        assertThrows<IllegalArgumentException> {
            Homework(
                "    ",
                "description",
                LocalDate.now(),
                semesterId
            )
        }
    }

    @Test
    fun descriptionTest() {
        assertThrows<IllegalArgumentException> {
            Homework(
                "name",
                "",
                LocalDate.now(),
                semesterId
            )
        }
        assertThrows<IllegalArgumentException> {
            Homework(
                "name",
                "     ",
                LocalDate.now(),
                semesterId
            )
        }
    }
}
