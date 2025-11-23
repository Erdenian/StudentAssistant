package ru.erdenian.studentassistant.repository.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import java.time.LocalDate
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "by_date",
    primaryKeys = ["lesson_id", "date"],
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
internal data class ByDateEntity(

    @ColumnInfo(name = "date")
    val date: LocalDate,

    @Suppress("DataClassShouldBeImmutable")
    @ColumnInfo(name = "lesson_id", index = true)
    var lessonId: Long = 0L,
) : Parcelable
