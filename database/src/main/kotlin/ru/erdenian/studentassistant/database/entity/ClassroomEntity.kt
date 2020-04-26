package ru.erdenian.studentassistant.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(
    tableName = "classrooms",
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
data class ClassroomEntity(

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "lesson_id", index = true)
    var lessonId: Long = 0L,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0L
) : Parcelable {
    init {
        require(name.isNotBlank())
    }
}
