package ru.erdenian.studentassistant.repository.api

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import ru.erdenian.studentassistant.repository.api.entity.Semester

/**
 * Репозиторий для управления расписаниями.
 */
interface SemesterRepository {

    /**
     * Создает новое расписание.
     *
     * @param name название расписания (должно быть уникальным).
     * @param firstDay дата начала расписания.
     * @param lastDay дата окончания расписания.
     */
    suspend fun insert(name: String, firstDay: LocalDate, lastDay: LocalDate)

    /**
     * Обновляет данные существующего расписания.
     *
     * @param id уникальный идентификатор расписания.
     * @param name новое название.
     * @param firstDay новая дата начала.
     * @param lastDay новая дата окончания.
     */
    suspend fun update(id: Long, name: String, firstDay: LocalDate, lastDay: LocalDate)

    /**
     * Удаляет расписание по его идентификатору.
     *
     * Вместе с расписанием каскадно удаляются все связанные занятия и домашние задания.
     *
     * @param id идентификатор расписания.
     */
    suspend fun delete(id: Long)

    /**
     * Возвращает поток со списком всех расписаний.
     *
     * Список отсортирован по датам и названию.
     */
    val allFlow: Flow<List<Semester>>

    /**
     * Получает расписание по идентификатору.
     *
     * @param id идентификатор расписания.
     * @return расписание или null, если оно не найдено.
     */
    suspend fun get(id: Long): Semester?

    /**
     * Возвращает поток данных конкретного расписания.
     *
     * @param id идентификатор расписания.
     * @return поток, эмитящий расписание или null, если оно было удалено.
     */
    fun getFlow(id: Long): Flow<Semester?>

    /**
     * Возвращает поток со списком названий всех расписаний.
     *
     * Используется для валидации уникальности имени при создании/редактировании.
     */
    val namesFlow: Flow<List<String>>
}
