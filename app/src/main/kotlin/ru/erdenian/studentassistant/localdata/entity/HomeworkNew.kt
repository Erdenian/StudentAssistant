package ru.erdenian.studentassistant.localdata.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.common.collect.ComparisonChain
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

    @PrimaryKey
    @ColumnInfo(name = "_id")
    val id: Long = generateId(),

    @ColumnInfo(name = "semester_id")
    val semesterId: Long
) : Comparable<HomeworkNew>, Parcelable {

    init {
        if (subjectName.isBlank()) throw IllegalArgumentException("Пустое название предмета")
        if (description.isBlank()) throw IllegalArgumentException("Пустое описание")
    }

    override fun compareTo(other: HomeworkNew) = ComparisonChain.start()
        .compare(deadline, other.deadline)
        .compare(id, other.id)
        .result()
}