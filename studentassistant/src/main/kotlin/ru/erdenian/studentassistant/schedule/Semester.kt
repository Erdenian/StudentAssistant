package ru.erdenian.studentassistant.schedule

import com.google.common.collect.ComparisonChain
import org.joda.time.Days
import org.joda.time.LocalDate

data class Semester(val name: String, val firstDay: LocalDate, val lastDay: LocalDate,
                    val id: Long = generateId()) : Comparable<Semester> {

  val length: Int by lazy { Days.daysBetween(firstDay, lastDay).days + 1 }

  init {
    if (name.isBlank()) throw IllegalArgumentException("Пустое название")
    if (!firstDay.isBefore(lastDay)) throw IllegalArgumentException("Неверно заданы даты: $firstDay - $lastDay")
  }

  fun getWeekNumber(day: LocalDate) = Days.daysBetween(firstDay.minusDays(firstDay.dayOfWeek - 1), day).days / 7

  override fun compareTo(other: Semester) = ComparisonChain.start()
      .compare(lastDay, other.lastDay)
      .compare(firstDay, other.firstDay)
      .compare(name, other.name)
      .compare(id, other.id)
      .result()
}
