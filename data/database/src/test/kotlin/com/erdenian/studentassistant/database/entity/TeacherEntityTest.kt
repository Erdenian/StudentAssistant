package com.erdenian.studentassistant.database.entity

import org.junit.Assert.assertThrows
import org.junit.Test

internal class TeacherEntityTest {

    @Test
    fun teacherTest() {
        assertThrows(IllegalArgumentException::class.java) {
            TeacherEntity("")
        }
        assertThrows(IllegalArgumentException::class.java) {
            TeacherEntity("   ")
        }
        TeacherEntity("1204")
    }
}
