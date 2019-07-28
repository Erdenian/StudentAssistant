package ru.erdenian.studentassistant.model.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.Period
import ru.erdenian.studentassistant.model.Converters
import ru.erdenian.studentassistant.model.entity.Lesson
import ru.erdenian.studentassistant.model.entity.LessonRepeat
import ru.erdenian.studentassistant.model.entity.Semester

@Suppress("TooManyFunctions", "MaxLineLength")
@Dao
abstract class LessonDao {

    private companion object {
        const val DEFAULT_DURATION_MILLIS = 5_400_000
        val DEFAULT_BREAK_LENGTH: Period = Period.minutes(10)
        val DEFAULT_START_TIME = LocalTime(9, 0)
    }

    // region Primary actions

    @Transaction
    open suspend fun insert(lesson: Lesson) = withContext(Dispatchers.IO) {
        val oldLesson = get(lesson.semesterId, lesson.id)
        insertLesson(lesson)
        if (
            (oldLesson != null) &&
            (oldLesson.subjectName != lesson.subjectName) &&
            !hasLessons(lesson.semesterId, oldLesson.subjectName)
        ) renameSubject(lesson.semesterId, oldLesson.subjectName, lesson.subjectName)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertLesson(lesson: Lesson)

    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId AND _id = :lessonId")
    abstract suspend fun get(semesterId: Long, lessonId: Long): Lesson?

    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId AND _id = :lessonId")
    abstract fun getLive(semesterId: Long, lessonId: Long): LiveData<Lesson?>

    @Transaction
    open suspend fun delete(lesson: Lesson) = withContext(Dispatchers.IO) {
        deleteLesson(lesson)
        if (!hasLessons(lesson.semesterId, lesson.subjectName)) {
            deleteHomeworks(lesson.semesterId, lesson.subjectName)
        }
    }

    @Delete
    protected abstract suspend fun deleteLesson(lesson: Lesson)

    @Query("DELETE FROM homeworks WHERE semester_id = :semesterId AND subject_name = :subjectName")
    protected abstract suspend fun deleteHomeworks(semesterId: Long, subjectName: String)

    // endregion

    // region Lessons

    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId ORDER BY start_time, end_time, _id")
    abstract fun get(semesterId: Long): LiveData<List<Lesson>>

    fun get(semester: Semester, day: LocalDate) = get(semester.id).map { lessons ->
        val weekNumber = semester.getWeekNumber(day)
        lessons.filter { it.lessonRepeat.repeatsOnDay(day, weekNumber) }
    }

    fun get(semesterId: Long, weekday: Int) = get(semesterId).map { lessons ->
        lessons.filter { lesson ->
            if (lesson.lessonRepeat !is LessonRepeat.ByWeekday) false
            else lesson.lessonRepeat.repeatsOnWeekday(weekday)
        }
    }

    @Query("SELECT COUNT(_id) FROM lessons WHERE semester_id = :semesterId")
    abstract suspend fun getCount(semesterId: Long): Int

    @Query("SELECT EXISTS(SELECT _id FROM lessons WHERE semester_id = :semesterId)")
    abstract fun hasLessons(semesterId: Long): LiveData<Boolean>

    // endregion

    // region Subjects

    @Query("SELECT COUNT(_id) FROM lessons WHERE semester_id = :semesterId AND subject_name = :subjectName")
    abstract suspend fun getCount(semesterId: Long, subjectName: String): Int

    @Query("SELECT COUNT(_id) > 0 FROM lessons WHERE semester_id = :semesterId AND subject_name = :subjectName")
    protected abstract suspend fun hasLessons(semesterId: Long, subjectName: String): Boolean

    @Query("SELECT DISTINCT subject_name FROM lessons WHERE semester_id = :semesterId ORDER BY subject_name")
    abstract fun getSubjects(semesterId: Long): LiveData<List<String>>

    @Transaction
    open suspend fun renameSubject(semesterId: Long, oldName: String, newName: String) =
        withContext(Dispatchers.IO) {
            renameLessonsSubject(semesterId, oldName, newName)
            renameHomeworksSubject(semesterId, oldName, newName)
        }

    @Query("UPDATE lessons SET subject_name = :newName WHERE semester_id = :semesterId AND subject_name = :oldName")
    protected abstract suspend fun renameLessonsSubject(
        semesterId: Long,
        oldName: String,
        newName: String
    )

    @Query("UPDATE homeworks SET subject_name = :newName WHERE semester_id = :semesterId AND subject_name = :oldName")
    protected abstract suspend fun renameHomeworksSubject(
        semesterId: Long,
        oldName: String,
        newName: String
    )

    // endregion

    // region Other fields

    @Query("SELECT DISTINCT type FROM lessons WHERE type IS NOT NULL AND semester_id = :semesterId ORDER BY type")
    abstract fun getTypes(semesterId: Long): LiveData<List<String>>

    @Query("SELECT teachers FROM lessons WHERE semester_id = :semesterId")
    protected abstract fun getTeachersRaw(semesterId: Long): LiveData<List<String>>

    fun getTeachers(semesterId: Long): LiveData<List<String>> =
        getTeachersRaw(semesterId).map { rawTeachers ->
            rawTeachers.asSequence()
                .flatMap { it.splitToSequence(Converters.SEPARATOR) }
                .distinct()
                .sorted()
                .toList()
        }

    @Query("SELECT classrooms FROM lessons WHERE semester_id = :semesterId")
    protected abstract fun getClassroomsRaw(semesterId: Long): LiveData<List<String>>

    fun getClassrooms(semesterId: Long): LiveData<List<String>> =
        getClassroomsRaw(semesterId).map { rawClassrooms ->
            rawClassrooms.asSequence()
                .flatMap { it.splitToSequence(Converters.SEPARATOR) }
                .distinct()
                .sorted()
                .toList()
        }

    @Query("SELECT IFNULL((SELECT end_time - start_time as duration FROM lessons WHERE semester_id = :semesterId GROUP BY duration ORDER BY COUNT(duration) DESC LIMIT 1), $DEFAULT_DURATION_MILLIS)")
    abstract suspend fun getLessonLength(semesterId: Long): Period

    @Transaction
    open suspend fun getNextStartTime(semesterId: Long, weekday: Int): LocalTime =
        withContext(Dispatchers.IO) {
            val lessons = getNextStartTimeRaw(semesterId).filter { lesson ->
                lesson.lessonRepeat is LessonRepeat.ByWeekday
            }
            if (lessons.isEmpty()) DEFAULT_START_TIME
            else {
                val breakLength = lessons.asSequence()
                    .groupBy { it.lessonRepeat }
                    .flatMap { (_, lessons) ->
                        lessons.zipWithNext().map { (a, b) ->
                            Period.fieldDifference(a.endTime, b.startTime)
                        }
                    }.groupingBy { it.normalizedStandard() }
                    .eachCount()
                    .maxBy { it.value }?.key ?: DEFAULT_BREAK_LENGTH
                lessons.last().endTime + breakLength
            }
        }

    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId ORDER BY start_time, end_time, _id")
    protected abstract suspend fun getNextStartTimeRaw(semesterId: Long): List<Lesson>

    // endregion
}
