package ru.erdenian.studentassistant.repository.api.entity

import java.time.LocalTime
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

internal class LocalTimeSerializerTest {

    @Test
    fun testSerialization() {
        val time = LocalTime.of(13, 45, 30)
        val json = Json.encodeToString(LocalTimeSerializer, time)
        assertEquals("\"13:45:30\"", json)
    }

    @Test
    fun testDeserialization() {
        val json = "\"13:45:30\""
        val time = Json.decodeFromString(LocalTimeSerializer, json)
        assertEquals(LocalTime.of(13, 45, 30), time)
    }
}
