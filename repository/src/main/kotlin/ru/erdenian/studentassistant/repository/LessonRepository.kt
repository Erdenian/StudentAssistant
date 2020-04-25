package ru.erdenian.studentassistant.repository

import androidx.lifecycle.LiveData
import com.shopify.livedataktx.LiveDataKtx
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
import ru.erdenian.studentassistant.database.entity.SemesterEntity
import ru.erdenian.studentassistant.database.entity.TeacherEntity
import ru.erdenian.studentassistant.entity.ImmutableSortedSet
import ru.erdenian.studentassistant.entity.Lesson

@Suppress("TooManyFunctions")
class LessonRepository(
    private val lessonDao: LessonDao,
    private val defaultStartTime: LocalTime,
    private val defaultDuration: Period,
    private val defaultBreakLength: Period
) : BaseRepository() {

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

    suspend fun get(semesterId: Long, lessonId: Long): FullLesson? = lessonDao.get(semesterId, lessonId)
    fun get(lesson: LessonEntity): LiveDataKtx<FullLesson?> = lessonDao.getLive(lesson.semesterId, lesson.id).toNullableKtx()
    fun hasLessons(semesterId: Long): LiveData<Boolean> = lessonDao.hasLessons(semesterId)
    suspend fun delete(id: Long): Unit = lessonDao.delete(id)

    fun get(semesterId: Long): LiveDataKtx<ImmutableSortedSet<FullLesson>> = lessonDao.get(semesterId).map()
    fun get(semester: SemesterEntity, day: LocalDate): LiveDataKtx<ImmutableSortedSet<FullLesson>> =
        lessonDao.get(semester, day).map()

    fun get(semesterId: Long, weekday: Int): LiveDataKtx<ImmutableSortedSet<FullLesson>> =
        lessonDao.get(semesterId, weekday).map()

    suspend fun getCount(semesterId: Long): Int = lessonDao.getCount(semesterId)
    suspend fun getCount(semesterId: Long, subjectName: String): Int = lessonDao.getCount(semesterId, subjectName)

    fun getSubjects(semesterId: Long): LiveDataKtx<ImmutableSortedSet<String>> = lessonDao.getSubjects(semesterId).map()
    suspend fun renameSubject(semesterId: Long, oldName: String, newName: String): Unit =
        lessonDao.renameSubject(semesterId, oldName, newName)

    fun getTypes(semesterId: Long): LiveDataKtx<ImmutableSortedSet<String>> = lessonDao.getTypes(semesterId).map()
    fun getTeachers(semesterId: Long): LiveDataKtx<ImmutableSortedSet<String>> = lessonDao.getTeachers(semesterId).map()
    fun getClassrooms(semesterId: Long): LiveDataKtx<ImmutableSortedSet<String>> = lessonDao.getClassrooms(semesterId).map()
    suspend fun getDuration(semesterId: Long): Period = lessonDao.getDuration(semesterId) ?: defaultDuration
    suspend fun getNextStartTime(semesterId: Long, weekday: Int): LocalTime =
        lessonDao.getNextStartTime(semesterId, weekday, defaultBreakLength) ?: defaultStartTime
}
