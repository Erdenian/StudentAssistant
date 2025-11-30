package ru.erdenian.studentassistant.utils

import org.junit.Assert.assertEquals
import org.junit.Test

internal class StringTest {

    @Test
    fun toSingleLineTest() {
        assertEquals("one two", "one\ntwo".toSingleLine())
        assertEquals("one two", "one\r\ntwo".toSingleLine())
        assertEquals("one two", "one\rtwo".toSingleLine())
        assertEquals("one  two", "one  two".toSingleLine())
        assertEquals("one two three", "one\ntwo\nthree".toSingleLine())
        assertEquals("", "".toSingleLine())
    }
}
