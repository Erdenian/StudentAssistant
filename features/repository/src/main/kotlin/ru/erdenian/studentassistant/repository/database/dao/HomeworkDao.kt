package ru.erdenian.studentassistant.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import ru.erdenian.studentassistant.repository.database.entity.HomeworkEntity

/**
 * DAO для работы с таблицей домашних заданий.
 */
@Dao
internal interface HomeworkDao {

    // region Primary actions

    /**
     * Вставляет новое домашнее задание.
     *
     * @param homework сущность домашнего задания.
     * @return идентификатор созданной записи.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(homework: HomeworkEntity): Long

    /**
     * Обновляет существующее домашнее задание.
     *
     * @param homework сущность домашнего задания с обновленными данными.
     */
    @Update
    suspend fun update(homework: HomeworkEntity)

    /**
     * Удаляет домашнее задание по ID.
     *
     * @param id идентификатор задания.
     */
    @Query("DELETE FROM homeworks WHERE _id = :id")
    suspend fun delete(id: Long)

    /**
     * Удаляет все домашние задания по названию предмета.
     *
     * @param subjectName название предмета.
     */
    @Query("DELETE FROM homeworks WHERE subject_name = :subjectName")
    suspend fun delete(subjectName: String)

    // endregion

    // region Homeworks

    /**
     * Получает домашнее задание по ID.
     *
     * @param id идентификатор задания.
     * @return сущность задания или null.
     */
    @Query("SELECT * FROM homeworks WHERE _id = :id")
    suspend fun get(id: Long): HomeworkEntity?

    /**
     * Возвращает поток домашнего задания по ID.
     *
     * @param id идентификатор задания.
     * @return поток с сущностью задания или null.
     */
    @Query("SELECT * FROM homeworks WHERE _id = :id")
    fun getFlow(id: Long): Flow<HomeworkEntity?>

    /**
     * Возвращает поток всех домашних заданий расписания.
     *
     * @param semesterId идентификатор расписания.
     * @return поток списка заданий.
     */
    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId ORDER BY is_done, deadline, subject_name, description, _id, semester_id")
    fun getAllFlow(semesterId: Long): Flow<List<HomeworkEntity>>

    /**
     * Возвращает количество домашних заданий в расписании.
     *
     * @param semesterId идентификатор расписания.
     * @return количество заданий.
     */
    @Query("SELECT COUNT(_id) FROM homeworks WHERE semester_id = :semesterId")
    suspend fun getCount(semesterId: Long): Int

    // endregion

    // region By subject name

    /**
     * Возвращает поток домашних заданий по конкретному предмету.
     *
     * @param semesterId идентификатор расписания.
     * @param subjectName название предмета.
     * @return поток списка заданий.
     */
    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND subject_name = :subjectName ORDER BY  is_done, deadline, subject_name, description, _id, semester_id")
    fun getAllFlow(semesterId: Long, subjectName: String): Flow<List<HomeworkEntity>>

    /**
     * Возвращает количество домашних заданий по предмету.
     *
     * @param semesterId идентификатор расписания.
     * @param subjectName название предмета.
     * @return количество заданий.
     */
    @Query("SELECT COUNT(_id) FROM homeworks WHERE subject_name = :subjectName AND semester_id = :semesterId")
    suspend fun getCount(semesterId: Long, subjectName: String): Int

    /**
     * Проверяет наличие заданий по предмету.
     *
     * @param semesterId идентификатор расписания.
     * @param subjectName название предмета.
     * @return true, если задания есть.
     */
    @Query("SELECT EXISTS(SELECT _id FROM homeworks WHERE subject_name = :subjectName AND semester_id = :semesterId)")
    suspend fun hasHomeworks(semesterId: Long, subjectName: String): Boolean

    // endregion

    // region By deadline

    /**
     * Возвращает поток актуальных заданий (дедлайн >= сегодня).
     *
     * @param semesterId идентификатор расписания.
     * @param today текущая дата.
     * @return поток списка заданий.
     */
    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND deadline >= :today ORDER BY  is_done, deadline, subject_name, description, _id, semester_id")
    fun getActualFlow(semesterId: Long, today: LocalDate = LocalDate.now()): Flow<List<HomeworkEntity>>

    /**
     * Возвращает поток просроченных невыполненных заданий.
     *
     * @param semesterId идентификатор расписания.
     * @param today текущая дата.
     * @return поток списка заданий.
     */
    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND deadline < :today AND is_done = 0 ORDER BY  is_done, deadline, subject_name, description, _id, semester_id")
    fun getOverdueFlow(semesterId: Long, today: LocalDate = LocalDate.now()): Flow<List<HomeworkEntity>>

    /**
     * Возвращает поток выполненных заданий или заданий с прошедшим дедлайном, которые были помечены как выполненные.
     *
     * @param semesterId идентификатор расписания.
     * @param today текущая дата.
     * @return поток списка заданий.
     */
    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND deadline < :today AND is_done = 1 ORDER BY  is_done, deadline, subject_name, description, _id, semester_id")
    fun getPastFlow(semesterId: Long, today: LocalDate = LocalDate.now()): Flow<List<HomeworkEntity>>

    /**
     * Возвращает поток актуальных заданий по конкретному предмету.
     *
     * @param semesterId идентификатор расписания.
     * @param subjectName название предмета.
     * @param today текущая дата.
     * @return поток списка заданий.
     */
    @Query("SELECT * FROM homeworks WHERE semester_id = :semesterId AND subject_name = :subjectName AND deadline >= :today ORDER BY  is_done, deadline, subject_name, description, _id, semester_id")
    fun getActualFlow(
        semesterId: Long,
        subjectName: String,
        today: LocalDate = LocalDate.now(),
    ): Flow<List<HomeworkEntity>>

    // endregion
}
