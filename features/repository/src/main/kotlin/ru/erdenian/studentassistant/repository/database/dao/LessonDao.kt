package ru.erdenian.studentassistant.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import ru.erdenian.studentassistant.repository.database.entity.ByDateEntity
import ru.erdenian.studentassistant.repository.database.entity.ByWeekdayEntity
import ru.erdenian.studentassistant.repository.database.entity.ClassroomEntity
import ru.erdenian.studentassistant.repository.database.entity.FullLesson
import ru.erdenian.studentassistant.repository.database.entity.LessonEntity
import ru.erdenian.studentassistant.repository.database.entity.TeacherEntity

/**
 * DAO для работы с занятиями.
 *
 * Содержит методы для CRUD операций над занятиями и связанными сущностями
 * (преподаватели, аудитории, правила повторения).
 */
@Dao
internal abstract class LessonDao {

    // region Primary actions

    /**
     * Вставляет занятие, повторяющееся по дням недели.
     *
     * Выполняется в транзакции: сначала вставляется само занятие, затем связанные сущности.
     *
     * @param lesson сущность занятия.
     * @param teachers набор преподавателей.
     * @param classrooms набор аудиторий.
     * @param byWeekday сущность, описывающая повторение по дням недели.
     * @return идентификатор созданного занятия.
     */
    @Transaction
    open suspend fun insert(
        lesson: LessonEntity,
        teachers: Set<TeacherEntity>,
        classrooms: Set<ClassroomEntity>,
        byWeekday: ByWeekdayEntity,
    ): Long = withContext(Dispatchers.IO) {
        val id = insert(lesson)
        insert(
            teachers.onEach { it.lessonId = id },
            classrooms.onEach { it.lessonId = id },
            byWeekday.apply { lessonId = id },
        )
        id
    }

