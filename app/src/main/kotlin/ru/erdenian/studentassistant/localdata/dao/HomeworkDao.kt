package ru.erdenian.studentassistant.localdata.dao

import androidx.room.*
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.localdata.entity.HomeworkNew

@Dao
interface HomeworkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(homework: HomeworkNew)

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId ORDER BY deadline, _id")
    fun get(semesterId: Long): List<HomeworkNew>

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND _id = :homeworkId")
    fun get(semesterId: Long, homeworkId: Long): HomeworkNew?

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND subject_name = :subjectName")
    fun get(semesterId: Long, subjectName: String): List<HomeworkNew>

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND deadline >= :today")
    fun getActual(semesterId: Long, today: LocalDate = LocalDate.now())

    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND deadline < :today")
    fun getPast(semesterId: Long, today: LocalDate = LocalDate.now())

    @Delete
    fun delete(homework: HomeworkNew)

    @Query("DELETE FROM homeworks")
    fun deleteAll()

    @Query("DELETE FROM homeworks WHERE semester_id = :semesterId")
    fun deleteAll(semesterId: Long)
}
