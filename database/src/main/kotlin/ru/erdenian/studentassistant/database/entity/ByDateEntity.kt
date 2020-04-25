package ru.erdenian.studentassistant.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import kotlinx.android.parcel.Parcelize
import org.joda.time.LocalDate

@Entity(
    tableName = "by_date",
    primaryKeys = ["lesson_id", "date"],
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["_id"],
            childColumns = ["lesson_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("lesson_id")]
)
@Parcelize
data class ByDateEntity(

    @ColumnInfo(name = "date")
    val date: LocalDate,

    @ColumnInfo(name = "lesson_id")
    val lessonId: Long
) : Parcelable
