package com.erdenian.studentassistant.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.DayOfWeek
import kotlinx.parcelize.Parcelize
import com.erdenian.studentassistant.entity.Lesson

@Entity(
    tableName = "by_weekday",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["_id"],
            childColumns = ["lesson_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
@Parcelize
data class ByWeekdayEntity(

    @ColumnInfo(name = "day_of_week")
    override val dayOfWeek: DayOfWeek,

    @ColumnInfo(name = "weeks")
    override val weeks: List<Boolean>,

    @Suppress("DataClassShouldBeImmutable")
    @PrimaryKey
    @ColumnInfo(name = "lesson_id", index = true)
    var lessonId: Long = 0L
) : Lesson.Repeat.ByWeekday() {

    init {
        require(weeks.isNotEmpty()) { "Список недель пуст" }
        require(weeks.contains(true)) { "Нет повторений ни на одной неделе" }
    }
}
