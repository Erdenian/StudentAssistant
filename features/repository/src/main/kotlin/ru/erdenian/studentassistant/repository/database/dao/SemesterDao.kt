package ru.erdenian.studentassistant.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.erdenian.studentassistant.repository.database.entity.SemesterEntity

/**
 * DAO для работы с таблицей расписаний.
 */
@Dao
internal interface SemesterDao {

    // region Primary actions

    /**
     * Вставляет новое расписание в базу данных.
     *
     * @param semester сущность расписания.
     * @return идентификатор вставленной записи.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(semester: SemesterEntity): Long

    /**
     * Обновляет существующее расписание.
     *
     * @param semester сущность расписания с обновленными данными.
     */
    @Update
    suspend fun update(semester: SemesterEntity)

    /**
     * Удаляет расписание по идентификатору.
     *
     * @param id идентификатор расписания.
     */
    @Query("DELETE FROM semesters WHERE _id = :id")
    suspend fun delete(id: Long)

    // endregion

    /**
     * Возвращает поток всех расписаний, отсортированных по датам и названию.
     *
     * @return поток списка расписаний.
     */
    @Query("SELECT * FROM semesters ORDER BY last_day, first_day, name, _id")
    fun getAllFlow(): Flow<List<SemesterEntity>>

    /**
     * Возвращает расписание по идентификатору.
     *
     * @param id идентификатор расписания.
     * @return расписание или null, если не найден.
     */
    @Query("SELECT * FROM semesters WHERE _id = :id")
    suspend fun get(id: Long): SemesterEntity?

    /**
     * Возвращает поток расписания по идентификатору.
     *
     * @param id идентификатор расписания.
     * @return поток с расписанием или null.
     */
    @Query("SELECT * FROM semesters WHERE _id = :id")
    fun getFlow(id: Long): Flow<SemesterEntity?>

    /**
     * Возвращает список всех названий расписаний.
     *
     * @return поток списка названий.
     */
    @Query("SELECT name FROM semesters ORDER BY last_day, first_day, name, _id")
    fun getNamesFlow(): Flow<List<String>>
}
