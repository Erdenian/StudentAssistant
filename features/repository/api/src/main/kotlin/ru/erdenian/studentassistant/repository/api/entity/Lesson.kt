package ru.erdenian.studentassistant.repository.api.entity

import android.os.Parcelable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Класс пары (урока).
 *
 * @property subjectName название предмета
 * @property type тип пары (лекция, семинар, ...)
 * @property teachers список преподавателей, ведущих пару
 * @property classrooms список аудиторий, в которых проходит пара
 * @property startTime время начала
 * @property endTime время конца
 * @property lessonRepeat когда повторяется пара
 * @property id уникальный id пары
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
     * ClosedRange из начального и конечного времени пары.
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
        { it.teachers.joinToString() },
        { it.classrooms.joinToString() },
        { it.lessonRepeat.toString() },
        Lesson::id,
        Lesson::semesterId,
    )

    /**
     * Класс повторений пары.
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
         * @property weeks список boolean значений, где i-е значение показывает повторять ли пару каждую i-ю неделю
         * @throws IllegalArgumentException если [weeks] пуст
         * @author Ilya Solovyov
         * @since 0.0.0
         */
        @Serializable
        data class ByWeekday(
            val dayOfWeek: DayOfWeek,
            val weeks: List<Boolean>,
        ) : Repeat()

        /**
         * Повторение по датам.
         *
         * @property dates список дат, по которым повторяется пара
         * @throws IllegalArgumentException если [dates] пуст
         * @author Ilya Solovyov
         * @since 0.0.0
         */
        @Serializable
        data class ByDates(
            val dates: Set<@Serializable(with = LocalDateSerializer::class) LocalDate>,
        ) : Repeat()
    }
}
