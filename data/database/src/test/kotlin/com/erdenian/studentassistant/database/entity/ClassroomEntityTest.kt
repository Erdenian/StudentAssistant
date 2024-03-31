package com.erdenian.studentassistant.database.entity

import org.junit.Assert.assertThrows
import org.junit.Test

internal class ClassroomEntityTest {

    @Test
    fun classroomTest() {
        assertThrows(IllegalArgumentException::class.java) {
            ClassroomEntity("")
        }
        assertThrows(IllegalArgumentException::class.java) {
            ClassroomEntity("   ")
        }
        ClassroomEntity("1204")
    }
}
