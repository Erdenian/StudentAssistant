package ru.erdenian.studentassistant.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.database.entity.HomeworkEntity

@Suppress("TooManyFunctions", "MaxLineLength")
@Dao
interface HomeworkDao {

    // region Primary actions

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(homework: HomeworkEntity)

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND _id = :homeworkId")
    suspend fun get(semesterId: Long, homeworkId: Long): HomeworkEntity?

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND _id = :homeworkId")
    fun getLive(semesterId: Long, homeworkId: Long): LiveData<HomeworkEntity?>

    @Delete
    suspend fun delete(homework: HomeworkEntity)

    // endregion

    // region Homeworks list

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId ORDER BY deadline, _id")
    fun get(semesterId: Long): LiveData<List<HomeworkEntity>>

    @Query("SELECT COUNT(_id) FROM homeworks WHERE semester_id = :semesterId")
    suspend fun getCount(semesterId: Long): Int

    // endregion

    // region By subject name

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND subject_name = :subjectName ORDER BY deadline, _id")
    fun get(semesterId: Long, subjectName: String): LiveData<List<HomeworkEntity>>

    @Query("SELECT COUNT(_id) FROM homeworks WHERE subject_name = :subjectName AND semester_id = :semesterId")
    suspend fun getCount(semesterId: Long, subjectName: String): Int

    @Query("SELECT COUNT(_id) > 0 FROM homeworks WHERE subject_name = :subjectName AND semester_id = :semesterId")
    suspend fun hasHomeworks(semesterId: Long, subjectName: String): Boolean

    // endregion

    // region By deadline

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND deadline >= :today ORDER BY deadline, _id")
    fun getActual(semesterId: Long, today: LocalDate = LocalDate.now()): LiveData<List<HomeworkEntity>>

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND deadline < :today ORDER BY deadline, _id")
    fun getPast(semesterId: Long, today: LocalDate = LocalDate.now()): LiveData<List<HomeworkEntity>>

    // endregion

    // region By subject name and deadline

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND subject_name = :subjectName AND deadline >= :today ORDER BY deadline, _id")
    fun getActual(semesterId: Long, subjectName: String, today: LocalDate = LocalDate.now()): LiveData<List<HomeworkEntity>>

    // endregion
}
