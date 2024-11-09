package com.erdenian.studentassistant.database.entity

import java.time.LocalDate
import org.junit.Assert.assertThrows
import org.junit.Test

internal class HomeworkEntityTest {

    private val semesterId = 1L

    @Test
    fun subjectNameTest() {
        assertThrows(IllegalArgumentException::class.java) {
            HomeworkEntity(
                "",
                "description",
                LocalDate.of(2023, 2, 15),
                semesterId,
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            HomeworkEntity(
                "    ",
                "description",
                LocalDate.of(2023, 2, 15),
                semesterId,
            )
        }
    }

    @Test
    fun descriptionTest() {
        assertThrows(IllegalArgumentException::class.java) {
            HomeworkEntity(
                "name",
                "",
                LocalDate.of(2023, 2, 15),
                semesterId,
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            HomeworkEntity(
                "name",
                "     ",
                LocalDate.of(2023, 2, 15),
                semesterId,
            )
        }
    }

    @Test
    fun successfulTest() {
        HomeworkEntity(
            "name",
            "description",
            LocalDate.of(2023, 2, 15),
            semesterId,
        )
    }
}
