package ru.erdenian.studentassistant.repository.api.entity

import java.time.LocalDate
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

internal class LocalDateSerializerTest {

    @Test
    fun testSerialization() {
        val date = LocalDate.of(2023, 2, 14)
        val json = Json.encodeToString(LocalDateSerializer, date)
        assertEquals("\"2023-02-14\"", json)
    }

    @Test
    fun testDeserialization() {
        val json = "\"2023-02-14\""
        val date = Json.decodeFromString(LocalDateSerializer, json)
        assertEquals(LocalDate.of(2023, 2, 14), date)
    }
}
