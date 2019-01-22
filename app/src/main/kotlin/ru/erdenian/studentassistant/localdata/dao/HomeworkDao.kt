package ru.erdenian.studentassistant.localdata.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.erdenian.studentassistant.localdata.entity.HomeworkNew

@Dao
interface HomeworkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(homework: HomeworkNew)

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId ORDER BY deadline, _id")
    fun get(semesterId: Long): LiveData<List<HomeworkNew>>

    @Delete
    fun delete(homework: HomeworkNew)

    @Query("DELETE FROM homeworks")
    fun deleteAll()

    @Query("DELETE FROM homeworks WHERE semester_id = :semesterId")
    fun deleteAll(semesterId: Long)
}
