package ru.erdenian.studentassistant.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.joda.time.LocalTime

/**
 * Класс пары (урока)
 *
 * @param subjectName название предмета
 * @param type тип пары (лекция, семинар, ...)
 * @param teachers список преподавателей, ведущих пару
 * @param classrooms список аудиторий, в которых проходит пара
 * @param startTime время начала
 * @param endTime время конца
 * @param lessonRepeat когда повторяется пара
 * @param id уникальный id пары
 * @throws IllegalArgumentException если [subjectName] пусто или [startTime] >= [endTime]
 * @author Ilya Solovyov
 * @since 0.0.0
 */
@Parcelize
@Entity(
    tableName = "lessons",
    foreignKeys = [
        ForeignKey(
            entity = Semester::class,
            parentColumns = ["_id"],
            childColumns = ["semester_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("semester_id")]
)
data class Lesson(

    @ColumnInfo(name = "subject_name")
    val subjectName: String,

    @ColumnInfo(name = "type")
    val type: String? = null,

    @ColumnInfo(name = "teachers")
    val teachers: ImmutableSortedSet<String> = immutableSortedSetOf(),

    @ColumnInfo(name = "classrooms")
    val classrooms: ImmutableSortedSet<String> = immutableSortedSetOf(),

    @ColumnInfo(name = "start_time")
    val startTime: LocalTime,

    @ColumnInfo(name = "end_time")
    val endTime: LocalTime,

    @ColumnInfo(name = "lesson_repeat")
    val lessonRepeat: LessonRepeat,

    @ColumnInfo(name = "semester_id")
    val semesterId: Long,

    @PrimaryKey
    @ColumnInfo(name = "_id")
    val id: Long = generateId()
) : Comparable<Lesson>, Parcelable {

    init {
        require(subjectName.isNotBlank()) { "Отсутствует название предмета" }
        require(type?.isNotBlank() ?: true)
        require(teachers.all { it.isNotBlank() })
        require(classrooms.all { it.isNotBlank() })
        require(startTime < endTime) { "Неверно заданы даты: $startTime - $endTime" }
    }

    override fun compareTo(other: Lesson) = compareValuesBy(
        this, other,
        Lesson::startTime,
        Lesson::endTime,
        Lesson::id
    )
}
