package ru.erdenian.studentassistant.localdata.dao

import androidx.room.*
import ru.erdenian.studentassistant.localdata.entity.SemesterNew

@Dao
interface SemesterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(semester: SemesterNew)

    @Query("SELECT * FROM semesters ORDER BY first_day, last_day, name, _id")
    fun getAll(): List<SemesterNew>

    @Query("SELECT * FROM semesters WHERE _id = :semesterId")
    fun get(semesterId: Long): SemesterNew?

    @Query("SELECT name FROM semesters ORDER BY first_day, last_day, name, _id")
    fun getNames(): List<String>

    @Query("SELECT COUNT(*) FROM lessons WHERE semester_id = :semesterId")
    fun lessonsCount(semesterId: Long): Int

    @Query("SELECT COUNT(*) > 0 FROM lessons WHERE semester_id = :semesterId")
    fun hasLessons(semesterId: Long): Boolean

    @Delete
    fun delete(semester: SemesterNew)

    @Deprecated("Only for debugging")
    @Query("DELETE FROM semesters")
    fun deleteAll()
}
