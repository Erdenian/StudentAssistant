package ru.erdenian.studentassistant.schedule

import com.google.common.collect.ComparisonChain
import com.google.common.collect.ImmutableSortedSet
import org.joda.time.LocalTime

/**
 * Класс пары (урока).
 *
 * @author Ilya Solovyev
 * @since 0.0.0
 * @param subjectName название предмета
 * @param type тип пары (лекция, семинар, ...)
 * @param teachers список преподавателей, ведущих пару
 * @param classrooms список аудиторий, в которых проходит пара
 * @param startTime время начала
 * @param endTime время конца
 * @param lessonRepeat когда повторяется пара
 * @param id уникальный id пары
 * @throws IllegalArgumentException если [subjectName] пусто или [startTime] >= [endTime]
 */
data class Lesson(
    val subjectName: String,
    val type: String,
    val teachers: ImmutableSortedSet<String>,
    val classrooms: ImmutableSortedSet<String>,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val lessonRepeat: LessonRepeat,
    val id: Long = generateId()
) : Comparable<Lesson> {

    init {
        if (subjectName.isBlank()) throw IllegalArgumentException("Отсутствует название предмета")
        if (startTime >= endTime) throw IllegalArgumentException("Неверно заданы даты: $startTime - $endTime")
    }

    override fun compareTo(other: Lesson) = ComparisonChain.start()
        .compare(startTime, other.startTime)
        .compare(endTime, other.endTime)
        .compare(id, other.id)
        .result()
}
