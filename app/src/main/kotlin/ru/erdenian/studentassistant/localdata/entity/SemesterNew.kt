package ru.erdenian.studentassistant.localdata.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.common.collect.ComparisonChain
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.Weeks
import ru.erdenian.studentassistant.schedule.generateId

/**
 * Класс семестра (четверти).
 *
 * @author Ilya Solovyev
 * @since 0.0.0
 * @property name название семестра
 * @property firstDay первый день семестра
 * @property lastDay последний день семестра
 * @property id уникальный id семестра
 * @throws IllegalArgumentException если [name] пусто или [firstDay] > [lastDay]
 */
@Parcelize
@Entity(tableName = "semesters")
data class SemesterNew(

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "first_day")
    val firstDay: LocalDate,

    @ColumnInfo(name = "last_day")
    val lastDay: LocalDate,

    @PrimaryKey
    @ColumnInfo(name = "_id")
    val id: Long = generateId()
) : Comparable<SemesterNew>, Parcelable {

    /**
     * Длина семестра в днях.
     *
     * @author Ilya Solovyev
     * @since 0.0.0
     */
    @IgnoredOnParcel
    @Ignore
    val length = Days.daysBetween(firstDay, lastDay).days + 1

    /**
     * Дата понедельника в неделе, содержащей [firstDay].
     *
     * @author Ilya Solovyev
     * @since 0.2.6
     */
    @IgnoredOnParcel
    @Ignore
    private val firstWeekMonday = firstDay.minusDays(firstDay.dayOfWeek - 1)

    init {
        if (name.isBlank()) throw IllegalArgumentException("Пустое название")
        if (firstDay > lastDay) throw IllegalArgumentException("Неверно заданы даты: $firstDay - $lastDay")
    }

    /**
     * Позволяет получить номер недели с начала семестра, содержащей определенную дату.
     *
     * Начинается с 0.
     *
     * @author Ilya Solovyev
     * @since 0.0.0
     * @param day день
     * @return номер недели, содержащей этот день (< 0, если [day] < [firstDay])
     */
    fun getWeekNumber(day: LocalDate) =
        Weeks.weeksBetween(firstWeekMonday, day).weeks - if (day >= firstWeekMonday) 0 else 1

    override fun compareTo(other: SemesterNew) = ComparisonChain.start()
        .compare(lastDay, other.lastDay)
        .compare(firstDay, other.firstDay)
        .compare(name, other.name)
        .compare(id, other.id)
        .result()
}
