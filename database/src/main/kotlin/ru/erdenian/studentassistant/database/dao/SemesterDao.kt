package ru.erdenian.studentassistant.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.erdenian.studentassistant.database.entity.SemesterEntity

@Dao
interface SemesterDao {

    // region Primary actions

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(semester: SemesterEntity)

    @Delete
    suspend fun delete(semester: SemesterEntity)

    // endregion

    @Query("SELECT * FROM semesters ORDER BY first_day, last_day, name, _id")
    fun getAll(): LiveData<List<SemesterEntity>>

    @Query("SELECT * FROM semesters WHERE _id = :semesterId")
    fun get(semesterId: Long): LiveData<SemesterEntity?>

    @Query("SELECT name FROM semesters ORDER BY first_day, last_day, name, _id")
    fun getNames(): LiveData<List<String>>
}
