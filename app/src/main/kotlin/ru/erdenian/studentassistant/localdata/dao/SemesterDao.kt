package ru.erdenian.studentassistant.localdata.dao

import androidx.room.*
import org.joda.time.Period
import ru.erdenian.studentassistant.localdata.entity.SemesterNew

@Dao
interface SemesterDao {

    private companion object {
        const val DEFAULT_DURATION_MILLIS = 5_400_000
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(semester: SemesterNew)

    @Query("SELECT * FROM semesters ORDER BY first_day, last_day, name, _id")
    fun getAll(): List<SemesterNew>

    @Query("SELECT * FROM semesters WHERE _id = :semesterId")
    fun get(semesterId: Long): SemesterNew?

    @Query("SELECT name FROM semesters ORDER BY first_day, last_day, name, _id")
    fun getNames(): List<String>

    @Query("SELECT DISTINCT subject_name FROM lessons WHERE semester_id = :semesterId ORDER BY subject_name")
    fun getSubjects(semesterId: Long): List<String>

    @Query("SELECT DISTINCT type FROM lessons WHERE semester_id = :semesterId ORDER BY type")
    fun getTypes(semesterId: Long): List<String>

    @Query("SELECT DISTINCT teachers FROM lessons WHERE semester_id = :semesterId ORDER BY teachers")
    fun getTeachers(semesterId: Long): List<String>

    @Query("SELECT DISTINCT classrooms FROM lessons WHERE semester_id = :semesterId ORDER BY classrooms")
    fun getClassrooms(semesterId: Long): List<String>

    @Query("SELECT IFNULL((SELECT end_time - start_time as duration FROM lessons WHERE semester_id = :semesterId GROUP BY duration ORDER BY COUNT(duration) desc), $DEFAULT_DURATION_MILLIS)")
    fun getLessonLength(semesterId: Long): Period

    @Query("SELECT COUNT(*) FROM lessons WHERE semester_id = :semesterId")
    fun lessonsCount(semesterId: Long): Int

    @Query("SELECT COUNT(*) > 0 FROM lessons WHERE semester_id = :semesterId")
    fun hasLessons(semesterId: Long): Boolean

    @Delete
    fun delete(semester: SemesterNew)

    @Query("DELETE FROM semesters")
    fun deleteAll()

    @Query("DELETE FROM semesters WHERE _id = :semesterId")
    fun deleteAll(semesterId: Long)
}
