package ru.erdenian.studentassistant.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.entity.generateId

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
    ],
    indices = [Index("semester_id"), Index("subject_name")]
)
@Parcelize
data class HomeworkEntity(

    @ColumnInfo(name = "subject_name")
    override val subjectName: String,

    @ColumnInfo(name = "description")
    override val description: String,

    @ColumnInfo(name = "deadline")
    override val deadline: LocalDate,

    @ColumnInfo(name = "semester_id")
    override val semesterId: Long,

    @PrimaryKey
    @ColumnInfo(name = "_id")
    override val id: Long = generateId()
) : Homework {

    init {
        require(subjectName.isNotBlank()) { "Пустое название предмета" }
        require(description.isNotBlank()) { "Пустое описание" }
    }
}
