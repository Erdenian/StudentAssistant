package com.erdenian.studentassistant.utils

import org.junit.Assert.assertEquals
import org.junit.Test

internal class StringTest {

    @Test
    fun toSingleLineTest() {
        assertEquals("one two", "one\ntwo".toSingleLine())
        assertEquals("one two", "one\r\ntwo".toSingleLine())
        assertEquals("one two", "one\rtwo".toSingleLine())
    }
}
