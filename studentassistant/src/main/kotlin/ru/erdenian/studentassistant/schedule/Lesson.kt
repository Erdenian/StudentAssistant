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
                  private val weekday: Int? = null, private val weeks: List<Boolean>? = null,
                  private val dates: ImmutableSortedSet<LocalDate>? = null,
                  val id: Long = System.nanoTime()) : Comparable<Lesson> {

    enum class RepeatType {
        BY_WEEKDAY,
        BY_DATE
    }

    val weekday_ = if (repeatType == RepeatType.BY_WEEKDAY) {
        if (weekday !in 1..7)
            throw IllegalArgumentException("Некорректный номер недели: $weekday")
        weekday
    } else null

    val weeks_ = if (repeatType == RepeatType.BY_WEEKDAY) {
        if (weeks!!.isEmpty())
            throw IllegalArgumentException("Массив с номерами недель пуст")
        weeks
    } else null

    val dates_ = if (repeatType == RepeatType.BY_DATE) {
        if (dates!!.isEmpty())
            throw IllegalArgumentException("Массив с датами пуст")
        dates
    } else null

    fun repeatsOnDay(day: LocalDate, weekNumber: Int): Boolean {
        when (repeatType) {
            Lesson.RepeatType.BY_WEEKDAY -> return (weeks_!![weekNumber % weeks_.size]) && (day.dayOfWeek == weekday_)
            Lesson.RepeatType.BY_DATE -> return day in dates_!!
            else -> Log.wtf(this.javaClass.name,
                    "В repeatType хранится неизвестное значение: $repeatType")
        }
        return false
    }

    fun repeatsOnWeekday(weekday: Int): Boolean {
        return (repeatType == RepeatType.BY_WEEKDAY) && (weekday == this.weekday_)
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
