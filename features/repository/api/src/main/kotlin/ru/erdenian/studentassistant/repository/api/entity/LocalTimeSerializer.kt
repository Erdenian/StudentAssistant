package ru.erdenian.studentassistant.repository.api.entity

import java.time.LocalTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object LocalTimeSerializer : KSerializer<LocalTime> {
    override val descriptor = PrimitiveSerialDescriptor("java.time.LocalTime", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): LocalTime = LocalTime.parse(decoder.decodeString())
    override fun serialize(encoder: Encoder, value: LocalTime) = encoder.encodeString(value.toString())
}
