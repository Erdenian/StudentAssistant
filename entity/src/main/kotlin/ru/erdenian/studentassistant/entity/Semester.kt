package ru.erdenian.studentassistant.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.Weeks

/**
 * Класс семестра (четверти).
 *
 * @property name название семестра
 * @property firstDay первый день семестра
 * @property lastDay последний день семестра
 * @property id уникальный id семестра
 * @throws IllegalArgumentException если [name] пусто или [firstDay] > [lastDay]
 * @author Ilya Solovyov
 * @since 0.0.0
 */
@Parcelize
@Entity(tableName = "semesters")
data class Semester(

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "first_day")
    val firstDay: LocalDate,

    @ColumnInfo(name = "last_day")
    val lastDay: LocalDate,

    @PrimaryKey
    @ColumnInfo(name = "_id")
    val id: Long = generateId()
) : Comparable<Semester>, Parcelable {

    /**
     * Длина семестра в днях.
     *
     * @author Ilya Solovyov
     * @since 0.0.0
     */
    @IgnoredOnParcel
    @Ignore
    val length = Days.daysBetween(firstDay, lastDay).days + 1

    @IgnoredOnParcel
    @Ignore
    val range = firstDay..lastDay

    /**
     * Дата понедельника в неделе, содержащей [firstDay].
     *
     * @author Ilya Solovyov
     * @since 0.2.6
     */
    @IgnoredOnParcel
    @Ignore
    private val firstWeekMonday = firstDay.minusDays(firstDay.dayOfWeek - 1)

    init {
        require(name.isNotBlank()) { "Пустое название" }
        require(firstDay < lastDay) { "Неверно заданы даты: $firstDay - $lastDay" }
    }

    /**
     * Позволяет получить номер недели с начала семестра, содержащей определенную дату.
     *
     * Начинается с 0.
     *
     * @author Ilya Solovyov
     * @since 0.0.0
     * @param day день
     * @return номер недели, содержащей этот день (< 0, если [day] < [firstDay])
     */
    fun getWeekNumber(day: LocalDate) =
        Weeks.weeksBetween(firstWeekMonday, day).weeks - if (day >= firstWeekMonday) 0 else 1

    override fun compareTo(other: Semester) = compareValuesBy(
        this, other,
        Semester::lastDay,
        Semester::firstDay,
        Semester::name,
        Semester::id
    )
}
