package com.erdenian.studentassistant.database

import androidx.room.TypeConverter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

internal object Converters {

    @TypeConverter
    @JvmStatic
    fun dayOfWeekToInt(value: DayOfWeek?): Int? = value?.value

    @TypeConverter
    @JvmStatic
    fun intToDayOfWeek(value: Int?): DayOfWeek? = value?.let(DayOfWeek::of)

    @TypeConverter
    @JvmStatic
    fun localDateToLong(value: LocalDate?): Long? = value?.toEpochDay()

    @TypeConverter
    @JvmStatic
    fun longToLocalDate(value: Long?): LocalDate? = value?.let(LocalDate::ofEpochDay)

    @TypeConverter
    @JvmStatic
    fun localTimeToInt(value: LocalTime?): Long? = value?.toNanoOfDay()

    @TypeConverter
    @JvmStatic
    fun intToLocalTime(value: Long?): LocalTime? = value?.let(LocalTime::ofNanoOfDay)

    @TypeConverter
    @JvmStatic
    fun booleanListToString(value: List<Boolean>?): String? = value?.joinToString("") { if (it) "1" else "0" }

    @TypeConverter
    @JvmStatic
    fun stringToBooleanList(value: String?): List<Boolean>? = value?.map { it == '1' }
}
