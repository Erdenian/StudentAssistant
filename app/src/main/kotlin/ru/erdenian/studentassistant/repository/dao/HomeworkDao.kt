package ru.erdenian.studentassistant.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.repository.entity.HomeworkNew

@Dao
interface HomeworkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(homework: HomeworkNew)

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND _id = :homeworkId")
    suspend fun get(semesterId: Long, homeworkId: Long): HomeworkNew?

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId ORDER BY deadline, _id")
    fun get(semesterId: Long): LiveData<List<HomeworkNew>>

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND subject_name = :subjectName ORDER BY deadline, _id")
    suspend fun get(semesterId: Long, subjectName: String): List<HomeworkNew>

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND deadline >= :today ORDER BY deadline, _id")
    fun getActual(semesterId: Long, today: LocalDate = LocalDate.now()): LiveData<List<HomeworkNew>>

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND subject_name = :subjectName AND deadline >= :today ORDER BY deadline, _id")
    suspend fun getActual(
        semesterId: Long,
        subjectName: String,
        today: LocalDate = LocalDate.now()
    ): List<HomeworkNew>

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND deadline < :today ORDER BY deadline, _id")
    fun getPast(semesterId: Long, today: LocalDate = LocalDate.now()): LiveData<List<HomeworkNew>>

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND subject_name = :subjectName AND deadline < :today ORDER BY deadline, _id")
    suspend fun getPast(
        semesterId: Long,
        subjectName: String,
        today: LocalDate = LocalDate.now()
    ): List<HomeworkNew>

    @Query("UPDATE homeworks SET subject_name = :newName WHERE semester_id = :semesterId AND subject_name = :oldName")
    suspend fun renameSubject(semesterId: Long, oldName: String, newName: String)

    @Delete
    suspend fun delete(homework: HomeworkNew)

    @Query("DELETE FROM homeworks WHERE semester_id = :semesterId AND subject_name = :subjectName")
    suspend fun delete(semesterId: Long, subjectName: String)

    @Deprecated("Only for debugging")
    @Query("DELETE FROM homeworks")
    suspend fun deleteAll()

    @Deprecated("Only for debugging")
    @Query("DELETE FROM homeworks WHERE semester_id = :semesterId")
    suspend fun deleteAll(semesterId: Long)
}
