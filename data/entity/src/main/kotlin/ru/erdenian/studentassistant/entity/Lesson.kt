package ru.erdenian.studentassistant.entity

import android.os.Parcelable
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import org.joda.time.LocalTime

/**
 * Класс пары (урока).
 *
 * @property subjectName название предмета
 * @property type тип пары (лекция, семинар, ...)
 * @property teachers список преподавателей, ведущих пару
 * @property classrooms список аудиторий, в которых проходит пара
 * @property startTime время начала
 * @property endTime время конца
 * @property lessonRepeat когда повторяется пара
 * @property id уникальный id пары
 * @throws IllegalArgumentException если [subjectName] пусто или [startTime] >= [endTime]
 * @author Ilya Solovyov
 * @since 0.0.0
 */
interface Lesson : Comparable<Lesson>, Parcelable {

    val subjectName: String
    val type: String
    val teachers: ImmutableSortedSet<String>
    val classrooms: ImmutableSortedSet<String>

    val startTime: LocalTime
    val endTime: LocalTime
    val lessonRepeat: Repeat

    val semesterId: Long
    val id: Long

    override fun compareTo(other: Lesson) = compareValuesBy(
        this,
        other,
        Lesson::startTime,
        Lesson::endTime,
        Lesson::id
    )

    /**
     * Класс повторений пары.
     *
     * @author Ilya Solovyov
     * @since 0.0.0
     */
    sealed class Repeat : Parcelable {

        /**
         * Показывает, повторяется ли пара в заданный день.
         *
         * @param day день
         * @param weekNumber номер недели, содержащей этот день (начинается с 0)
         * @return true, если пара повторяется в заданный день, в противном случае false
         * @author Ilya Solovyov
         * @since 0.0.0
         */
        abstract fun repeatsOnDay(day: LocalDate, weekNumber: Int): Boolean

        /**
         * Повторение по дням недели.
         *
         * Для хранения списка недель используется List, так как массивы изменяемы.
         *
         * @property weekday день недели (1 - понедельник, 7 - воскресенье)
         * @property weeks список boolean значений, где i-е значение показывает
         *              повторять ли пару каждую i-ю неделю
         * @throws IllegalArgumentException если [weekday] задан некорректно или [weeks] пуст
         * @author Ilya Solovyov
         * @since 0.0.0
         */
        abstract class ByWeekday : Repeat() {

            abstract val weekday: Int
            abstract val weeks: List<Boolean>

            override fun repeatsOnDay(day: LocalDate, weekNumber: Int) =
                (day.dayOfWeek == weekday) && (weeks[weekNumber % weeks.size])

            /**
             * Показывает, повторяется ли пара в заданный день недели без учета номера недели.
             *
             * @param weekday день недели (1 - понедельник, 7 - воскресенье)
             * @return true, если пара повторяется в этот день недели, хотя бы на одной неделе,
             *               false в противном случае
             * @throws IllegalArgumentException если [weekday] задан некорректно
             * @author Ilya Solovyov
             * @since 0.0.0
             */
            fun repeatsOnWeekday(weekday: Int): Boolean {
                require(weekday in DateTimeConstants.MONDAY..DateTimeConstants.SUNDAY) { "Неверный день недели: $weekday" }
                return (weekday == this.weekday)
            }
        }

        /**
         * Повторение по датам.
         *
         * @property dates список дат, по которым повторяется пара
         * @throws IllegalArgumentException если [dates] пуст
         * @author Ilya Solovyov
         * @since 0.0.0
         */
        abstract class ByDates : Repeat() {

            abstract val dates: Set<LocalDate>

            override fun repeatsOnDay(day: LocalDate, weekNumber: Int) = day in dates

            /**
             * То же, что и [repeatsOnDay], но без ненужного второго параметра.
             *
             * @param date день
             * @return true, если пара повторяется в заданный день, в противном случае false
             * @author Ilya Solovyov
             * @since 0.0.0
             */
            fun repeatsOnDate(date: LocalDate) = repeatsOnDay(date, -1)
        }
    }
}
