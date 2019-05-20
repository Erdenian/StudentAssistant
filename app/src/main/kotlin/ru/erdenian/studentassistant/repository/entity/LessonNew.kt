package ru.erdenian.studentassistant.repository.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.schedule.generateId

/**
 * Класс пары (урока).
 *
 * @author Ilya Solovyev
 * @since 0.0.0
 * @param subjectName название предмета
 * @param type тип пары (лекция, семинар, ...)
 * @param teachers список преподавателей, ведущих пару
 * @param classrooms список аудиторий, в которых проходит пара
 * @param startTime время начала
 * @param endTime время конца
 * @param lessonRepeat когда повторяется пара
 * @param id уникальный id пары
 * @throws IllegalArgumentException если [subjectName] пусто или [startTime] >= [endTime]
 */
@Parcelize
@Entity(
    tableName = "lessons",
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
data class LessonNew(

    @ColumnInfo(name = "subject_name")
    val subjectName: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "teachers")
    val teachers: List<String>,

    @ColumnInfo(name = "classrooms")
    val classrooms: List<String>,

    @ColumnInfo(name = "start_time")
    val startTime: LocalTime,

    @ColumnInfo(name = "end_time")
    val endTime: LocalTime,

    @ColumnInfo(name = "lesson_repeat")
    val lessonRepeat: LessonRepeatNew,

    @PrimaryKey
    @ColumnInfo(name = "_id")
    val id: Long = generateId(),

    @ColumnInfo(name = "semester_id")
    val semesterId: Long
) : Comparable<LessonNew>, Parcelable {

    init {
        require(subjectName.isNotBlank()) { "Отсутствует название предмета" }
        require(startTime < endTime) { "Неверно заданы даты: $startTime - $endTime" }
    }

    override fun compareTo(other: LessonNew) = compareValuesBy(
        this, other,
        LessonNew::startTime, LessonNew::endTime, LessonNew::id
    )
}
