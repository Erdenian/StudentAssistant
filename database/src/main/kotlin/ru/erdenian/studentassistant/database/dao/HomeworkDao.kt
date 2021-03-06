package ru.erdenian.studentassistant.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.database.entity.HomeworkEntity

@Dao
interface HomeworkDao {

    // region Primary actions

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(homework: HomeworkEntity): Long

    @Update
    suspend fun update(homework: HomeworkEntity)

    @Query("DELETE FROM homeworks WHERE _id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM homeworks WHERE subject_name = :subjectName")
    suspend fun delete(subjectName: String)

    // endregion

    // region Homeworks

    @Query("SELECT * FROM homeworks WHERE _id = :id")
    suspend fun get(id: Long): HomeworkEntity?

    @Query("SELECT * FROM homeworks WHERE _id = :id")
    fun getLiveData(id: Long): LiveData<HomeworkEntity?>

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId ORDER BY deadline, _id")
    fun getAllLiveData(semesterId: Long): LiveData<List<HomeworkEntity>>

    @Query("SELECT COUNT(_id) FROM homeworks WHERE semester_id = :semesterId")
    suspend fun getCount(semesterId: Long): Int

    // endregion

    // region By subject name

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND subject_name = :subjectName ORDER BY deadline, _id")
    fun getAllLiveData(semesterId: Long, subjectName: String): LiveData<List<HomeworkEntity>>

    @Query("SELECT COUNT(_id) FROM homeworks WHERE subject_name = :subjectName AND semester_id = :semesterId")
    suspend fun getCount(semesterId: Long, subjectName: String): Int

    @Query("SELECT EXISTS(SELECT _id FROM homeworks WHERE subject_name = :subjectName AND semester_id = :semesterId)")
    suspend fun hasHomeworks(semesterId: Long, subjectName: String): Boolean

    // endregion

    // region By deadline

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND deadline >= :today ORDER BY deadline, _id")
    fun getActualLiveData(semesterId: Long, today: LocalDate = LocalDate.now()): LiveData<List<HomeworkEntity>>

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND deadline < :today AND is_done = 0 ORDER BY deadline, _id")
    fun getOverdueLiveData(semesterId: Long, today: LocalDate = LocalDate.now()): LiveData<List<HomeworkEntity>>

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND deadline < :today AND is_done = 1 ORDER BY deadline, _id")
    fun getPastLiveData(semesterId: Long, today: LocalDate = LocalDate.now()): LiveData<List<HomeworkEntity>>

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND subject_name = :subjectName AND deadline >= :today ORDER BY deadline, _id")
    fun getActualLiveData(
        semesterId: Long,
        subjectName: String,
        today: LocalDate = LocalDate.now()
    ): LiveData<List<HomeworkEntity>>

    // endregion
}
