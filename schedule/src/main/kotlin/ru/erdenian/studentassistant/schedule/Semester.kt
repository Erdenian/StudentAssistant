package ru.erdenian.studentassistant.schedule

import com.google.common.collect.ComparisonChain
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.Weeks

/**
 * Класс семестра (четверти).
 *
 * @author Ilya Solovyev
 * @since 0.0.0
 * @property name название семестра
 * @property firstDay первый день семестра
 * @property lastDay последний день семестра
 * @property id уникальный id семестра
 * @throws IllegalArgumentException если [name] пусто или [firstDay] > [lastDay]
 */
data class Semester(
    val name: String,
    val firstDay: LocalDate,
    val lastDay: LocalDate,
    val id: Long = generateId()
) : Comparable<Semester> {

    /**
     * Длина семестра в днях.
     *
     * @author Ilya Solovyev
     * @since 0.0.0
     */
    val length = Days.daysBetween(firstDay, lastDay).days + 1

    /**
     * Дата понедельника в неделе, содержащей [firstDay].
     *
     * @author Ilya Solovyev
     * @since 0.2.6
     */
    private val firstWeekMonday = firstDay.minusDays(firstDay.dayOfWeek - 1)

    init {
        if (name.isBlank()) throw IllegalArgumentException("Пустое название")
        if (firstDay > lastDay) throw IllegalArgumentException("Неверно заданы даты: $firstDay - $lastDay")
    }

    /**
     * Позволяет получить номер недели с начала семестра, содержащей определенную дату.
     *
     * Начинается с 0.
     *
     * @author Ilya Solovyev
     * @since 0.0.0
     * @param day день
     * @return номер недели, содержащей этот день (< 0, если [day] < [firstDay])
     */
    fun getWeekNumber(day: LocalDate) =
        Weeks.weeksBetween(firstWeekMonday, day).weeks - if (day >= firstWeekMonday) 0 else 1

    override fun compareTo(other: Semester) = ComparisonChain.start()
        .compare(lastDay, other.lastDay)
        .compare(firstDay, other.firstDay)
        .compare(name, other.name)
        .compare(id, other.id)
        .result()
}
