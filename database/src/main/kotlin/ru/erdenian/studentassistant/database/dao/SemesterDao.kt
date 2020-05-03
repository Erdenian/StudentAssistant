package ru.erdenian.studentassistant.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.erdenian.studentassistant.database.entity.SemesterEntity

@Dao
interface SemesterDao {

    // region Primary actions

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(semester: SemesterEntity): Long

    @Update
    suspend fun update(semester: SemesterEntity)

    @Query("DELETE FROM semesters WHERE _id = :id")
    suspend fun delete(id: Long)

    // endregion

    @Query("SELECT * FROM semesters ORDER BY first_day, last_day, name, _id")
    fun getAllLiveData(): LiveData<List<SemesterEntity>>

    @Query("SELECT * FROM semesters WHERE _id = :id")
    fun getLiveData(id: Long): LiveData<SemesterEntity?>

    @Query("SELECT name FROM semesters ORDER BY first_day, last_day, name, _id")
    fun getNamesLiveData(): LiveData<List<String>>
}
