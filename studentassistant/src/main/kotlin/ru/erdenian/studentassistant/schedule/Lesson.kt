package ru.erdenian.studentassistant.schedule

import android.util.Log
import com.google.common.collect.ComparisonChain
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSortedSet
import org.joda.time.LocalDate
import org.joda.time.LocalTime

data class Lesson(val name: String, val type: String?,
                  @Transient private var teachers_: ImmutableSortedSet<String>?,
                  @Transient private var classrooms_: ImmutableSortedSet<String>?,
                  val startTime: LocalTime, val endTime: LocalTime,
                  val repeatType: Lesson.RepeatType,
                  @Transient private var weekday_: Int? = null,
                  @Transient private var weeks_: ImmutableList<Boolean>? = null,
                  @Transient private var dates_: ImmutableSortedSet<LocalDate>? = null,
                  val id: Long = System.nanoTime()) : Comparable<Lesson> {

    enum class RepeatType {
        BY_WEEKDAY,
        BY_DATE
    }

    val teachers = {
        val value = teachers_
        teachers_ = null

        if (value?.isEmpty() == true) null
        else value
    }()

    val classrooms = {
        val value = classrooms_
        classrooms_ = null

        if (value?.isEmpty() == true) null
        else value
    }()

    val weekday = {
        val value = weekday_
        weekday_ = null

        if (repeatType == RepeatType.BY_WEEKDAY) {
            if (value !in 1..7) throw IllegalArgumentException("Некорректный номер недели: $value")
            else value
        } else null
    }()

    val weeks = {
        val value = weeks_
        weeks_ = null

        if (repeatType == RepeatType.BY_WEEKDAY) {
            if (value!!.isEmpty()) throw IllegalArgumentException("Массив с номерами недель пуст")
            else if (!value.contains(true)) throw IllegalArgumentException("Массив с номерами недель заполнен некорректно")
            else value
        } else null
    }()

    val dates = {
        val value = dates_
        dates_ = null

        if (repeatType == RepeatType.BY_DATE) {
            if (value!!.isEmpty()) throw IllegalArgumentException("Массив с датами пуст")
            else value
        } else null
    }()

    fun repeatsOnDay(day: LocalDate, weekNumber: Int): Boolean {
        when (repeatType) {
            Lesson.RepeatType.BY_WEEKDAY -> return (weeks!![weekNumber % weeks.size]) && (day.dayOfWeek == weekday)
            Lesson.RepeatType.BY_DATE -> return day in dates!!
            else -> Log.wtf(this.javaClass.name, "В repeatType хранится неизвестное значение: $repeatType")
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