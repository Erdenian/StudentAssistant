package ru.erdenian.studentassistant.repository

import androidx.room.TypeConverter
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.Period
import ru.erdenian.studentassistant.repository.entity.LessonRepeatNew

class Converters {

    companion object {
        const val SEPARATOR = ";"

        private val epoch = LocalDate(1970, 1, 1)
    }

    @TypeConverter
    fun localDateToInt(value: LocalDate?): Int? =
        if (value == null) null else Days.daysBetween(epoch, value).days

    @TypeConverter
    fun intToLocalDate(value: Int?): LocalDate? =
        if (value == null) null else epoch.plusDays(value)

    @TypeConverter
    fun localTimeToInt(value: LocalTime?): Int? =
        value?.millisOfDay

    @TypeConverter
    fun intToLocalTime(value: Int?): LocalTime? =
        if (value == null) null else LocalTime.MIDNIGHT.plusMillis(value)

    @TypeConverter
    fun intToPeriod(value: Int?): Period? =
        if (value == null) null else Period.millis(value)

    @TypeConverter
    fun stringsImmutableSortedSetToString(value: ImmutableSortedSet<String?>?): String? =
        value?.joinToString(SEPARATOR)

    @TypeConverter
    fun stringToStringsImmutableSortedSet(value: String?): ImmutableSortedSet<String>? =
        value?.split(SEPARATOR)?.toImmutableSortedSet()

    @TypeConverter
    fun lessonRepeatToString(value: LessonRepeatNew?): String? =
        if (value == null) null else when (value) {
            is LessonRepeatNew.ByWeekday ->
                value.weekday.toString() + SEPARATOR + value.weeks.joinToString(SEPARATOR)
            is LessonRepeatNew.ByDates -> value.dates.joinToString(SEPARATOR)
        }

    @TypeConverter
    fun stringToLessonRepeat(value: String?): LessonRepeatNew? =
        if (value == null) null else {
            if (value.contains('.')) LessonRepeatNew.ByDates(
                value.split(SEPARATOR).map { LocalDate.parse(it) }.toImmutableSortedSet()
            )
            else {
                val separated = value.split(SEPARATOR)
                LessonRepeatNew.ByWeekday(
                    separated[0].toInt(),
                    separated.asSequence().drop(1).map { it.toBoolean() }.toList()
                )
            }
        }
}
