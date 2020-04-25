package ru.erdenian.studentassistant.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.shopify.livedataktx.LiveDataKtx
import com.shopify.livedataktx.map
import com.shopify.livedataktx.toKtx
import com.shopify.livedataktx.toNullableKtx
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.Period
import ru.erdenian.studentassistant.database.dao.LessonDao
import ru.erdenian.studentassistant.database.entity.ByDateEntity
import ru.erdenian.studentassistant.database.entity.ByWeekdayEntity
import ru.erdenian.studentassistant.database.entity.ClassroomEntity
import ru.erdenian.studentassistant.database.entity.FullLesson
import ru.erdenian.studentassistant.database.entity.LessonEntity
import ru.erdenian.studentassistant.database.entity.TeacherEntity
import ru.erdenian.studentassistant.entity.ImmutableSortedSet
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.entity.toImmutableSortedSet

@Suppress("TooManyFunctions")
class LessonRepository(
    private val lessonDao: LessonDao,
    private val defaultStartTime: LocalTime,
    private val defaultDuration: Period,
    private val defaultBreakLength: Period
) : BaseRepository() {

    // region Primary actions

    suspend fun insert(
        subjectName: String,
        type: String,
        teachers: ImmutableSortedSet<String>,
        classrooms: ImmutableSortedSet<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        lessonRepeat: Lesson.Repeat,
        semesterId: Long
    ) {
        val lessonEntity = LessonEntity(subjectName, type, startTime, endTime, semesterId)
        val teachersEntity = teachers.map { TeacherEntity(it) }
        val classroomsEntity = classrooms.map { ClassroomEntity(it) }
        when (lessonRepeat) {
            is Lesson.Repeat.ByWeekday -> lessonDao.insert(
                lessonEntity, teachersEntity, classroomsEntity,
                ByWeekdayEntity(lessonRepeat.weekday, lessonRepeat.weeks)
            )
            is Lesson.Repeat.ByDates -> lessonDao.insert(
                lessonEntity, teachersEntity, classroomsEntity,
                lessonRepeat.dates.map { ByDateEntity(it) }
            )
        }.let {}
    }

    suspend fun update(
        id: Long,
        subjectName: String,
        type: String,
        teachers: ImmutableSortedSet<String>,
        classrooms: ImmutableSortedSet<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        lessonRepeat: Lesson.Repeat,
        semesterId: Long
    ) {
        val lessonEntity = LessonEntity(subjectName, type, startTime, endTime, semesterId, id)
        val teachersEntity = teachers.map { TeacherEntity(it, id) }
        val classroomsEntity = classrooms.map { ClassroomEntity(it, id) }
        when (lessonRepeat) {
            is Lesson.Repeat.ByWeekday -> lessonDao.insert(
                lessonEntity, teachersEntity, classroomsEntity,
                ByWeekdayEntity(lessonRepeat.weekday, lessonRepeat.weeks, id)
            )
            is Lesson.Repeat.ByDates -> lessonDao.insert(
                lessonEntity, teachersEntity, classroomsEntity,
                lessonRepeat.dates.map { ByDateEntity(it, id) }
            )
        }.let {}
    }

    suspend fun delete(id: Long): Unit = lessonDao.delete(id)

    // endregion

    // region Lessons

    suspend fun get(id: Long): Lesson? = lessonDao.get(id)

    fun getLive(id: Long): LiveDataKtx<Lesson?> = lessonDao.getLive(id).toNullableKtx().map { it }

    fun getAll(semesterId: Long): LiveDataKtx<ImmutableSortedSet<Lesson>> =
        lessonDao.getAll(semesterId).map { (it as List<Lesson>).toImmutableSortedSet() }.toKtx()

    fun getAll(semester: Semester, day: LocalDate): LiveData<ImmutableSortedSet<Lesson>> = getAll(semester.id).map { lessons ->
        val weekNumber = semester.getWeekNumber(day)
        lessons.filter { it.lessonRepeat.repeatsOnDay(day, weekNumber) }.map()
    }

    fun getAll(semesterId: Long, weekday: Int): LiveDataKtx<ImmutableSortedSet<FullLesson>> =
        lessonDao.getAll(semesterId, weekday).map()

    suspend fun getCount(semesterId: Long): Int = lessonDao.getCount(semesterId)

    fun hasLessons(semesterId: Long): LiveData<Boolean> = lessonDao.hasLessons(semesterId)

    // endregion

    // region Subjects

    suspend fun getCount(semesterId: Long, subjectName: String): Int = lessonDao.getCount(semesterId, subjectName)

    fun getSubjects(semesterId: Long): LiveDataKtx<ImmutableSortedSet<String>> = lessonDao.getSubjects(semesterId).map()

    suspend fun renameSubject(semesterId: Long, oldName: String, newName: String): Unit =
        lessonDao.renameSubject(semesterId, oldName, newName)

    // endregion

    // region Other fields

    fun getTypes(semesterId: Long): LiveDataKtx<ImmutableSortedSet<String>> = lessonDao.getTypes(semesterId).map()

    fun getTeachers(semesterId: Long): LiveDataKtx<ImmutableSortedSet<String>> = lessonDao.getTeachers(semesterId).map()

    fun getClassrooms(semesterId: Long): LiveDataKtx<ImmutableSortedSet<String>> = lessonDao.getClassrooms(semesterId).map()

    suspend fun getDuration(semesterId: Long): Period = lessonDao.getDuration(semesterId) ?: defaultDuration

    suspend fun getNextStartTime(semesterId: Long, weekday: Int): LocalTime =
        lessonDao.getNextStartTime(semesterId, weekday, defaultBreakLength) ?: defaultStartTime

    // endregion
}
