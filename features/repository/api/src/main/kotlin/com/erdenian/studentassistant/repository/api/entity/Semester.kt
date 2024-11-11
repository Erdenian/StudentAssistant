package com.erdenian.studentassistant.repository.api.entity

import android.os.Parcelable
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlinx.parcelize.Parcelize

/**
 * Класс семестра (четверти).
 *
 * @property name название семестра
 * @property firstDay первый день семестра
 * @property lastDay последний день семестра
 * @property id уникальный id семестра
 * @throws IllegalArgumentException если [name] пусто или [firstDay] > [lastDay]
 * @author Ilya Solovyov
 * @since 0.0.0
 */
@Parcelize
data class Semester(
    val name: String,
    val firstDay: LocalDate,
    val lastDay: LocalDate,
    val id: Long,
) : Comparable<Semester>, Parcelable {

    /**
     * Длина семестра в днях.
     *
     * @author Ilya Solovyov
     * @since 0.0.0
     */
    val length: Int get() = ChronoUnit.DAYS.between(firstDay, lastDay).toInt() + 1

    /**
     * ClosedRange из начальной и конечной даты семестра.
     *
     * @author Ilya Solovyov
     * @since 0.5.4
     */
    val dateRange: ClosedRange<LocalDate> get() = firstDay..lastDay

    /**
     * Дата понедельника в неделе, содержащей [firstDay].
     *
     * @author Ilya Solovyov
     * @since 0.2.6
     */
    private val firstWeekMonday: LocalDate get() = firstDay.minusDays(firstDay.dayOfWeek.value.toLong() - 1L)

    /**
     * Позволяет получить номер недели с начала семестра, содержащей определенную дату.
     *
     * Начинается с 0.
     *
     * @author Ilya Solovyov
     * @since 0.0.0
     * @param day день
     * @return номер недели, содержащей этот день (< 0, если [day] < [firstDay])
     */
    fun getWeekNumber(day: LocalDate): Int =
        ChronoUnit.WEEKS.between(firstWeekMonday, day).toInt() - if (day >= firstWeekMonday) 0 else 1

    override fun compareTo(other: Semester) = compareValuesBy(
        this,
        other,
        Semester::lastDay,
        Semester::firstDay,
        Semester::name,
        Semester::id,
    )
}
