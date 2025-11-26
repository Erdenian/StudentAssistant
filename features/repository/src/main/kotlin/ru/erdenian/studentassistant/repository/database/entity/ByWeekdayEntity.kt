package ru.erdenian.studentassistant.repository.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.DayOfWeek
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "by_weekday",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["_id"],
            childColumns = ["lesson_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
    ],
)
@Parcelize
internal data class ByWeekdayEntity(

    @ColumnInfo(name = "day_of_week")
    val dayOfWeek: DayOfWeek,

    @ColumnInfo(name = "weeks")
    val weeks: List<Boolean>,

    @Suppress("DataClassShouldBeImmutable")
    @PrimaryKey
    @ColumnInfo(name = "lesson_id", index = true)
    var lessonId: Long = 0L,
) : Parcelable {

    init {
        require(weeks.isNotEmpty()) { "Список недель пуст" }
        require(weeks.contains(true)) { "Нет повторений ни на одной неделе" }
    }
}