    /**
     * Вставляет занятие, проходящее в конкретные даты.
     *
     * Выполняется в транзакции.
     *
     * @param lesson сущность занятия.
     * @param teachers набор преподавателей.
     * @param classrooms набор аудиторий.
     * @param byDates набор дат проведения занятия.
     * @return идентификатор созданного занятия.
     */
    @Transaction
    open suspend fun insert(
        lesson: LessonEntity,
        teachers: Set<TeacherEntity>,
        classrooms: Set<ClassroomEntity>,
        byDates: Set<ByDateEntity>,
    ): Long {
        require(byDates.isNotEmpty()) { "Dates list must contain at least one item" }
        return withContext(Dispatchers.IO) {
            val id = insert(lesson)
            insert(
                teachers.onEach { it.lessonId = id },
                classrooms.onEach { it.lessonId = id },
                byDates.onEach { it.lessonId = id },
            )
            id
        }
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    protected abstract suspend fun insert(lesson: LessonEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    protected abstract suspend fun insert(
        teachers: Set<TeacherEntity>,
        classrooms: Set<ClassroomEntity>,
        byWeekday: ByWeekdayEntity,
    )

    @Insert(onConflict = OnConflictStrategy.ABORT)
    protected abstract suspend fun insert(
        teachers: Set<TeacherEntity>,
        classrooms: Set<ClassroomEntity>,
        byDates: Set<ByDateEntity>,
    )

    /**
     * Обновляет занятие (повторение по дням недели).
     *
     * Реализовано как удаление старого и вставка нового, чтобы корректно обновить
     * списки преподавателей и аудиторий.
     *
     * @param lesson сущность занятия.
     * @param teachers набор преподавателей.
     * @param classrooms набор аудиторий.
     * @param byWeekday сущность, описывающая повторение по дням недели.
     */
    @Transaction
    open suspend fun update(
        lesson: LessonEntity,
        teachers: Set<TeacherEntity>,
        classrooms: Set<ClassroomEntity>,
        byWeekday: ByWeekdayEntity,
    ): Unit = withContext(Dispatchers.IO) {
        delete(lesson.id)
        insert(lesson = lesson, teachers = teachers, classrooms = classrooms, byWeekday = byWeekday)
    }

    /**
     * Обновляет занятие (повторение по датам).
     *
     * Реализовано как удаление старого и вставка нового.
     *
     * @param lesson сущность занятия.
     * @param teachers набор преподавателей.
     * @param classrooms набор аудиторий.
     * @param byDates набор дат проведения занятия.
     */
    @Transaction
    open suspend fun update(
        lesson: LessonEntity,
        teachers: Set<TeacherEntity>,
        classrooms: Set<ClassroomEntity>,
        byDates: Set<ByDateEntity>,
    ): Unit = withContext(Dispatchers.IO) {
        delete(lesson.id)
        insert(lesson = lesson, teachers = teachers, classrooms = classrooms, byDates = byDates)
    }

    /**
     * Удаляет занятие по идентификатору.
     *
     * @param id идентификатор занятия.
     */
    @Query("DELETE FROM lessons WHERE _id = :id")
    abstract suspend fun delete(id: Long)

    // endregion

    // region Lessons

    /**
     * Получает полную информацию о занятии (со списками учителей и т.д.) по ID.
     *
     * @param id идентификатор занятия.
     * @return полное занятие или null.
     */
    @Transaction
    @Query("SELECT * FROM lessons WHERE _id = :id")
    abstract suspend fun get(id: Long): FullLesson?

    /**
     * Возвращает поток полной информации о занятии.
     *
     * @param id идентификатор занятия.
     * @return поток с полным занятием.
     */
    @Transaction
    @Query("SELECT * FROM lessons WHERE _id = :id")
    abstract fun getFlow(id: Long): Flow<FullLesson?>

    /**
     * Возвращает поток всех занятий в расписании.
     *
     * @param semesterId идентификатор расписания.
     * @return поток списка всех занятий.
     */
    @Transaction
    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId ORDER BY start_time, end_time, subject_name, type, _id, semester_id")
    abstract fun getAllFlow(semesterId: Long): Flow<List<FullLesson>>

    /**
     * Возвращает поток занятий на конкретный день.
     *
     * Этот метод выполняет сложный запрос, объединяя регулярные занятия и занятия по датам.
     *
     * **Логика проверки недель:**
     * Поле `weeks` в таблице `by_weekday` хранит строку из '0' и '1'.
     * Функция `SUBSTR` используется для проверки, стоит ли '1' на позиции, соответствующей текущей неделе.
     * Индекс недели вычисляется как `(:weekNumber % length(w.weeks)) + 1` (+1 т.к. SQL индексация с 1).
     *
     * @param semesterId ID расписания.
     * @param dayOfWeek день недели запрошенной даты.
     * @param weekNumber номер недели от начала расписания (для проверки чередования недель).
     * @param date сама дата (для проверки занятий по датам).
     * @return поток списка занятий на этот день.
     */
    @Transaction
    @Query(
        """
        SELECT l.* FROM lessons AS l
        INNER JOIN by_weekday AS w ON l._id = w.lesson_id
        WHERE l.semester_id = :semesterId
          AND w.day_of_week = :dayOfWeek
          AND substr(w.weeks, (:weekNumber % length(w.weeks)) + 1, 1) = '1'
        UNION
        SELECT l.* FROM lessons AS l
        INNER JOIN by_date AS d ON l._id = d.lesson_id
        WHERE l.semester_id = :semesterId
          AND d.date = :date
        ORDER BY start_time, end_time, subject_name, type, _id, semester_id
    """,
    )
    abstract fun getAllFlow(
        semesterId: Long,
        dayOfWeek: DayOfWeek,
        weekNumber: Int,
        date: LocalDate,
    ): Flow<List<FullLesson>>

    /**
     * Возвращает поток всех занятий в расписании для определенного дня недели.
     *
     * @param semesterId идентификатор расписания.
     * @param dayOfWeek день недели.
     * @return поток списка занятий.
     */
    @Transaction
    @Query("SELECT lessons.* FROM lessons INNER JOIN by_weekday ON by_weekday.lesson_id = lessons._id WHERE semester_id = :semesterId AND day_of_week = :dayOfWeek ORDER BY start_time, end_time, subject_name, type, _id, semester_id")
    abstract fun getAllFlow(semesterId: Long, dayOfWeek: DayOfWeek): Flow<List<FullLesson>>

    /**
     * Возвращает количество занятий в расписании.
     *
     * @param semesterId идентификатор расписания.
     * @return количество занятий.
     */
    @Query("SELECT COUNT(_id) FROM lessons WHERE semester_id = :semesterId")
    abstract suspend fun getCount(semesterId: Long): Int

    /**
     * Возвращает поток, сообщающий о наличии занятий в расписании.
     *
     * @param semesterId идентификатор расписания.
     * @return поток boolean (true, если есть хотя бы одно занятие).
     */
    @Query("SELECT EXISTS(SELECT _id FROM lessons WHERE semester_id = :semesterId)")
    abstract fun hasLessonsFlow(semesterId: Long): Flow<Boolean>

    /**
     * Проверяет, есть ли в расписании занятия, которые повторяются не каждую неделю.
     *
     * Логика:
     * Если строка weeks (хранящаяся как последовательность '0' и '1') содержит '0',
     * значит есть недели, когда занятие не проводится (пропуски).
     * В этом случае изменение даты начала расписания (сдвиг понедельника первой недели) может привести
     * к тому, что даты занятий "съедут" относительно календаря (например, четные недели станут нечетными
     * в понимании пользователя).
     *
     * Если же все занятия проводятся каждую неделю (все '1'), то сдвиг начала расписания
     * не повлияет на даты повторений.
     *
     * @param semesterId идентификатор расписания.
     * @return true, если есть такие занятия.
     */
    @Query("SELECT EXISTS(SELECT lesson_id FROM by_weekday WHERE lesson_id IN (SELECT _id FROM lessons WHERE semester_id = :semesterId) AND weeks LIKE '%0%')")
    abstract suspend fun hasNonRecurringLessons(semesterId: Long): Boolean

    // endregion

    // region Subjects

    /**
     * Возвращает количество занятий по предмету в расписании.
     *
     * @param semesterId идентификатор расписания.
     * @param subjectName название предмета.
     * @return количество занятий.
     */
    @Query("SELECT COUNT(_id) FROM lessons WHERE semester_id = :semesterId AND subject_name = :subjectName")
    abstract suspend fun getCount(semesterId: Long, subjectName: String): Int

    /**
     * Возвращает поток уникальных названий предметов в расписании.
     *
     * @param semesterId идентификатор расписания.
     * @return поток списка названий.
     */
    @Query("SELECT DISTINCT subject_name FROM lessons WHERE semester_id = :semesterId ORDER BY subject_name")
    abstract fun getSubjectsFlow(semesterId: Long): Flow<List<String>>

    /**
     * Переименовывает предмет во всех занятиях и домашних заданиях.
     *
     * @param semesterId идентификатор расписания.
     * @param oldName старое название.
     * @param newName новое название.
     */
    @Transaction
    open suspend fun renameSubject(semesterId: Long, oldName: String, newName: String) = withContext(Dispatchers.IO) {
        renameLessonsSubject(semesterId, oldName, newName)
        renameHomeworksSubject(semesterId, oldName, newName)
    }

    @Query("UPDATE lessons SET subject_name = :newName WHERE semester_id = :semesterId AND subject_name = :oldName")
    protected abstract suspend fun renameLessonsSubject(semesterId: Long, oldName: String, newName: String)

    @Query("UPDATE homeworks SET subject_name = :newName WHERE semester_id = :semesterId AND subject_name = :oldName")
    protected abstract suspend fun renameHomeworksSubject(semesterId: Long, oldName: String, newName: String)

    // endregion

    // region Other fields

    /**
     * Возвращает поток уникальных типов занятий.
     *
     * @param semesterId идентификатор расписания.
     * @return поток списка типов.
     */
    @Query("SELECT DISTINCT type FROM lessons WHERE type IS NOT NULL AND semester_id = :semesterId ORDER BY type")
    abstract fun getTypesFlow(semesterId: Long): Flow<List<String>>

    /**
     * Возвращает поток имен всех преподавателей.
     *
     * @param semesterId идентификатор расписания.
     * @return поток списка имен.
     */
    @Query("SELECT DISTINCT t.name FROM teachers AS t INNER JOIN lessons AS l ON l._id = t.lesson_id WHERE l.semester_id = :semesterId ORDER BY t.name")
    abstract fun getTeachersFlow(semesterId: Long): Flow<List<String>>

    /**
     * Возвращает поток названий всех аудиторий.
     *
     * @param semesterId идентификатор расписания.
     * @return поток списка аудиторий.
     */
    @Query("SELECT DISTINCT c.name FROM classrooms AS c INNER JOIN lessons AS l ON l._id = c.lesson_id WHERE l.semester_id = :semesterId ORDER BY c.name")
    abstract fun getClassroomsFlow(semesterId: Long): Flow<List<String>>

    /**
     * Возвращает время окончания последнего занятия в указанный день недели.
     * Используется для подстановки времени начала следующего занятия.
     *
     * @param semesterId идентификатор расписания.
     * @param dayOfWeek день недели.
     * @return время окончания или null.
     */
    @Query("SELECT l.end_time FROM lessons AS l INNER JOIN by_weekday AS w ON w.lesson_id = l._id WHERE l.semester_id = :semesterId AND w.day_of_week = :dayOfWeek ORDER BY end_time DESC LIMIT 1")
    abstract suspend fun getLastEndTime(semesterId: Long, dayOfWeek: DayOfWeek): LocalTime?

    // endregion
}
