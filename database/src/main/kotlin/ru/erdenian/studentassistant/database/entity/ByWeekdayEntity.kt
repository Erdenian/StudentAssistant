package ru.erdenian.studentassistant.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import org.joda.time.DateTimeConstants
import ru.erdenian.studentassistant.entity.Lesson

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

    @ColumnInfo(name = "weekday")
    override val weekday: Int,

    @ColumnInfo(name = "weeks")
    override val weeks: List<Boolean>,

    @Suppress("DataClassShouldBeImmutable")
    @PrimaryKey
    @ColumnInfo(name = "lesson_id", index = true)
    var lessonId: Long = 0L
) : Lesson.Repeat.ByWeekday() {

    init {
        require(weekday in DateTimeConstants.MONDAY..DateTimeConstants.SUNDAY) { "Неверный день недели: $weekday" }
        require(weeks.isNotEmpty()) { "Список недель пуст" }
        require(weeks.contains(true)) { "Нет повторений ни на одной неделе" }
    }
}
