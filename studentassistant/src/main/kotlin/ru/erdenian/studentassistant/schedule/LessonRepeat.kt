package ru.erdenian.studentassistant.schedule

import com.google.common.collect.ImmutableSortedSet
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate

/**
 * Класс повторений пары.
 *
 * @author Ilya Solovyev
 * @since 0.0.0
 */
sealed class LessonRepeat {

  /**
   * Показывает, повторяется ли пара в заданный день.
   *
   * @author Ilya Solovyev
   * @since 0.0.0
   * @param day день
   * @param weekNumber номер недели, содержащей этот день
   * @return true, если пара повторяется в заданный день, в противном случае false
   */
  abstract fun repeatsOnDay(day: LocalDate, weekNumber: Int): Boolean

  /**
   * Повторение по дням недели.
   *
   * @author Ilya Solovyev
   * @since 0.0.0
   * @param weekday день недели (1 - понедельник, 7 - воскресенье)
   * @param weeks список boolean значений, где i-е значение показывает повторять ли пару каждую i-ю неделю
   * @throws IllegalArgumentException если [weekday] задан некорректно или [weeks] пуст
   */
  class ByWeekday(val weekday: Int, val weeks: List<Boolean>) : LessonRepeat() {

    init {
      if (weekday !in DateTimeConstants.MONDAY..DateTimeConstants.SUNDAY)
        throw IllegalArgumentException("Неверный день недели: $weekday")
      if (weeks.isEmpty()) throw IllegalArgumentException("Массив недель пуст")
    }

    override fun repeatsOnDay(day: LocalDate, weekNumber: Int) = (weeks[weekNumber % weeks.size]) && (day.dayOfWeek == weekday)

    /**
     * Показывает, повторяется ли пара в заданный день недели без учета номера недели.
     *
     * @author Ilya Solovyev
     * @since 0.0.0
     * @param weekday день недели (1 - понедельник, 7 - воскресенье)
     * @return true, если пара повторяется в этот день недели, хотя бы на одной неделе, false в противном случае
     * @throws IllegalArgumentException если [weekday] задан некорректно
     */
    fun repeatsOnWeekday(weekday: Int): Boolean {
      if (weekday !in DateTimeConstants.MONDAY..DateTimeConstants.SUNDAY)
        throw IllegalArgumentException("Неверный день недели: $weekday")
      return (weekday == this.weekday)
    }
  }

  /**
   * Повторение по датам.
   *
   * @author Ilya Solovyev
   * @since 0.0.0
   * @param dates список дат, по которым повторяется пара
   * @throws IllegalArgumentException если [dates] пуст
   */
  class ByDates(val dates: ImmutableSortedSet<LocalDate>) : LessonRepeat() {

    init {
      if (dates.isEmpty()) throw IllegalArgumentException("Массив дат пуст")
    }

    override fun repeatsOnDay(day: LocalDate, weekNumber: Int) = day in dates

    /**
     * То же, что и [repeatsOnDay], но без ненужного второго параметра
     *
     * @author Ilya Solovyev
     * @since 0.0.0
     * @param date день
     * @return true, если пара повторяется в заданный день, в противном случае false
     */
    fun repeatsOnDate(date: LocalDate) = repeatsOnDay(date, -1)
  }
}
