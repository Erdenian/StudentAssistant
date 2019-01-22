package ru.erdenian.studentassistant.localdata.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.erdenian.studentassistant.localdata.entity.SemesterNew

@Dao
interface SemesterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(semester: SemesterNew)

    @Query("SELECT * FROM semesters ORDER BY first_day, last_day, name, _id")
    fun getAll(): List<SemesterNew>

    @Query("SELECT * FROM semesters WHERE _id = :semesterId")
    fun get(semesterId: Long): LiveData<SemesterNew>

    @Delete
    fun delete(semester: SemesterNew)

    @Query("DELETE FROM semesters")
    fun deleteAll()

    @Query("DELETE FROM semesters WHERE _id = :semesterId")
    fun deleteAll(semesterId: Long)
}
