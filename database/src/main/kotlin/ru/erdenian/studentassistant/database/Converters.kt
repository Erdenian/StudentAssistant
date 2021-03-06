package ru.erdenian.studentassistant.database

import androidx.room.TypeConverter
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.LocalTime

internal object Converters {

    @Suppress("MagicNumber")
    private val epoch = LocalDate(1970, 1, 1)

    @TypeConverter
    @JvmStatic
    fun localDateToInt(value: LocalDate?): Int? = value?.let { Days.daysBetween(epoch, it).days }

    @TypeConverter
    @JvmStatic
    fun intToLocalDate(value: Int?): LocalDate? = value?.let(epoch::plusDays)

    @TypeConverter
    @JvmStatic
    fun localTimeToInt(value: LocalTime?): Int? = value?.millisOfDay

    @TypeConverter
    @JvmStatic
    fun intToLocalTime(value: Int?): LocalTime? = value?.let(LocalTime.MIDNIGHT::plusMillis)

    @TypeConverter
    @JvmStatic
    fun booleanListToString(value: List<Boolean>?): String? = value?.joinToString("") { if (it) "1" else "0" }

    @TypeConverter
    @JvmStatic
    fun stringToBooleanList(value: String?): List<Boolean>? = value?.map { it == '1' }
}
