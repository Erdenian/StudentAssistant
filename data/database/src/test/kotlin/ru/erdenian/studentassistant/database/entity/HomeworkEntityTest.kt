package ru.erdenian.studentassistant.database.entity

import java.time.LocalDate
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class HomeworkEntityTest {

    private val semesterId = 1L

    @Test
    fun subjectNameTest() {
        assertThrows<IllegalArgumentException> {
            HomeworkEntity(
                "",
                "description",
                LocalDate.now(),
                semesterId
            )
        }
        assertThrows<IllegalArgumentException> {
            HomeworkEntity(
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
            HomeworkEntity(
                "name",
                "",
                LocalDate.now(),
                semesterId
            )
        }
        assertThrows<IllegalArgumentException> {
            HomeworkEntity(
                "name",
                "     ",
                LocalDate.now(),
                semesterId
            )
        }
    }
}
