package ru.erdenian.studentassistant.repository.database.entity

import java.time.LocalDate
import org.junit.Assert.assertThrows
import org.junit.Test

internal class HomeworkEntityTest {

    private val semesterId = 1L

    @Test
    fun subjectNameTest() {
        assertThrows(IllegalArgumentException::class.java) {
            HomeworkEntity(
                subjectName = "",
                description = "description",
                deadline = LocalDate.of(2023, 2, 15),
                semesterId = semesterId,
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            HomeworkEntity(
                subjectName = "    ",
                description = "description",
                deadline = LocalDate.of(2023, 2, 15),
                semesterId = semesterId,
            )
        }
    }

    @Test
    fun descriptionTest() {
        assertThrows(IllegalArgumentException::class.java) {
            HomeworkEntity(
                subjectName = "name",
                description = "",
                deadline = LocalDate.of(2023, 2, 15),
                semesterId = semesterId,
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            HomeworkEntity(
                subjectName = "name",
                description = "     ",
                deadline = LocalDate.of(2023, 2, 15),
                semesterId = semesterId,
            )
        }
    }

    @Test
    fun successfulTest() {
        HomeworkEntity(
            subjectName = "name",
            description = "description",
            deadline = LocalDate.of(2023, 2, 15),
            semesterId = semesterId,
        )
    }
}
