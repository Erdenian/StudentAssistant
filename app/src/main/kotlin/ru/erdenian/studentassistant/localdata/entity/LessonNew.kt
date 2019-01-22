package ru.erdenian.studentassistant.localdata.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.common.collect.ComparisonChain
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.schedule.LessonRepeat
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
@Entity(tableName = "lessons")
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
    val lessonRepeat: LessonRepeat,

    @PrimaryKey
    @ColumnInfo(name = "_id")
    val id: Long = generateId(),

    @ColumnInfo(name = "semester_id")
    val semesterId: Long
) : Comparable<LessonNew> {

    init {
        if (subjectName.isBlank()) throw IllegalArgumentException("Отсутствует название предмета")
        if (startTime >= endTime) throw IllegalArgumentException("Неверно заданы даты: $startTime - $endTime")
    }

    override fun compareTo(other: LessonNew) = ComparisonChain.start()
        .compare(startTime, other.startTime)
        .compare(endTime, other.endTime)
        .compare(id, other.id)
        .result()

    fun test(other: LessonNew): Int {
        return compareValuesBy(
            this,
            other,
            compareBy(nullsFirst(), LessonNew::startTime).thenBy(LessonNew::endTime),
            { it }
        )
    }
}
