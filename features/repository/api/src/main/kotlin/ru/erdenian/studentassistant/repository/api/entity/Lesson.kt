package ru.erdenian.studentassistant.repository.api.entity

import android.os.Parcelable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Класс занятия.
 *
 * @property subjectName название предмета
 * @property type тип занятия (лекция, семинар, ...)
 * @property teachers список преподавателей, ведущих занятие
 * @property classrooms список аудиторий, в которых проходит занятие
 * @property startTime время начала
 * @property endTime время конца
 * @property lessonRepeat когда повторяется занятие
 * @property id уникальный id занятия
 * @throws IllegalArgumentException если [subjectName] пусто или [startTime] >= [endTime]
 * @author Ilya Solovyov
 * @since 0.0.0
 */
@Serializable
@Parcelize
data class Lesson(
    val subjectName: String,
    val type: String,
    val teachers: List<String>,
    val classrooms: List<String>,
    @Serializable(with = LocalTimeSerializer::class)
    val startTime: LocalTime,
    @Serializable(with = LocalTimeSerializer::class)
    val endTime: LocalTime,
    val lessonRepeat: Repeat,
    val semesterId: Long,
    val id: Long,
) : Comparable<Lesson>, Parcelable {

    /**
     * ClosedRange из начального и конечного времени занятия.
     *
     * @author Ilya Solovyov
     * @since 0.5.4
     */
    val timeRange: ClosedRange<LocalTime> get() = startTime..endTime

    override fun compareTo(other: Lesson) = compareValuesBy(
        this,
        other,
        Lesson::startTime,
        Lesson::endTime,
        Lesson::subjectName,
        Lesson::type,
        Lesson::id,
        Lesson::semesterId,
    )

    /**
     * Класс повторений занятия.
     *
     * @author Ilya Solovyov
     * @since 0.0.0
     */
    @Serializable
    @Parcelize
    sealed class Repeat : Parcelable {

        /**
         * Повторение по дням недели.
         *
         * Для хранения списка недель используется List, так как массивы изменяемы.
         *
         * @property dayOfWeek день недели
         * @property weeks список boolean значений, где i-е значение показывает повторять ли занятие каждую i-ю неделю
         * @throws IllegalArgumentException если [weeks] пуст
         * @author Ilya Solovyov
         * @since 0.0.0
         */
        @Serializable
        data class ByWeekday(
            @Serializable(with = DayOfWeekSerializer::class)
            val dayOfWeek: DayOfWeek,
            val weeks: List<Boolean>,
        ) : Repeat()

        /**
         * Повторение по датам.
         *
         * @property dates список дат, по которым повторяется занятие
         * @throws IllegalArgumentException если [dates] пуст
         * @author Ilya Solovyov
         * @since 0.0.0
         */
        @Serializable
        data class ByDates(
            val dates: Set<@Serializable(with = LocalDateSerializer::class) LocalDate>,
        ) : Repeat()

        private object DayOfWeekSerializer : KSerializer<DayOfWeek> {
            override val descriptor = PrimitiveSerialDescriptor("java.time.DayOfWeek", PrimitiveKind.INT)
            override fun deserialize(decoder: Decoder): DayOfWeek = DayOfWeek.of(decoder.decodeInt())
            override fun serialize(encoder: Encoder, value: DayOfWeek) = encoder.encodeInt(value.value)
        }
    }
}
