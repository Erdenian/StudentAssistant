package ru.erdenian.studentassistant.repository.api

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import ru.erdenian.studentassistant.repository.api.entity.Semester

/**
 * Репозиторий для управления семестрами.
 */
interface SemesterRepository {

    /**
     * Создает новый семестр.
     *
     * @param name название семестра (должно быть уникальным).
     * @param firstDay дата начала семестра.
     * @param lastDay дата окончания семестра.
     */
    suspend fun insert(name: String, firstDay: LocalDate, lastDay: LocalDate)

    /**
     * Обновляет данные существующего семестра.
     *
     * @param id уникальный идентификатор семестра.
     * @param name новое название.
     * @param firstDay новая дата начала.
     * @param lastDay новая дата окончания.
     */
    suspend fun update(id: Long, name: String, firstDay: LocalDate, lastDay: LocalDate)

    /**
     * Удаляет семестр по его идентификатору.
     *
     * Вместе с семестром каскадно удаляются все связанные занятия и домашние задания.
     *
     * @param id идентификатор семестра.
     */
    suspend fun delete(id: Long)

    /**
     * Возвращает поток со списком всех семестров.
     *
     * Список отсортирован по датам и названию.
     */
    val allFlow: Flow<List<Semester>>

    /**
     * Получает семестр по идентификатору.
     *
     * @param id идентификатор семестра.
     * @return семестр или null, если он не найден.
     */
    suspend fun get(id: Long): Semester?

    /**
     * Возвращает поток данных конкретного семестра.
     *
     * @param id идентификатор семестра.
     * @return поток, эмитящий семестр или null, если он был удален.
     */
    fun getFlow(id: Long): Flow<Semester?>

    /**
     * Возвращает поток со списком названий всех семестров.
     *
     * Используется для валидации уникальности имени при создании/редактировании.
     */
    val namesFlow: Flow<List<String>>
}
