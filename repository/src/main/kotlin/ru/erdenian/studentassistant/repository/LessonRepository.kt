package ru.erdenian.studentassistant.repository

import com.shopify.livedataktx.toNullableKtx
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.repository.database.dao.LessonDao

@Suppress("TooManyFunctions")
class LessonRepository(private val lessonDao: LessonDao) : BaseRepository() {

    suspend fun insert(lesson: Lesson) = lessonDao.insert(lesson)
    suspend fun get(semesterId: Long, lessonId: Long) = lessonDao.get(semesterId, lessonId)
    fun get(lesson: Lesson) = lessonDao.getLive(lesson.semesterId, lesson.id).toNullableKtx()
    fun hasLessons(semesterId: Long) = lessonDao.hasLessons(semesterId)
    suspend fun delete(lesson: Lesson) = lessonDao.delete(lesson)

    fun get(semesterId: Long) = lessonDao.get(semesterId).map()
    fun get(semester: Semester, day: LocalDate) = lessonDao.get(semester, day).map()
    fun get(semesterId: Long, weekday: Int) = lessonDao.get(semesterId, weekday).map()
    suspend fun getCount(semesterId: Long) = lessonDao.getCount(semesterId)
    suspend fun getCount(semesterId: Long, subjectName: String) =
        lessonDao.getCount(semesterId, subjectName)

    fun getSubjects(semesterId: Long) = lessonDao.getSubjects(semesterId).map()
    suspend fun renameSubject(semesterId: Long, oldName: String, newName: String) =
        lessonDao.renameSubject(semesterId, oldName, newName)

    fun getTypes(semesterId: Long) = lessonDao.getTypes(semesterId).map()
    fun getTeachers(semesterId: Long) = lessonDao.getTeachers(semesterId).map()
    fun getClassrooms(semesterId: Long) = lessonDao.getClassrooms(semesterId).map()
    suspend fun getLessonLength(semesterId: Long) = lessonDao.getLessonLength(semesterId)
    suspend fun getNextStartTime(semesterId: Long, weekday: Int) =
        lessonDao.getNextStartTime(semesterId, weekday)
}
