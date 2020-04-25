package ru.erdenian.studentassistant.database

import androidx.room.TypeConverter
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.Period

object Converters {

    private val epoch = LocalDate(1970, 1, 1)

    @TypeConverter
    @JvmStatic
    fun localDateToInt(value: LocalDate?): Int? = value?.let { Days.daysBetween(epoch, it).days }

    @TypeConverter
    @JvmStatic
    fun intToLocalDate(value: Int?): LocalDate? = value?.let { epoch.plusDays(it) }

    @TypeConverter
    @JvmStatic
    fun localTimeToInt(value: LocalTime?): Int? = value?.millisOfDay

    @TypeConverter
    @JvmStatic
    fun intToLocalTime(value: Int?): LocalTime? = value?.let { LocalTime.MIDNIGHT.plusMillis(it) }

    @TypeConverter
    @JvmStatic
    fun intToPeriod(value: Int?): Period? = value?.let { Period.millis(it) }
}
