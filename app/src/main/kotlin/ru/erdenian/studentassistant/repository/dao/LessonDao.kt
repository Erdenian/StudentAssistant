package ru.erdenian.studentassistant.repository.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.joda.time.Period
import ru.erdenian.studentassistant.repository.Converters
import ru.erdenian.studentassistant.repository.entity.Lesson

@Suppress("TooManyFunctions", "MaxLineLength")
@Dao
abstract class LessonDao {

    private companion object {
        const val DEFAULT_DURATION_MILLIS = 5_400_000
    }

    // region Primary actions

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(lesson: Lesson)

    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId AND _id = :lessonId")
    abstract suspend fun get(semesterId: Long, lessonId: Long): Lesson?

    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId AND _id = :lessonId")
    abstract fun getLive(semesterId: Long, lessonId: Long): LiveData<Lesson?>

    @Delete
    abstract suspend fun delete(lesson: Lesson)

    @Deprecated("Only for debugging")
    @Query("DELETE FROM lessons WHERE semester_id = :semesterId")
    abstract suspend fun deleteAll(semesterId: Long)

    @Deprecated("Only for debugging")
    @Query("DELETE FROM lessons")
    abstract suspend fun deleteAll()

    // endregion

    // region Lessons

    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId ORDER BY start_time, end_time, _id")
    abstract fun get(semesterId: Long): LiveData<List<Lesson>>

    @Query("SELECT COUNT(_id) FROM lessons WHERE semester_id = :semesterId")
    abstract suspend fun getCount(semesterId: Long): Int

    @Query("SELECT COUNT(_id) > 0 FROM lessons WHERE semester_id = :semesterId")
    abstract suspend fun hasLessons(semesterId: Long): Boolean

    // endregion

    // region Subjects

    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId AND subject_name = :subjectName ORDER BY start_time, end_time, _id")
    abstract fun get(semesterId: Long, subjectName: String): LiveData<List<Lesson>>

    @Query("SELECT COUNT(_id) FROM lessons WHERE semester_id = :semesterId AND subject_name = :subjectName")
    abstract suspend fun getCount(semesterId: Long, subjectName: String): Int

    @Query("SELECT COUNT(_id) > 0 FROM lessons WHERE semester_id = :semesterId AND subject_name = :subjectName")
    abstract suspend fun hasLessons(semesterId: Long, subjectName: String): Boolean

    @Query("SELECT DISTINCT subject_name FROM lessons WHERE semester_id = :semesterId ORDER BY subject_name")
    abstract fun getSubjects(semesterId: Long): LiveData<List<String>>

    @Query("UPDATE lessons SET subject_name = :newName WHERE semester_id = :semesterId AND subject_name = :oldName")
    abstract suspend fun renameSubject(semesterId: Long, oldName: String, newName: String)

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

    @Query("SELECT IFNULL((SELECT end_time - start_time as duration FROM lessons WHERE semester_id = :semesterId GROUP BY duration ORDER BY COUNT(duration) desc), $DEFAULT_DURATION_MILLIS)")
    abstract suspend fun getLessonLength(semesterId: Long): Period

    // endregion
}
