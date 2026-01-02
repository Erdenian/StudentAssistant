package ru.erdenian.studentassistant.repository.api

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.Flow
import ru.erdenian.studentassistant.repository.api.entity.Lesson

/**
 * Репозиторий для управления занятиями.
 */
interface LessonRepository {

    // region Primary actions

    /**
     * Создает новое занятие, повторяющееся по дням недели.
     *
     * @param subjectName название предмета.
     * @param type тип занятия (лекция, семинар и т.д.).
     * @param teachers список преподавателей.
     * @param classrooms список аудиторий.
     * @param startTime время начала.
     * @param endTime время окончания.
     * @param semesterId идентификатор расписания.
     * @param dayOfWeek день недели.
     * @param weeks список флагов повторения по неделям (true - повторяется, false - нет).
     */
    suspend fun insert(
        subjectName: String,
        type: String,
        teachers: Set<String>,
        classrooms: Set<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        semesterId: Long,
        dayOfWeek: DayOfWeek,
        weeks: List<Boolean>,
    )

    /**
     * Создает новое занятие, проходящее в конкретные даты.
     *
     * @param subjectName название предмета.
     * @param type тип занятия.
     * @param teachers список преподавателей.
     * @param classrooms список аудиторий.
     * @param startTime время начала.
     * @param endTime время окончания.
     * @param semesterId идентификатор расписания.
     * @param dates набор дат проведения занятия.
     */
    suspend fun insert(
        subjectName: String,
        type: String,
        teachers: Set<String>,
        classrooms: Set<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        semesterId: Long,
        dates: Set<LocalDate>,
    )

    /**
     * Обновляет существующее занятие, устанавливая повторение по дням недели.
     *
     * @param id идентификатор занятия.
     * @param subjectName название предмета.
     * @param type тип занятия.
     * @param teachers список преподавателей.
     * @param classrooms список аудиторий.
     * @param startTime время начала.
     * @param endTime время окончания.
     * @param semesterId идентификатор расписания.
     * @param dayOfWeek день недели.
     * @param weeks список флагов повторения по неделям.
     */
    suspend fun update(
        id: Long,
        subjectName: String,
        type: String,
        teachers: Set<String>,
        classrooms: Set<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        semesterId: Long,
        dayOfWeek: DayOfWeek,
        weeks: List<Boolean>,
    )

    /**
     * Обновляет существующее занятие, устанавливая повторение по датам.
     *
     * @param id идентификатор занятия.
     * @param subjectName название предмета.
     * @param type тип занятия.
     * @param teachers список преподавателей.
     * @param classrooms список аудиторий.
     * @param startTime время начала.
     * @param endTime время окончания.
     * @param semesterId идентификатор расписания.
     * @param dates набор дат проведения занятия.
     */
    suspend fun update(
        id: Long,
        subjectName: String,
        type: String,
        teachers: Set<String>,
        classrooms: Set<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        semesterId: Long,
        dates: Set<LocalDate>,
    )

    /**
     * Удаляет занятие по идентификатору.
     *
     * @param id идентификатор занятия.
     */
    suspend fun delete(id: Long)

    // endregion

    // region Lessons

    /**
     * Получает занятие по идентификатору.
     *
     * @param id идентификатор занятия.
     * @return занятие или null, если оно не найдено.
     */
    suspend fun get(id: Long): Lesson?

    /**
     * Возвращает поток данных конкретного занятия.
     *
     * @param id идентификатор занятия.
     * @return поток, эмитящий занятие или null, если оно удалено.
     */
    fun getFlow(id: Long): Flow<Lesson?>

    /**
     * Возвращает поток всех занятий текущего выбранного расписания.
     */
    val allFlow: Flow<List<Lesson>>

    /**
     * Возвращает поток занятий на конкретную дату для текущего выбранного расписания.
     *
     * Учитывает как регулярные занятия (проверяет четность недели), так и занятия по датам.
     *
     * @param day дата, для которой нужно получить расписание.
     * @return поток со списком занятий на указанный день.
     */
    fun getAllFlow(day: LocalDate): Flow<List<Lesson>>

    /**
     * Возвращает поток всех занятий в расписании, проходящих в указанный день недели.
     *
     * @param semesterId идентификатор расписания.
     * @param dayOfWeek день недели.
     * @return поток со списком занятий.
     */
    fun getAllFlow(semesterId: Long, dayOfWeek: DayOfWeek): Flow<List<Lesson>>

    /**
     * Возвращает общее количество занятий в расписании.
     *
     * @param semesterId идентификатор расписания.
     * @return количество занятий.
     */
    suspend fun getCount(semesterId: Long): Int

    /**
     * Поток, показывающий, есть ли занятия в текущем выбранном расписании.
     */
    val hasLessonsFlow: Flow<Boolean>

    /**
     * Проверяет, есть ли в расписании занятия, повторяющиеся не каждую неделю.
     *
     * Используется для предупреждения пользователя при изменении дат расписания,
     * так как это может сбить цикл четности недель.
     *
     * @param semesterId идентификатор расписания.
     * @return true, если есть занятия с пропусками недель ("дырками" в цикле).
     */
    suspend fun hasNonRecurringLessons(semesterId: Long): Boolean

    // endregion

    // region Subjects

    /**
     * Возвращает количество занятий по конкретному предмету в расписании.
     *
     * @param semesterId идентификатор расписания.
     * @param subjectName название предмета.
     * @return количество занятий.
     */
    suspend fun getCount(semesterId: Long, subjectName: String): Int

    /**
     * Возвращает поток списка всех уникальных названий предметов в расписании.
     *
     * @param semesterId идентификатор расписания.
     * @return поток списка названий.
     */
    fun getSubjects(semesterId: Long): Flow<List<String>>

    /**
     * Переименовывает предмет во всех занятиях и домашних заданиях расписания.
     *
     * @param semesterId идентификатор расписания.
     * @param oldName старое название предмета.
     * @param newName новое название предмета.
     */
    suspend fun renameSubject(semesterId: Long, oldName: String, newName: String)

    // endregion

    // region Other fields

    /**
     * Возвращает поток списка всех уникальных типов занятий в расписании.
     *
     * @param semesterId идентификатор расписания.
     * @return поток списка типов.
     */
    fun getTypes(semesterId: Long): Flow<List<String>>

    /**
     * Возвращает поток списка всех преподавателей в расписании.
     *
     * @param semesterId идентификатор расписания.
     * @return поток списка имен преподавателей.
     */
    fun getTeachers(semesterId: Long): Flow<List<String>>

    /**
     * Возвращает поток списка всех аудиторий в расписании.
     *
     * @param semesterId идентификатор расписания.
     * @return поток списка названий аудиторий.
     */
    fun getClassrooms(semesterId: Long): Flow<List<String>>

    /**
     * Вычисляет предлагаемое время начала для следующего занятия в этот день.
     *
     * Берет время окончания последнего занятия в этот день и добавляет длительность перемены.
     * Если занятий нет, возвращает время начала по умолчанию из настроек.
     *
     * @param semesterId идентификатор расписания.
     * @param dayOfWeek день недели.
     * @return предлагаемое время начала.
     */
    suspend fun getNextStartTime(semesterId: Long, dayOfWeek: DayOfWeek): LocalTime

    // endregion
}
