package ru.erdenian.studentassistant.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.Period
import ru.erdenian.studentassistant.repository.Converters
import ru.erdenian.studentassistant.repository.entity.LessonNew

@Dao
abstract class LessonDao {

    private companion object {
        const val DEFAULT_DURATION_MILLIS = 5_400_000
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(lesson: LessonNew)

    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId AND _id = :lessonId")
    abstract suspend fun get(semesterId: Long, lessonId: Long): LessonNew?

    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId ORDER BY start_time, end_time, _id")
    abstract fun get(semesterId: Long): LiveData<List<LessonNew>>

    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId AND subject_name = :subjectName ORDER BY start_time, end_time, _id")
    abstract suspend fun get(semesterId: Long, subjectName: String): List<LessonNew>

    @Query("SELECT DISTINCT subject_name FROM lessons WHERE semester_id = :semesterId ORDER BY subject_name")
    abstract suspend fun getSubjects(semesterId: Long): List<String>

    @Query("SELECT DISTINCT type FROM lessons WHERE semester_id = :semesterId ORDER BY type")
    abstract suspend fun getTypes(semesterId: Long): List<String>

    @Query("SELECT teachers FROM lessons WHERE semester_id = :semesterId")
    protected abstract suspend fun getTeachersRaw(semesterId: Long): List<String>

    @Transaction
    open suspend fun getTeachers(semesterId: Long): List<String> = withContext(Dispatchers.IO) {
        getTeachersRaw(semesterId).asSequence()
            .flatMap { it.splitToSequence(Converters.SEPARATOR) }
            .distinct()
            .sorted()
            .toList()
    }

    @Query("SELECT classrooms FROM lessons WHERE semester_id = :semesterId")
    protected abstract suspend fun getClassroomsRaw(semesterId: Long): List<String>

    @Transaction
    open suspend fun getClassrooms(semesterId: Long): List<String> = withContext(Dispatchers.IO) {
        getClassroomsRaw(semesterId).asSequence()
            .flatMap { it.splitToSequence(Converters.SEPARATOR) }
            .distinct()
            .sorted()
            .toList()
    }

    @Query("SELECT IFNULL((SELECT end_time - start_time as duration FROM lessons WHERE semester_id = :semesterId GROUP BY duration ORDER BY COUNT(duration) desc), $DEFAULT_DURATION_MILLIS)")
    abstract suspend fun getLessonLength(semesterId: Long): Period

    @Query("SELECT COUNT(_id) FROM lessons WHERE semester_id = :semesterId AND subject_name = :subjectName")
    abstract suspend fun getCount(semesterId: Long, subjectName: String): Int

    @Query("UPDATE lessons SET subject_name = :newName WHERE semester_id = :semesterId AND subject_name = :oldName")
    abstract suspend fun renameSubject(semesterId: Long, oldName: String, newName: String)

    @Delete
    abstract suspend fun delete(lesson: LessonNew)

    @Deprecated("Only for debugging")
    @Query("DELETE FROM lessons")
    abstract suspend fun deleteAll()

    @Deprecated("Only for debugging")
    @Query("DELETE FROM lessons WHERE semester_id = :semesterId")
    abstract suspend fun deleteAll(semesterId: Long)
}
