package ru.erdenian.studentassistant.database.entity

import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.entity.toImmutableSortedSet

@Parcelize
data class FullLesson(

    @Embedded
    val lesson: LessonEntity,

    @Relation(
        parentColumn = "_id",
        entityColumn = "lesson_id"
    )
    val lessonTeachers: List<TeacherEntity>,

    @Relation(
        parentColumn = "_id",
        entityColumn = "lesson_id"
    )
    val lessonClassrooms: List<ClassroomEntity>,

    @Relation(
        parentColumn = "_id",
        entityColumn = "lesson_id"
    )
    val byWeekday: ByWeekdayEntity?,

    @Relation(
        parentColumn = "_id",
        entityColumn = "lesson_id"
    )
    val byDates: List<ByDateEntity>
) : Lesson {

    override val subjectName get() = lesson.subjectName
    override val type get() = lesson.type
    override val startTime get() = lesson.startTime
    override val endTime get() = lesson.endTime

    override val semesterId get() = lesson.semesterId
    override val id get() = lesson.id

    @IgnoredOnParcel
    override val teachers by lazy { lessonTeachers.asSequence().map { it.name }.toImmutableSortedSet() }

    @IgnoredOnParcel
    override val classrooms by lazy { lessonClassrooms.asSequence().map { it.name }.toImmutableSortedSet() }

    @IgnoredOnParcel
    override val lessonRepeat by lazy { byWeekday ?: ByDatesRepeat(byDates.asSequence().map { it.date }.toSet()) }

    init {
        require((byWeekday != null) xor byDates.isNotEmpty())
    }

    @Parcelize
    private data class ByDatesRepeat(override val dates: Set<LocalDate>) : Lesson.Repeat.ByDates()
}
