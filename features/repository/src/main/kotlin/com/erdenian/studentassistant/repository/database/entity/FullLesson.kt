package com.erdenian.studentassistant.repository.database.entity

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.erdenian.studentassistant.repository.api.entity.Lesson
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class FullLesson(

    @Embedded
    val lesson: LessonEntity,

    @Relation(
        parentColumn = "_id",
        entityColumn = "lesson_id",
    )
    val teachers: List<TeacherEntity>,

    @Relation(
        parentColumn = "_id",
        entityColumn = "lesson_id",
    )
    val classrooms: List<ClassroomEntity>,

    @Relation(
        parentColumn = "_id",
        entityColumn = "lesson_id",
    )
    val byWeekday: ByWeekdayEntity?,

    @Relation(
        parentColumn = "_id",
        entityColumn = "lesson_id",
    )
    val byDates: Set<ByDateEntity>,
) : Parcelable {

    init {
        require((byWeekday != null) xor byDates.isNotEmpty())
    }

    fun toLesson() = Lesson(
        subjectName = lesson.subjectName,
        type = lesson.type,
        teachers = teachers.asSequence().map { it.name }.sorted().toList(),
        classrooms = classrooms.asSequence().map { it.name }.sorted().toList(),
        startTime = lesson.startTime,
        endTime = lesson.endTime,
        lessonRepeat = byWeekday?.toLessonRepeat() ?: byDates.toLessonRepeat(),
        semesterId = lesson.semesterId,
        id = lesson.id,
    )

    private fun ByWeekdayEntity.toLessonRepeat() = Lesson.Repeat.ByWeekday(dayOfWeek, weeks)
    private fun Set<ByDateEntity>.toLessonRepeat() =
        Lesson.Repeat.ByDates(byDates.asSequence().map { it.date }.toSet())
}
