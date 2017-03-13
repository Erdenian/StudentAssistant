package ru.erdenian.studentassistant.schedule

import com.google.common.collect.ImmutableSortedSet
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate

sealed class LessonRepeat {

  abstract fun repeatsOnDay(day: LocalDate, weekNumber: Int): Boolean

  class ByWeekday(val weekday: Int, val weeks: List<Boolean>) : LessonRepeat() {

    init {
      if (weekday !in DateTimeConstants.MONDAY..DateTimeConstants.SUNDAY)
        throw IllegalArgumentException("Неверный день недели: $weekday")
      if (weeks.isEmpty()) throw IllegalArgumentException("Массив недель пуст")
    }

    override fun repeatsOnDay(day: LocalDate, weekNumber: Int) = (weeks[weekNumber % weeks.size]) && (day.dayOfWeek == weekday)

    fun repeatsOnWeekday(weekday: Int) = (weekday == this.weekday)
  }

  class ByDates(val dates: ImmutableSortedSet<LocalDate>) : LessonRepeat() {

    init {
      if (dates.isEmpty()) throw IllegalArgumentException("Массив дат пуст")
    }

    override fun repeatsOnDay(day: LocalDate, weekNumber: Int) = day in dates

    fun repeatsOnDate(date: LocalDate) = repeatsOnDay(date, -1)
  }
}
