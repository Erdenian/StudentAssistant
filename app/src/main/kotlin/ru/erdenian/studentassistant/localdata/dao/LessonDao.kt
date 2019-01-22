package ru.erdenian.studentassistant.localdata.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.erdenian.studentassistant.localdata.entity.LessonNew

@Dao
interface LessonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(lesson: LessonNew)

    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId ORDER BY start_time, end_time, _id")
    fun get(semesterId: Long): LiveData<List<LessonNew>>

    @Delete
    fun delete(lesson: LessonNew)

    @Query("DELETE FROM lessons")
    fun deleteAll()

    @Query("DELETE FROM lessons WHERE semester_id = :semesterId")
    fun deleteAll(semesterId: Long)
}
