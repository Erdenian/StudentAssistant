package ru.erdenian.studentassistant.repository.entity

import android.os.Parcelable
import com.google.common.collect.ImmutableSortedSet
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate

/**
 * Класс повторений пары.
 *
 * @author Ilya Solovyev
 * @since 0.0.0
 */
sealed class LessonRepeatNew : Parcelable {

    /**
     * Показывает, повторяется ли пара в заданный день.
     *
     * @author Ilya Solovyev
     * @since 0.0.0
     * @param day день
     * @param weekNumber номер недели, содержащей этот день (начинается с 0)
     * @return true, если пара повторяется в заданный день, в противном случае false
     */
    abstract fun repeatsOnDay(day: LocalDate, weekNumber: Int): Boolean

    /**
     * Повторение по дням недели.
     *
     * Для хранения списка недель используется List, так как массивы изменяемы.
     *
     * @author Ilya Solovyev
     * @since 0.0.0
     * @param weekday день недели (1 - понедельник, 7 - воскресенье)
     * @param weeks список boolean значений, где i-е значение показывает повторять ли пару каждую i-ю неделю
     * @throws IllegalArgumentException если [weekday] задан некорректно или [weeks] пуст
     */
    @Parcelize
    data class ByWeekday(val weekday: Int, val weeks: List<Boolean>) : LessonRepeatNew() {

        init {
            require(weekday in DateTimeConstants.MONDAY..DateTimeConstants.SUNDAY) {
                "Неверный день недели: $weekday"
            }
            require(weeks.isNotEmpty()) { "Массив недель пуст" }
        }

        override fun repeatsOnDay(day: LocalDate, weekNumber: Int) =
            (weeks[weekNumber % weeks.size]) && (day.dayOfWeek == weekday)

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
            require(weekday !in DateTimeConstants.MONDAY..DateTimeConstants.SUNDAY) {
                "Неверный день недели: $weekday"
            }
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
    @Parcelize
    data class ByDates(val dates: ImmutableSortedSet<LocalDate>) : LessonRepeatNew() {

        init {
            require(dates.isNotEmpty()) { "Массив дат пуст" }
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