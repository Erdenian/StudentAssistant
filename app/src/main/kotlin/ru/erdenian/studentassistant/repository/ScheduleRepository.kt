package ru.erdenian.studentassistant.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.shopify.livedataktx.toKtx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.repository.dao.HomeworkDao
import ru.erdenian.studentassistant.repository.dao.LessonDao
import ru.erdenian.studentassistant.repository.dao.SemesterDao
import ru.erdenian.studentassistant.repository.entity.HomeworkNew
import ru.erdenian.studentassistant.repository.entity.LessonNew
import ru.erdenian.studentassistant.repository.entity.LessonRepeatNew
import ru.erdenian.studentassistant.repository.entity.SemesterNew

class ScheduleRepository(context: Context) {

    private val semesterDao: SemesterDao
    private val lessonDao: LessonDao
    private val homeworkDao: HomeworkDao

    init {
        ScheduleDatabase.getInstance(context).also { db ->
            semesterDao = db.semesterDao
            lessonDao = db.lessonDao
            homeworkDao = db.homeworkDao
        }
    }

    @Deprecated("Only for debugging")
    suspend fun clear() = withContext(Dispatchers.IO) {
        listOf(
            async { deleteSemesters() },
            async { deleteLessons() },
            async { deleteHomeworks() }
        ).awaitAll()
    }

    // region Semesters

    suspend fun insert(semester: SemesterNew) = semesterDao.insert(semester)
    suspend fun delete(semester: SemesterNew) = semesterDao.delete(semester)

    @Deprecated("Only for debugging")
    suspend fun deleteSemesters() = semesterDao.deleteAll()

    fun getSemesters() = semesterDao.getAll().map()
    fun getSemester(semesterId: Long) = semesterDao.get(semesterId)
    fun getSemestersNames() = semesterDao.getNames().map()

    // endregion

    // region Lessons

    suspend fun insert(lesson: LessonNew) {
        val oldLesson = getLesson(lesson.semesterId, lesson.id)
        lessonDao.insert(lesson)
        if (
            (oldLesson != null) &&
            (oldLesson.subjectName != lesson.subjectName) &&
            !hasLessons(lesson.semesterId, oldLesson.subjectName)
        ) renameSubject(lesson.semesterId, oldLesson.subjectName, lesson.subjectName)
    }

    suspend fun getLesson(semesterId: Long, lessonId: Long) = lessonDao.get(semesterId, lessonId)

    suspend fun delete(lesson: LessonNew) {
        lessonDao.delete(lesson)
        if (!hasLessons(lesson.semesterId, lesson.subjectName)) {
            homeworkDao.delete(lesson.semesterId, lesson.subjectName)
        }
    }

    @Deprecated("Only for debugging")
    suspend fun deleteLessons(semesterId: Long) = lessonDao.deleteAll(semesterId)

    @Deprecated("Only for debugging")
    suspend fun deleteLessons() = lessonDao.deleteAll()

    fun getLessons(semesterId: Long) = lessonDao.get(semesterId).map()
    suspend fun getLessonsCount(semesterId: Long) = lessonDao.getCount(semesterId)
    suspend fun hasLessons(semesterId: Long) = lessonDao.hasLessons(semesterId)

    fun getLessons(semesterId: Long, subjectName: String) =
        lessonDao.get(semesterId, subjectName).map()

    suspend fun getLessonsCount(semesterId: Long, subjectName: String) =
        lessonDao.getCount(semesterId, subjectName)

    suspend fun hasLessons(semesterId: Long, subjectName: String) =
        lessonDao.hasLessons(semesterId, subjectName)

    fun getSubjects(semesterId: Long) = lessonDao.getSubjects(semesterId).map()

    fun getTypes(semesterId: Long) = lessonDao.getTypes(semesterId).map()
    fun getTeachers(semesterId: Long) = lessonDao.getTeachers(semesterId).map()
    fun getClassrooms(semesterId: Long) = lessonDao.getClassrooms(semesterId).map()
    suspend fun getLessonLength(semesterId: Long) = lessonDao.getLessonLength(semesterId)

    fun getLessons(semester: SemesterNew, day: LocalDate) =
        getLessons(semester.id).map { lessons ->
            val weekNumber = semester.getWeekNumber(day)
            lessons.filter { it.lessonRepeat.repeatsOnDay(day, weekNumber) }.map()
        }

    fun getLessons(semesterId: Long, weekday: Int) =
        getLessons(semesterId).map { lessons ->
            lessons.filter { lesson ->
                if (lesson.lessonRepeat !is LessonRepeatNew.ByWeekday) false
                else lesson.lessonRepeat.repeatsOnWeekday(weekday)
            }.map()
        }

    // endregion

    // region Homeworks

    suspend fun insert(homework: HomeworkNew) = homeworkDao.insert(homework)

    suspend fun getHomework(semesterId: Long, homeworkId: Long) =
        homeworkDao.get(semesterId, homeworkId)

    suspend fun delete(homework: HomeworkNew) = homeworkDao.delete(homework)

    suspend fun deleteHomeworks(semesterId: Long, subjectName: String) =
        homeworkDao.delete(semesterId, subjectName)

    @Deprecated("Only for debugging")
    suspend fun deleteHomeworks(semesterId: Long) = homeworkDao.deleteAll(semesterId)

    @Deprecated("Only for debugging")
    suspend fun deleteHomeworks() = homeworkDao.deleteAll()

    fun getHomeworks(semesterId: Long) = homeworkDao.get(semesterId).map()
    suspend fun getHomeworksCount(semesterId: Long) = homeworkDao.getCount(semesterId)
    suspend fun hasHomeworks(semesterId: Long) = homeworkDao.hasHomeworks(semesterId)

    fun getHomeworks(semesterId: Long, subjectName: String) =
        homeworkDao.get(semesterId, subjectName).map()

    suspend fun getHomeworksCount(semesterId: Long, subjectName: String) =
        homeworkDao.getCount(semesterId, subjectName)

    suspend fun hasHomeworks(semesterId: Long, subjectName: String) =
        homeworkDao.hasHomeworks(semesterId, subjectName)

    fun getActualHomeworks(semesterId: Long) = homeworkDao.getActual(semesterId).map()
    fun getPastHomeworks(semesterId: Long) = homeworkDao.getPast(semesterId).map()

    fun getActualHomeworks(semesterId: Long, subjectName: String) =
        homeworkDao.getActual(semesterId, subjectName).map()

    fun getPastHomeworks(semesterId: Long, subjectName: String) =
        homeworkDao.getPast(semesterId, subjectName).map()

    // endregion

    suspend fun renameSubject(semesterId: Long, oldName: String, newName: String) {
        lessonDao.renameSubject(semesterId, oldName, newName)
        homeworkDao.renameSubject(semesterId, oldName, newName)
    }

    private fun <T : Comparable<T>> List<T>.map() = toImmutableSortedSet()
    private fun <T : Comparable<T>> LiveData<List<T>>.map() =
        map { it.toImmutableSortedSet() }.toKtx()
}
