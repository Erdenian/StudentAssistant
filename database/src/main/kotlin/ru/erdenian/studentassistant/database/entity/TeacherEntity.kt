package ru.erdenian.studentassistant.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

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
    ]
)
@Parcelize
data class TeacherEntity(

    @ColumnInfo(name = "name")
    val name: String,

    @Suppress("DataClassShouldBeImmutable")
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
