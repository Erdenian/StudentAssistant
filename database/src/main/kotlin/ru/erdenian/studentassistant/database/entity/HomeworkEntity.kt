package ru.erdenian.studentassistant.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.entity.Homework

@Entity(
    tableName = "homeworks",
    foreignKeys = [
        ForeignKey(
            entity = SemesterEntity::class,
            parentColumns = ["_id"],
            childColumns = ["semester_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
@Parcelize
data class HomeworkEntity(

    @ColumnInfo(name = "subject_name", index = true)
    override val subjectName: String,

    @ColumnInfo(name = "description")
    override val description: String,

    @ColumnInfo(name = "deadline")
    override val deadline: LocalDate,

    @ColumnInfo(name = "semester_id", index = true)
    override val semesterId: Long,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    override val id: Long = 0L
) : Homework {

    init {
        require(subjectName.isNotBlank()) { "Пустое название предмета" }
        require(description.isNotBlank()) { "Пустое описание" }
    }
}
