package ru.erdenian.studentassistant.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.erdenian.studentassistant.repository.database.entity.SemesterEntity

/**
 * DAO для работы с таблицей семестров.
 */
@Dao
internal interface SemesterDao {

    // region Primary actions

    /**
     * Вставляет новый семестр в базу данных.
     *
     * @param semester сущность семестра.
     * @return идентификатор вставленной записи.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(semester: SemesterEntity): Long

    /**
     * Обновляет существующий семестр.
     *
     * @param semester сущность семестра с обновленными данными.
     */
    @Update
    suspend fun update(semester: SemesterEntity)

    /**
     * Удаляет семестр по идентификатору.
     *
     * @param id идентификатор семестра.
     */
    @Query("DELETE FROM semesters WHERE _id = :id")
    suspend fun delete(id: Long)

    // endregion

    /**
     * Возвращает поток всех семестров, отсортированных по датам и названию.
     *
     * @return поток списка семестров.
     */
    @Query("SELECT * FROM semesters ORDER BY last_day, first_day, name, _id")
    fun getAllFlow(): Flow<List<SemesterEntity>>

    /**
     * Возвращает семестр по идентификатору.
     *
     * @param id идентификатор семестра.
     * @return семестр или null, если не найден.
     */
    @Query("SELECT * FROM semesters WHERE _id = :id")
    suspend fun get(id: Long): SemesterEntity?

    /**
     * Возвращает поток семестра по идентификатору.
     *
     * @param id идентификатор семестра.
     * @return поток с семестром или null.
     */
    @Query("SELECT * FROM semesters WHERE _id = :id")
    fun getFlow(id: Long): Flow<SemesterEntity?>

    /**
     * Возвращает список всех названий семестров.
     *
     * @return поток списка названий.
     */
    @Query("SELECT name FROM semesters ORDER BY last_day, first_day, name, _id")
    fun getNamesFlow(): Flow<List<String>>
}
