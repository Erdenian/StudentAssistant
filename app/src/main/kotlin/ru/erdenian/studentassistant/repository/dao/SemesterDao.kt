package ru.erdenian.studentassistant.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.erdenian.studentassistant.repository.entity.SemesterNew

@Dao
interface SemesterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(semester: SemesterNew)

    @Query("SELECT * FROM semesters ORDER BY first_day, last_day, name, _id")
    fun getAll(): LiveData<List<SemesterNew>>

    @Query("SELECT * FROM semesters WHERE _id = :semesterId")
    fun get(semesterId: Long): LiveData<SemesterNew?>

    @Query("SELECT name FROM semesters ORDER BY first_day, last_day, name, _id")
    fun getNames(): LiveData<List<String>>

    @Query("SELECT COUNT(_id) FROM lessons WHERE semester_id = :semesterId")
    suspend fun lessonsCount(semesterId: Long): Int

    @Query("SELECT COUNT(_id) > 0 FROM lessons WHERE semester_id = :semesterId")
    suspend fun hasLessons(semesterId: Long): Boolean

    @Query("SELECT COUNT(_id) FROM homeworks WHERE subject_name = :subjectName AND semester_id = :semesterId")
    suspend fun homeworksCount(semesterId: Long, subjectName: String): Int

    @Query("SELECT COUNT(_id) > 0 FROM homeworks WHERE subject_name = :subjectName AND semester_id = :semesterId")
    suspend fun hasHomeworks(semesterId: Long, subjectName: String): Boolean

    @Delete
    suspend fun delete(semester: SemesterNew)

    @Deprecated("Only for debugging")
    @Query("DELETE FROM semesters")
    suspend fun deleteAll()
}
