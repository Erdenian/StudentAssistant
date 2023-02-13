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
                LocalDate.now(),
                semesterId
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
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
        assertThrows(IllegalArgumentException::class.java) {
            HomeworkEntity(
                "name",
                "",
                LocalDate.now(),
                semesterId
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            HomeworkEntity(
                "name",
                "     ",
                LocalDate.now(),
                semesterId
            )
        }
    }

    @Test
    fun successfulTest() {
        HomeworkEntity(
            "name",
            "description",
            LocalDate.now(),
            semesterId
        )
    }
}
