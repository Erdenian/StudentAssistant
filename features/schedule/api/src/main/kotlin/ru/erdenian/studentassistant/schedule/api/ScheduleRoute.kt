package ru.erdenian.studentassistant.schedule.api

import androidx.navigation3.runtime.NavKey
import java.time.DayOfWeek
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import ru.erdenian.studentassistant.repository.api.entity.Lesson

sealed interface ScheduleRoute : NavKey {

    @Serializable
    data object Schedule : ScheduleRoute

    @Serializable
    data class SemesterEditor(val semesterId: Long? = null) : ScheduleRoute

    @Serializable
    data class ScheduleEditor(val semesterId: Long) : ScheduleRoute

    @Serializable
    data class LessonEditor(
        val semesterId: Long,
        @Serializable(with = DayOfWeekSerializer::class)
        val dayOfWeek: DayOfWeek? = null,
        val subjectName: String? = null,
        val lessonId: Long? = null,
        val copy: Boolean? = null,
    ) : ScheduleRoute

    @Serializable
    data class LessonInformation(val lesson: Lesson) : ScheduleRoute

    private object DayOfWeekSerializer : KSerializer<DayOfWeek> {
        override val descriptor = PrimitiveSerialDescriptor("java.time.DayOfWeek", PrimitiveKind.INT)
        override fun deserialize(decoder: Decoder): DayOfWeek = DayOfWeek.of(decoder.decodeInt())
        override fun serialize(encoder: Encoder, value: DayOfWeek) = encoder.encodeInt(value.value)
    }
}
