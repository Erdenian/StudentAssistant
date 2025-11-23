package ru.erdenian.studentassistant.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import java.time.DayOfWeek
import java.time.LocalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import ru.erdenian.studentassistant.repository.database.entity.ByDateEntity
import ru.erdenian.studentassistant.repository.database.entity.ByWeekdayEntity
import ru.erdenian.studentassistant.repository.database.entity.ClassroomEntity
import ru.erdenian.studentassistant.repository.database.entity.FullLesson
import ru.erdenian.studentassistant.repository.database.entity.LessonEntity
import ru.erdenian.studentassistant.repository.database.entity.TeacherEntity

@Dao
internal abstract class LessonDao {

    // region Primary actions

    @Transaction
    open suspend fun insert(
        lesson: LessonEntity,
        teachers: Set<TeacherEntity>,
        classrooms: Set<ClassroomEntity>,
        byWeekday: ByWeekdayEntity,
    ): Long = withContext(Dispatchers.IO) {
        val id = insert(lesson)
        insert(
            teachers.onEach { it.lessonId = id },
            classrooms.onEach { it.lessonId = id },
            byWeekday.apply { lessonId = id },
        )
        id
    }

    @Transaction
    open suspend fun insert(
        lesson: LessonEntity,
        teachers: Set<TeacherEntity>,
        classrooms: Set<ClassroomEntity>,
        byDates: Set<ByDateEntity>,
    ): Long {
        require(byDates.isNotEmpty()) { "Dates list must contain at least one item" }
        return withContext(Dispatchers.IO) {
            val id = insert(lesson)
            insert(
                teachers.onEach { it.lessonId = id },
                classrooms.onEach { it.lessonId = id },
                byDates.onEach { it.lessonId = id },
            )
            id
        }
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    protected abstract suspend fun insert(lesson: LessonEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    protected abstract suspend fun insert(
        teachers: Set<TeacherEntity>,
        classrooms: Set<ClassroomEntity>,
        byWeekday: ByWeekdayEntity,
    )

    @Insert(onConflict = OnConflictStrategy.ABORT)
    protected abstract suspend fun insert(
        teachers: Set<TeacherEntity>,
        classrooms: Set<ClassroomEntity>,
        byDates: Set<ByDateEntity>,
    )

    @Transaction
    open suspend fun update(
        lesson: LessonEntity,
        teachers: Set<TeacherEntity>,
        classrooms: Set<ClassroomEntity>,
        byWeekday: ByWeekdayEntity,
    ): Unit = withContext(Dispatchers.IO) {
        delete(lesson.id)
        insert(lesson = lesson, teachers = teachers, classrooms = classrooms, byWeekday = byWeekday)
    }

    @Transaction
    open suspend fun update(
        lesson: LessonEntity,
        teachers: Set<TeacherEntity>,
        classrooms: Set<ClassroomEntity>,
        byDates: Set<ByDateEntity>,
    ): Unit = withContext(Dispatchers.IO) {
        delete(lesson.id)
        insert(lesson = lesson, teachers = teachers, classrooms = classrooms, byDates = byDates)
    }

    @Query("DELETE FROM lessons WHERE _id = :id")
    abstract suspend fun delete(id: Long)

    // endregion

    // region Lessons

    @Transaction
    @Query("SELECT * FROM lessons WHERE _id = :id")
    abstract suspend fun get(id: Long): FullLesson?

    @Transaction
    @Query("SELECT * FROM lessons WHERE _id = :id")
    abstract fun getFlow(id: Long): Flow<FullLesson?>

    @Transaction
    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId ORDER BY start_time, end_time, subject_name, type, _id, semester_id")
    abstract fun getAllFlow(semesterId: Long): Flow<List<FullLesson>>

    @Transaction
    @Query("SELECT lessons.* FROM lessons INNER JOIN by_weekday ON by_weekday.lesson_id = lessons._id WHERE semester_id = :semesterId AND day_of_week = :dayOfWeek ORDER BY start_time, end_time, subject_name, type, _id, semester_id")
    abstract fun getAllFlow(semesterId: Long, dayOfWeek: DayOfWeek): Flow<List<FullLesson>>

    @Query("SELECT COUNT(_id) FROM lessons WHERE semester_id = :semesterId")
    abstract suspend fun getCount(semesterId: Long): Int

    @Query("SELECT EXISTS(SELECT _id FROM lessons WHERE semester_id = :semesterId)")
    abstract fun hasLessonsFlow(semesterId: Long): Flow<Boolean>

    // endregion

    // region Subjects

    @Query("SELECT COUNT(_id) FROM lessons WHERE semester_id = :semesterId AND subject_name = :subjectName")
    abstract suspend fun getCount(semesterId: Long, subjectName: String): Int

    @Query("SELECT DISTINCT subject_name FROM lessons WHERE semester_id = :semesterId ORDER BY subject_name")
    abstract fun getSubjectsFlow(semesterId: Long): Flow<List<String>>

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
    abstract fun getTypesFlow(semesterId: Long): Flow<List<String>>

    @Query("SELECT DISTINCT t.name FROM teachers AS t INNER JOIN lessons AS l ON l._id = t.lesson_id WHERE l.semester_id = :semesterId ORDER BY t.name")
    abstract fun getTeachersFlow(semesterId: Long): Flow<List<String>>

    @Query("SELECT DISTINCT c.name FROM classrooms AS c INNER JOIN lessons AS l ON l._id = c.lesson_id WHERE l.semester_id = :semesterId ORDER BY c.name")
    abstract fun getClassroomsFlow(semesterId: Long): Flow<List<String>>

    @Query("SELECT l.end_time FROM lessons AS l INNER JOIN by_weekday AS w ON w.lesson_id = l._id WHERE l.semester_id = :semesterId AND w.day_of_week = :dayOfWeek ORDER BY end_time DESC LIMIT 1")
    abstract suspend fun getLastEndTime(semesterId: Long, dayOfWeek: DayOfWeek): LocalTime?

    // endregion
}
