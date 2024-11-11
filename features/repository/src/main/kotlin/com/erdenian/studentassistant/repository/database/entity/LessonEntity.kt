package com.erdenian.studentassistant.repository.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalTime
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "lessons",
    foreignKeys = [
        ForeignKey(
            entity = SemesterEntity::class,
            parentColumns = ["_id"],
            childColumns = ["semester_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
    ],
)
@Parcelize
internal data class LessonEntity(

    @ColumnInfo(name = "subject_name", index = true)
    val subjectName: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "start_time")
    val startTime: LocalTime,

    @ColumnInfo(name = "end_time")
    val endTime: LocalTime,

    @ColumnInfo(name = "semester_id", index = true)
    val semesterId: Long,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0L,
) : Parcelable {

    init {
        require(subjectName.isNotBlank()) { "Отсутствует название предмета" }
        require(type.isEmpty() || type.isNotBlank())
        require(startTime < endTime) { "Неверно заданы даты: $startTime - $endTime" }
    }
}
