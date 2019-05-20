package ru.erdenian.studentassistant.localdata.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.joda.time.Period
import ru.erdenian.studentassistant.localdata.entity.LessonNew

@Dao
interface LessonDao {

    private companion object {
        const val DEFAULT_DURATION_MILLIS = 5_400_000
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(lesson: LessonNew)

    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId AND _id = :lessonId")
    fun get(semesterId: Long, lessonId: Long): LessonNew?

    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId ORDER BY start_time, end_time, _id")
    fun get(semesterId: Long): LiveData<List<LessonNew>>

    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId AND subject_name = :subjectName ORDER BY start_time, end_time, _id")
    fun get(semesterId: Long, subjectName: String): List<LessonNew>

    @Query("SELECT DISTINCT subject_name FROM lessons WHERE semester_id = :semesterId ORDER BY subject_name")
    fun getSubjects(semesterId: Long): List<String>

    @Query("SELECT DISTINCT type FROM lessons WHERE semester_id = :semesterId ORDER BY type")
    fun getTypes(semesterId: Long): List<String>

    // Todo
    @Query("SELECT DISTINCT teachers FROM lessons WHERE semester_id = :semesterId ORDER BY teachers")
    fun getTeachers(semesterId: Long): List<String>

    // Todo
    @Query("SELECT DISTINCT classrooms FROM lessons WHERE semester_id = :semesterId ORDER BY classrooms")
    fun getClassrooms(semesterId: Long): List<String>

    @Query("SELECT IFNULL((SELECT end_time - start_time as duration FROM lessons WHERE semester_id = :semesterId GROUP BY duration ORDER BY COUNT(duration) desc), $DEFAULT_DURATION_MILLIS)")
    fun getLessonLength(semesterId: Long): Period

    @Query("SELECT COUNT(_id) FROM lessons WHERE semester_id = :semesterId AND subject_name = :subjectName")
    fun getCount(semesterId: Long, subjectName: String): Int

    @Query("UPDATE lessons SET subject_name = :newName WHERE semester_id = :semesterId AND subject_name = :oldName")
    fun renameSubject(semesterId: Long, oldName: String, newName: String)

    @Delete
    fun delete(lesson: LessonNew)

    @Deprecated("Only for debugging")
    @Query("DELETE FROM lessons")
    fun deleteAll()

    @Deprecated("Only for debugging")
    @Query("DELETE FROM lessons WHERE semester_id = :semesterId")
    fun deleteAll(semesterId: Long)
}
