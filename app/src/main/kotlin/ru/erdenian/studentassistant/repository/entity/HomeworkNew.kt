package ru.erdenian.studentassistant.repository.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.schedule.generateId

/**
 * Класс домашнего задания.
 *
 * @author Ilya Solovyev
 * @since 0.0.0
 * @property subjectName название предмета, по которому задано задание
 * @property description описание задания
 * @property deadline срок сдачи
 * @property id уникальный id задания
 * @throws IllegalArgumentException если [subjectName] или [description] пусты
 */
@Parcelize
@Entity(
    tableName = "homeworks",
    foreignKeys = [
        ForeignKey(
            entity = SemesterNew::class,
            parentColumns = ["_id"],
            childColumns = ["semester_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("semester_id")]
)
data class HomeworkNew(

    @ColumnInfo(name = "subject_name")
    val subjectName: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "deadline")
    val deadline: LocalDate,

    @ColumnInfo(name = "semester_id")
    val semesterId: Long,

    @PrimaryKey
    @ColumnInfo(name = "_id")
    val id: Long = generateId()
) : Comparable<HomeworkNew>, Parcelable {

    init {
        require(subjectName.isNotBlank()) { "Пустое название предмета" }
        require(description.isNotBlank()) { "Пустое описание" }
    }

    override fun compareTo(other: HomeworkNew) = compareValuesBy(
        this, other,
        HomeworkNew::deadline,
        HomeworkNew::id
    )
}
