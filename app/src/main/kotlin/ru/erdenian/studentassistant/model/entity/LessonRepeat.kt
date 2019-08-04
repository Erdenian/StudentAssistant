package ru.erdenian.studentassistant.model.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.model.ImmutableSortedSet

/**
 * Класс повторений пары
 *
 * @author Ilya Solovyov
 * @since 0.0.0
 */
sealed class LessonRepeat : Parcelable {

    /**
     * Показывает, повторяется ли пара в заданный день
     *
     * @param day день
     * @param weekNumber номер недели, содержащей этот день (начинается с 0)
     * @return true, если пара повторяется в заданный день, в противном случае false
     * @author Ilya Solovyov
     * @since 0.0.0
     */
    abstract fun repeatsOnDay(day: LocalDate, weekNumber: Int): Boolean

    /**
     * Повторение по дням недели
     *
     * Для хранения списка недель используется List, так как массивы изменяемы.
     *
     * @param weekday день недели (1 - понедельник, 7 - воскресенье)
     * @param weeks список boolean значений, где i-е значение показывает повторять ли пару каждую i-ю неделю
     * @throws IllegalArgumentException если [weekday] задан некорректно или [weeks] пуст
     * @author Ilya Solovyov
     * @since 0.0.0
     */
    @Parcelize
    data class ByWeekday(val weekday: Int, val weeks: List<Boolean>) : LessonRepeat() {

        init {
            require(weekday in DateTimeConstants.MONDAY..DateTimeConstants.SUNDAY) {
                "Неверный день недели: $weekday"
            }
            require(weeks.isNotEmpty()) { "Список недель пуст" }
            require(weeks.contains(true)) { "Нет повторений ни на одной неделе" }
        }

        override fun repeatsOnDay(day: LocalDate, weekNumber: Int) =
            (day.dayOfWeek == weekday) && (weeks[weekNumber % weeks.size])

        /**
         * Показывает, повторяется ли пара в заданный день недели без учета номера недели
         *
         * @param weekday день недели (1 - понедельник, 7 - воскресенье)
         * @return true, если пара повторяется в этот день недели, хотя бы на одной неделе, false в противном случае
         * @throws IllegalArgumentException если [weekday] задан некорректно
         * @author Ilya Solovyov
         * @since 0.0.0
         */
        fun repeatsOnWeekday(weekday: Int): Boolean {
            require(weekday in DateTimeConstants.MONDAY..DateTimeConstants.SUNDAY) {
                "Неверный день недели: $weekday"
            }
            return (weekday == this.weekday)
        }
    }

    /**
     * Повторение по датам
     *
     * @param dates список дат, по которым повторяется пара
     * @throws IllegalArgumentException если [dates] пуст
     * @author Ilya Solovyov
     * @since 0.0.0
     */
    @Parcelize
    data class ByDates(val dates: ImmutableSortedSet<LocalDate>) : LessonRepeat() {

        init {
            require(dates.isNotEmpty()) { "Массив дат пуст" }
        }

        override fun repeatsOnDay(day: LocalDate, weekNumber: Int) = day in dates

        /**
         * То же, что и [repeatsOnDay], но без ненужного второго параметра
         *
         * @param date день
         * @return true, если пара повторяется в заданный день, в противном случае false
         * @author Ilya Solovyov
         * @since 0.0.0
         */
        fun repeatsOnDate(date: LocalDate) = repeatsOnDay(date, -1)
    }
}
