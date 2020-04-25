package ru.erdenian.studentassistant.database.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.Period
import ru.erdenian.studentassistant.database.entity.ByDateEntity
import ru.erdenian.studentassistant.database.entity.ByWeekdayEntity
import ru.erdenian.studentassistant.database.entity.ClassroomEntity
import ru.erdenian.studentassistant.database.entity.FullLesson
import ru.erdenian.studentassistant.database.entity.LessonEntity
import ru.erdenian.studentassistant.database.entity.SemesterEntity
import ru.erdenian.studentassistant.database.entity.TeacherEntity

@Dao
abstract class LessonDao {

    private companion object {
        const val DEFAULT_DURATION_MILLIS = 5_400_000
        val DEFAULT_BREAK_LENGTH: Period = Period.minutes(10)
        val DEFAULT_START_TIME = LocalTime(9, 0)
    }

    // region Primary actions

    @Transaction
    open suspend fun insert(
        lesson: LessonEntity,
        teachers: List<TeacherEntity>,
        classrooms: List<ClassroomEntity>,
        byWeekday: ByWeekdayEntity
    ): Long = withContext(Dispatchers.IO) {
        val id = insert(lesson)
        insert(
            teachers.apply { forEach { it.lessonId = id } },
            classrooms.apply { forEach { it.lessonId = id } },
            byWeekday.apply { lessonId = id },
            emptyList()
        )
    }

