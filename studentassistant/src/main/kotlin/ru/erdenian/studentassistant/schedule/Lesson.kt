package ru.erdenian.studentassistant.schedule

import android.util.Log
import com.google.common.collect.ComparisonChain
import com.google.common.collect.ImmutableSortedSet
import org.joda.time.LocalDate
import org.joda.time.LocalTime

/**
 * Класс пары. Является неизменяемым.
 *
 * @author Ilya Solovyev
 * @version 0.0.0
 * @since 0.0.0
 */
data class Lesson(val name: String, val type: String?,
                  val teachers: ImmutableSortedSet<String>, val classrooms: ImmutableSortedSet<String>,
                  val startTime: LocalTime, val endTime: LocalTime,
                  val repeatType: Lesson.RepeatType,
                  private val weekday_: Int? = null, private val weeks_: List<Boolean>? = null,
                  private val dates_: ImmutableSortedSet<LocalDate>? = null,
                  val id: Long = System.nanoTime()) : Comparable<Lesson> {

    enum class RepeatType {
        BY_WEEKDAY,
        BY_DATE
    }

    val weekday = if (repeatType == RepeatType.BY_WEEKDAY) {
        if (weekday_ !in 1..7)
            throw IllegalArgumentException("Некорректный номер недели: $weekday_")
        weekday_
    } else null

    val weeks = if (repeatType == RepeatType.BY_WEEKDAY) {
        if (weeks_!!.isEmpty())
            throw IllegalArgumentException("Массив с номерами недель пуст")
        weeks_
    } else null

    val dates = if (repeatType == RepeatType.BY_DATE) {
        if (dates_!!.isEmpty())
            throw IllegalArgumentException("Массив с датами пуст")
        dates_
    } else null

    fun repeatsOnDay(day: LocalDate, weekNumber: Int): Boolean {
        when (repeatType) {
            Lesson.RepeatType.BY_WEEKDAY -> return (weeks!![weekNumber % weeks.size]) && (day.dayOfWeek == weekday)
            Lesson.RepeatType.BY_DATE -> return day in dates!!
            else -> Log.wtf(this.javaClass.name,
                    "В repeatType хранится неизвестное значение: $repeatType")
        }
        return false
    }

    fun repeatsOnWeekday(weekday: Int): Boolean {
        return (repeatType == RepeatType.BY_WEEKDAY) && (weekday == this.weekday)
    }

    fun repeatsOnDate(): Boolean {
        return (repeatType == RepeatType.BY_DATE)
    }

    override fun compareTo(other: Lesson): Int {
        return ComparisonChain.start()
                .compare(startTime, other.startTime)
                .compare(endTime, other.endTime)
                .result()
    }
}
