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
    val byDates: List<ByDateEntity>,
) : Parcelable {

    init {
        require((byWeekday != null) xor byDates.isNotEmpty())
    }

    fun toLesson() = Lesson(
        subjectName = lesson.subjectName,
        type = lesson.type,
        teachers = teachers.map { it.name },
        classrooms = classrooms.map { it.name },
        startTime = lesson.startTime,
        endTime = lesson.endTime,
        lessonRepeat = byWeekday?.toLessonRepeat() ?: byDates.toLessonRepeat(),
        semesterId = lesson.semesterId,
        id = lesson.id,
    )

    private fun ByWeekdayEntity.toLessonRepeat() = Lesson.Repeat.ByWeekday(dayOfWeek, weeks)
    private fun List<ByDateEntity>.toLessonRepeat() =
        byDates.asSequence().map { it.date }.toSet().let(Lesson.Repeat::ByDates)
}
