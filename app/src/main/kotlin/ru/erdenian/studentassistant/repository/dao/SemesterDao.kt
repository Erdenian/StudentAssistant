package ru.erdenian.studentassistant.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.erdenian.studentassistant.repository.entity.Semester

@Suppress("TooManyFunctions", "MaxLineLength")
@Dao
interface SemesterDao {

    // region Primary actions

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(semester: Semester)

    @Delete
    suspend fun delete(semester: Semester)

    @Deprecated("Only for debugging")
    @Query("DELETE FROM semesters")
    suspend fun deleteAll()

    // endregion

    @Query("SELECT * FROM semesters ORDER BY first_day, last_day, name, _id")
    fun getAll(): LiveData<List<Semester>>

    @Query("SELECT * FROM semesters WHERE _id = :semesterId")
    fun get(semesterId: Long): LiveData<Semester?>

    @Query("SELECT name FROM semesters ORDER BY first_day, last_day, name, _id")
    fun getNames(): LiveData<List<String>>
}
