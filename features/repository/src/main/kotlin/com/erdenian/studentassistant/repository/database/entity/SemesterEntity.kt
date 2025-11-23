package ru.erdenian.studentassistant.repository.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import kotlinx.parcelize.Parcelize
import ru.erdenian.studentassistant.repository.api.entity.Semester

@Entity(
    tableName = "semesters",
    indices = [Index("name", unique = true)],
)
@Parcelize
internal data class SemesterEntity(

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "first_day")
    val firstDay: LocalDate,

    @ColumnInfo(name = "last_day")
    val lastDay: LocalDate,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0L,
) : Parcelable {

    init {
        require(name.isNotBlank()) { "Пустое название" }
        require(firstDay <= lastDay) { "Неверно заданы даты: $firstDay - $lastDay" }
    }

    fun toSemester() = Semester(
        name = name,
        firstDay = firstDay,
        lastDay = lastDay,
        id = id,
    )
}
