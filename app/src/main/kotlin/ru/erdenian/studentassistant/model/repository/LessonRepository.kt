package ru.erdenian.studentassistant.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.shopify.livedataktx.toKtx
import com.shopify.livedataktx.toNullableKtx
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.model.dao.LessonDao
import ru.erdenian.studentassistant.model.entity.Lesson
import ru.erdenian.studentassistant.model.entity.LessonRepeat
import ru.erdenian.studentassistant.model.entity.Semester
import ru.erdenian.studentassistant.model.toImmutableSortedSet

@Suppress("TooManyFunctions")
class LessonRepository(private val lessonDao: LessonDao) {

    suspend fun insert(lesson: Lesson) = lessonDao.insert(lesson)
    suspend fun get(semesterId: Long, lessonId: Long) = lessonDao.get(semesterId, lessonId)
    fun get(lesson: Lesson) = lessonDao.getLive(lesson.semesterId, lesson.id).toNullableKtx()
    suspend fun delete(lesson: Lesson) = lessonDao.delete(lesson)

    fun get(semesterId: Long) = lessonDao.get(semesterId).map()
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

    fun get(semester: Semester, day: LocalDate) =
        get(semester.id).map { lessons ->
            val weekNumber = semester.getWeekNumber(day)
            lessons.filter { it.lessonRepeat.repeatsOnDay(day, weekNumber) }
        }.map()

    fun get(semesterId: Long, weekday: Int) =
        get(semesterId).map { lessons ->
            lessons.filter { lesson ->
                if (lesson.lessonRepeat !is LessonRepeat.ByWeekday) false
                else lesson.lessonRepeat.repeatsOnWeekday(weekday)
            }
        }.map()

    private fun <T : Comparable<T>> List<T>.map() = toImmutableSortedSet()
    private fun <T : Comparable<T>> LiveData<List<T>>.map() =
        map { it.toImmutableSortedSet() }.toKtx()
}
