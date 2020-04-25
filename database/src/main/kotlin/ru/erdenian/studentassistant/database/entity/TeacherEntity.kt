package ru.erdenian.studentassistant.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(
    tableName = "teachers",
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
data class TeacherEntity(

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "lesson_id")
    var lessonId: Long = 0L,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0L
) : Parcelable {
    init {
        require(name.isNotBlank())
    }
}
