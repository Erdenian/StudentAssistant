package ru.erdenian.studentassistant.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.repository.entity.HomeworkNew

@Suppress("TooManyFunctions", "MaxLineLength", "MaximumLineLength")
@Dao
interface HomeworkDao {

    // region Primary actions

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(homework: HomeworkNew)

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND _id = :homeworkId")
    suspend fun get(semesterId: Long, homeworkId: Long): HomeworkNew?

    @Delete
    suspend fun delete(homework: HomeworkNew)

    @Query("DELETE FROM homeworks WHERE semester_id = :semesterId AND subject_name = :subjectName")
    suspend fun delete(semesterId: Long, subjectName: String)

    @Deprecated("Only for debugging")
    @Query("DELETE FROM homeworks WHERE semester_id = :semesterId")
    suspend fun deleteAll(semesterId: Long)

    @Deprecated("Only for debugging")
    @Query("DELETE FROM homeworks")
    suspend fun deleteAll()

    // endregion

    // region Homeworks list

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId ORDER BY deadline, _id")
    fun get(semesterId: Long): LiveData<List<HomeworkNew>>

    @Query("SELECT COUNT(_id) FROM homeworks WHERE semester_id = :semesterId")
    suspend fun getCount(semesterId: Long): Int

    @Query("SELECT COUNT(_id) > 0 FROM homeworks WHERE semester_id = :semesterId")
    suspend fun hasHomeworks(semesterId: Long): Boolean

    // endregion

    // region By subject name

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND subject_name = :subjectName ORDER BY deadline, _id")
    fun get(semesterId: Long, subjectName: String): LiveData<List<HomeworkNew>>

    @Query("SELECT COUNT(_id) FROM homeworks WHERE subject_name = :subjectName AND semester_id = :semesterId")
    suspend fun getCount(semesterId: Long, subjectName: String): Int

    @Query("SELECT COUNT(_id) > 0 FROM homeworks WHERE subject_name = :subjectName AND semester_id = :semesterId")
    suspend fun hasHomeworks(semesterId: Long, subjectName: String): Boolean

    // endregion

    // region By deadline

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND deadline >= :today ORDER BY deadline, _id")
    fun getActual(semesterId: Long, today: LocalDate = LocalDate.now()): LiveData<List<HomeworkNew>>

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND deadline < :today ORDER BY deadline, _id")
    fun getPast(semesterId: Long, today: LocalDate = LocalDate.now()): LiveData<List<HomeworkNew>>

    // endregion

    // region By subject name and deadline

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND subject_name = :subjectName AND deadline >= :today ORDER BY deadline, _id")
    fun getActual(
        semesterId: Long,
        subjectName: String,
        today: LocalDate = LocalDate.now()
    ): LiveData<List<HomeworkNew>>

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND subject_name = :subjectName AND deadline < :today ORDER BY deadline, _id")
    fun getPast(
        semesterId: Long,
        subjectName: String,
        today: LocalDate = LocalDate.now()
    ): LiveData<List<HomeworkNew>>

    // endregion

    @Query("UPDATE homeworks SET subject_name = :newName WHERE semester_id = :semesterId AND subject_name = :oldName")
    suspend fun renameSubject(semesterId: Long, oldName: String, newName: String)
}
