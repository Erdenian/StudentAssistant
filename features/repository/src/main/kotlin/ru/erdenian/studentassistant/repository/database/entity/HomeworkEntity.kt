package ru.erdenian.studentassistant.repository.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import kotlinx.parcelize.Parcelize
import ru.erdenian.studentassistant.repository.api.entity.Homework

@Entity(
    tableName = "homeworks",
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
internal data class HomeworkEntity(

    @ColumnInfo(name = "subject_name", index = true)
    val subjectName: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "deadline")
    val deadline: LocalDate,

    @ColumnInfo(name = "semester_id", index = true)
    val semesterId: Long,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0L,

    @ColumnInfo(name = "is_done")
    val isDone: Boolean = false,
) : Parcelable {

    init {
        require(subjectName.isNotBlank()) { "Пустое название предмета" }
        require(description.isNotBlank()) { "Пустое описание" }
    }

    fun toHomework() = Homework(
        subjectName = subjectName,
        description = description,
        deadline = deadline,
        isDone = isDone,
        semesterId = semesterId,
        id = id,
    )
}
