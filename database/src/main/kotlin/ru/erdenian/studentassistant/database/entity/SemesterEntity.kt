package ru.erdenian.studentassistant.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.entity.generateId

@Entity(tableName = "semesters")
@Parcelize
data class SemesterEntity(

    @ColumnInfo(name = "name")
    override val name: String,

    @ColumnInfo(name = "first_day")
    override val firstDay: LocalDate,

    @ColumnInfo(name = "last_day")
    override val lastDay: LocalDate,

    @PrimaryKey
    @ColumnInfo(name = "_id")
    override val id: Long = generateId()
) : Semester {

    init {
        require(name.isNotBlank()) { "Пустое название" }
        require(firstDay < lastDay) { "Неверно заданы даты: $firstDay - $lastDay" }
    }
}
