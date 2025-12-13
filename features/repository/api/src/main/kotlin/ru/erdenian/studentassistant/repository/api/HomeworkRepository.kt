package ru.erdenian.studentassistant.repository.api

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import ru.erdenian.studentassistant.repository.api.entity.Homework

/**
 * Репозиторий для управления домашними заданиями.
 */
interface HomeworkRepository {

    // region Primary actions

    /**
     * Создает новое домашнее задание.
     *
     * @param subjectName название предмета.
     * @param description описание задания.
     * @param deadline срок сдачи.
     * @param semesterId идентификатор семестра.
     */
    suspend fun insert(subjectName: String, description: String, deadline: LocalDate, semesterId: Long)

    /**
     * Обновляет существующее домашнее задание.
     *
     * @param id идентификатор задания.
     * @param subjectName название предмета.
     * @param description описание задания.
     * @param deadline срок сдачи.
     * @param semesterId идентификатор семестра.
     */
    suspend fun update(id: Long, subjectName: String, description: String, deadline: LocalDate, semesterId: Long)

    /**
     * Удаляет домашнее задание по идентификатору.
     *
     * @param id идентификатор задания.
     */
    suspend fun delete(id: Long)

    /**
     * Удаляет все домашние задания по указанному предмету.
     *
     * @param subjectName название предмета.
     */
    suspend fun delete(subjectName: String)

    // endregion

    // region Homeworks

    /**
     * Получает домашнее задание по идентификатору.
     *
     * @param id идентификатор задания.
     * @return домашнее задание или null, если не найдено.
     */
    suspend fun get(id: Long): Homework?

    /**
     * Возвращает поток данных конкретного домашнего задания.
     *
     * @param id идентификатор задания.
     * @return поток, эмитящий задание или null, если оно удалено.
     */
    fun getFlow(id: Long): Flow<Homework?>

    /**
     * Возвращает поток всех домашних заданий для текущего выбранного семестра.
     */
    val allFlow: Flow<List<Homework>>

    /**
     * Возвращает общее количество домашних заданий в текущем выбранном семестре.
     *
     * @return количество заданий.
     */
    suspend fun getCount(): Int

    // endregion

    // region By subject name

    /**
     * Возвращает поток всех домашних заданий по конкретному предмету в текущем семестре.
     *
     * @param subjectName название предмета.
     * @return поток списка заданий.
     */
    fun getAllFlow(subjectName: String): Flow<List<Homework>>

    /**
     * Возвращает количество домашних заданий по конкретному предмету в текущем семестре.
     *
     * @param subjectName название предмета.
     * @return количество заданий.
     */
    suspend fun getCount(subjectName: String): Int

    /**
     * Проверяет, существуют ли домашние задания по указанному предмету в семестре.
     *
     * @param semesterId идентификатор семестра.
     * @param subjectName название предмета.
     * @return true, если задания есть.
     */
    suspend fun hasHomeworks(semesterId: Long, subjectName: String): Boolean

    // endregion

    // region By deadline

    /**
     * Поток актуальных домашних заданий (дедлайн сегодня или в будущем).
     */
    val actualFlow: Flow<List<Homework>>

    /**
     * Поток просроченных домашних заданий (дедлайн прошел, и задание не выполнено).
     */
    val overdueFlow: Flow<List<Homework>>

    /**
     * Поток выполненных или прошедших домашних заданий (дедлайн прошел, и задание выполнено).
     */
    val pastFlow: Flow<List<Homework>>

    /**
     * Возвращает поток актуальных домашних заданий по конкретному предмету.
     *
     * @param subjectName название предмета.
     * @return поток списка актуальных заданий.
     */
    fun getActualFlow(subjectName: String): Flow<List<Homework>>

    // endregion
}
