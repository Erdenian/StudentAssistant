package ru.erdenian.studentassistant.repository

import androidx.room.TypeConverter
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.Period
import ru.erdenian.studentassistant.extensions.toImmutableSortedSet
import ru.erdenian.studentassistant.schedule.LessonRepeat

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
    fun stringsToString(value: List<String?>?): String? =
        value?.joinToString(SEPARATOR)

    @TypeConverter
    fun stringToStrings(value: String?): List<String?>? =
        value?.split(SEPARATOR)

    @TypeConverter
    fun lessonRepeatToString(value: LessonRepeat?): String? =
        if (value == null) null else when (value) {
            is LessonRepeat.ByWeekday -> value.weekday.toString() +
                    SEPARATOR + value.weeks.joinToString(
                SEPARATOR
            )
            is LessonRepeat.ByDates -> value.dates.joinToString(SEPARATOR)
        }

    @TypeConverter
    fun stringToLessonRepeat(value: String?): LessonRepeat? =
        if (value == null) null else {
            if (value.contains('.')) LessonRepeat.ByDates(
                value.split(SEPARATOR).map {
                    LocalDate.parse(
                        it
                    )
                }.toImmutableSortedSet()
            )
            else {
                val separated = value.split(SEPARATOR)
                LessonRepeat.ByWeekday(
                    separated[0].toInt(),
                    separated.drop(1).map { it.toBoolean() }
                )
            }
        }
}