    @Transaction
    open suspend fun insert(
        lesson: LessonEntity,
        teachers: List<TeacherEntity>,
        classrooms: List<ClassroomEntity>,
        byDates: List<ByDateEntity>
    ): Long = withContext(Dispatchers.IO) {
        val id = insert(lesson)
        insert(
            teachers.apply { forEach { it.lessonId = id } },
            classrooms.apply { forEach { it.lessonId = id } },
            null,
            byDates.apply { forEach { it.lessonId = id } }
        )
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    protected abstract suspend fun insert(lesson: LessonEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    protected abstract suspend fun insert(
        teachers: List<TeacherEntity>,
        classrooms: List<ClassroomEntity>,
        byWeekday: ByWeekdayEntity?,
        byDates: List<ByDateEntity>
    ): Long

    @Transaction
    open suspend fun update(
        lesson: LessonEntity,
        teachers: List<TeacherEntity>,
        classrooms: List<ClassroomEntity>,
        byWeekday: ByWeekdayEntity
    ) = withContext(Dispatchers.IO) {
        delete(lesson.id)
        insert(lesson, teachers, classrooms, byWeekday)
    }

    @Transaction
    open suspend fun update(
        lesson: LessonEntity,
        teachers: List<TeacherEntity>,
        classrooms: List<ClassroomEntity>,
        byDates: List<ByDateEntity>
    ) = withContext(Dispatchers.IO) {
        delete(lesson.id)
        insert(lesson, teachers, classrooms, byDates)
    }

    @Query("DELETE FROM lessons WHERE _id = :id")
    abstract suspend fun delete(id: Long)

    // endregion

    // region Lessons

    @Transaction
    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId AND _id = :lessonId")
    abstract suspend fun get(semesterId: Long, lessonId: Long): FullLesson?

    @Transaction
    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId AND _id = :lessonId")
    abstract fun getLive(semesterId: Long, lessonId: Long): LiveData<FullLesson?>

    @Transaction
    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId ORDER BY start_time, end_time, _id")
    abstract fun get(semesterId: Long): LiveData<List<FullLesson>>

    @Transaction
    fun get(semester: SemesterEntity, day: LocalDate): LiveData<List<FullLesson>> = get(semester.id).map { lessons ->
        val weekNumber = semester.getWeekNumber(day)
        lessons.filter { it.lessonRepeat.repeatsOnDay(day, weekNumber) }
    }

    @Transaction
    fun get(semesterId: Long, weekday: Int): LiveData<List<FullLesson>> = get(semesterId).map { lessons ->
        lessons.filter { (it.lessonRepeat as? ByWeekdayEntity)?.repeatsOnWeekday(weekday) ?: false }
    }

    @Query("SELECT COUNT(_id) FROM lessons WHERE semester_id = :semesterId")
    abstract suspend fun getCount(semesterId: Long): Int

    @Query("SELECT EXISTS(SELECT _id FROM lessons WHERE semester_id = :semesterId)")
    abstract fun hasLessons(semesterId: Long): LiveData<Boolean>

    // endregion

    // region Subjects

    @Query("SELECT COUNT(_id) FROM lessons WHERE semester_id = :semesterId AND subject_name = :subjectName")
    abstract suspend fun getCount(semesterId: Long, subjectName: String): Int

    @Query("SELECT DISTINCT subject_name FROM lessons WHERE semester_id = :semesterId ORDER BY subject_name")
    abstract fun getSubjects(semesterId: Long): LiveData<List<String>>

    @Transaction
    open suspend fun renameSubject(semesterId: Long, oldName: String, newName: String) = withContext(Dispatchers.IO) {
        renameLessonsSubject(semesterId, oldName, newName)
        renameHomeworksSubject(semesterId, oldName, newName)
    }

    @Query("UPDATE lessons SET subject_name = :newName WHERE semester_id = :semesterId AND subject_name = :oldName")
    protected abstract suspend fun renameLessonsSubject(semesterId: Long, oldName: String, newName: String)

    @Query("UPDATE homeworks SET subject_name = :newName WHERE semester_id = :semesterId AND subject_name = :oldName")
    protected abstract suspend fun renameHomeworksSubject(semesterId: Long, oldName: String, newName: String)

    // endregion

    // region Other fields

    @Query("SELECT DISTINCT type FROM lessons WHERE type IS NOT NULL AND semester_id = :semesterId ORDER BY type")
    abstract fun getTypes(semesterId: Long): LiveData<List<String>>

    @Query("SELECT DISTINCT t.name FROM teachers AS t INNER JOIN lessons AS l ON l._id = t.lesson_id WHERE l.semester_id = :semesterId ORDER BY t.name")
    protected abstract fun getTeachers(semesterId: Long): LiveData<List<String>>

    @Query("SELECT DISTINCT c.name FROM classrooms AS c INNER JOIN lessons AS l ON l._id = c.lesson_id WHERE l.semester_id = :semesterId ORDER BY c.name")
    protected abstract fun getClassrooms(semesterId: Long): LiveData<List<String>>

    @Query("SELECT end_time - start_time as duration FROM lessons WHERE semester_id = :semesterId GROUP BY duration ORDER BY COUNT(duration) DESC LIMIT 1")
    abstract suspend fun getLessonLength(semesterId: Long): Period?

    @Transaction
    open suspend fun getNextStartTime(semesterId: Long, weekday: Int): LocalTime? = withContext(Dispatchers.IO) {
        val lessons = getNextStartTimeRaw(semesterId).filter { lesson ->
            (lesson.lessonRepeat as? ByWeekdayEntity)?.repeatsOnWeekday(weekday) ?: false
        }

        val breakLength = lessons.asSequence()
            .groupBy { it.lessonRepeat }
            .flatMap { (_, lessons) ->
                lessons.asSequence()
                    .zipWithNext()
                    .map { (a, b) -> Period.fieldDifference(a.endTime, b.startTime) }
                    .toList()
            }.groupingBy { it.normalizedStandard() }
            .eachCount()
            .maxBy { it.value }?.key ?: DEFAULT_BREAK_LENGTH
        lessons.lastOrNull()?.run { endTime + breakLength }
    }

    @Transaction
    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId ORDER BY start_time, end_time, _id")
    protected abstract suspend fun getNextStartTimeRaw(semesterId: Long): List<FullLesson>

    // endregion
}
