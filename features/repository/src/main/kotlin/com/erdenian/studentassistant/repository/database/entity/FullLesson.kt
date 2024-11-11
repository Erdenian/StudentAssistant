package com.erdenian.studentassistant.repository.database.entity

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.erdenian.studentassistant.entity.Lesson
import com.erdenian.studentassistant.entity.toImmutableSortedSet
import java.time.LocalDate
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class FullLesson(

    @Embedded
    val lesson: LessonEntity,

    @Relation(
        parentColumn = "_id",
        entityColumn = "lesson_id",
    )
    val lessonTeachers: List<TeacherEntity>,

    @Relation(
        parentColumn = "_id",
        entityColumn = "lesson_id",
    )
    val lessonClassrooms: List<ClassroomEntity>,

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
) : Lesson {

    override val subjectName get() = lesson.subjectName
    override val type get() = lesson.type
    override val startTime get() = lesson.startTime
    override val endTime get() = lesson.endTime

    override val semesterId get() = lesson.semesterId
    override val id get() = lesson.id

    @delegate:Ignore
    @IgnoredOnParcel
    override val teachers by lazy { lessonTeachers.asSequence().map { it.name }.toImmutableSortedSet() }

    @delegate:Ignore
    @IgnoredOnParcel
    override val classrooms by lazy { lessonClassrooms.asSequence().map { it.name }.toImmutableSortedSet() }

    @delegate:Ignore
    @IgnoredOnParcel
    override val lessonRepeat by lazy { byWeekday ?: ByDatesRepeat(byDates.asSequence().map { it.date }.toSet()) }

    init {
        require((byWeekday != null) xor byDates.isNotEmpty())
    }

    @Parcelize
    private data class ByDatesRepeat(override val dates: Set<LocalDate>) : Lesson.Repeat.ByDates()
}
