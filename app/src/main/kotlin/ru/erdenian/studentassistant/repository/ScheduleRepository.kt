package ru.erdenian.studentassistant.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.shopify.livedataktx.toKtx
import kotlinx.coroutines.Dispatchers
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

    // region Semesters

    suspend fun insertSemester(semester: SemesterNew) = semesterDao.insert(semester)
    fun getAllSemesters() = semesterDao.getAll().map()
    suspend fun getSemester(semesterId: Long) = semesterDao.get(semesterId)
    fun getSemestersNames() = semesterDao.getNames().map()
    suspend fun getLessonsCount(semesterId: Long) = semesterDao.lessonsCount(semesterId)
    suspend fun hasLessons(semesterId: Long) = semesterDao.hasLessons(semesterId)
    suspend fun delete(semester: SemesterNew) = semesterDao.delete(semester)

    // endregion

    // region Lessons

    suspend fun insertLesson(lesson: LessonNew) = lessonDao.insert(lesson)
    suspend fun getLesson(semesterId: Long, lessonId: Long) = lessonDao.get(semesterId, lessonId)
    fun getLessons(semesterId: Long) = lessonDao.get(semesterId).map()
    private suspend fun getLessonsList(semesterId: Long) = lessonDao.getList(semesterId)
    suspend fun getLessons(semesterId: Long, subjectName: String) =
        lessonDao.get(semesterId, subjectName).map()

    suspend fun getSubjects(semesterId: Long) = lessonDao.getSubjects(semesterId).map()
    suspend fun getTypes(semesterId: Long) = lessonDao.getTypes(semesterId).map()
    suspend fun getTeachers(semesterId: Long) = lessonDao.getTeachers(semesterId).map()
    suspend fun getClassrooms(semesterId: Long) = lessonDao.getClassrooms(semesterId).map()
    suspend fun getLessonLength(semesterId: Long) = lessonDao.getLessonLength(semesterId)
    suspend fun getLessonsCount(semesterId: Long, subjectName: String) =
        lessonDao.getCount(semesterId, subjectName)

    suspend fun hasSubject(semesterId: Long, subjectName: String) =
        lessonDao.hasSubject(semesterId, subjectName)

    suspend fun deleteLesson(lesson: LessonNew) {
        lessonDao.delete(lesson)
        if (!hasSubject(lesson.semesterId, lesson.subjectName)) {
            homeworkDao.delete(lesson.semesterId, lesson.subjectName)
        }
    }

    suspend fun getLessons(semester: SemesterNew, day: LocalDate) = withContext(Dispatchers.IO) {
        val weekNumber = semester.getWeekNumber(day)
        getLessonsList(semester.id).filter { it.lessonRepeat.repeatsOnDay(day, weekNumber) }.map()
    }

    suspend fun getLessons(semesterId: Long, weekday: Int) = withContext(Dispatchers.IO) {
        getLessonsList(semesterId).filter {
            (it.lessonRepeat as? LessonRepeatNew.ByWeekday)?.repeatsOnWeekday(weekday) ?: false
        }.map()
    }

    // endregion

    // region Homework

    suspend fun insertHomework(homework: HomeworkNew) = homeworkDao.insert(homework)
    suspend fun getHomework(semesterId: Long, homeworkId: Long) =
        homeworkDao.get(semesterId, homeworkId)

    fun getHomeworks(semesterId: Long) = homeworkDao.get(semesterId).map()
    suspend fun getHomeworks(semesterId: Long, subjectName: String) =
        homeworkDao.get(semesterId, subjectName).map()

    fun getActualHomeworks(semesterId: Long) = homeworkDao.getActual(semesterId).map()
    suspend fun getActualHomeworks(semesterId: Long, subjectName: String) =
        homeworkDao.getActual(semesterId, subjectName).map()

    fun getPastHomeworks(semesterId: Long) = homeworkDao.getPast(semesterId).map()
    suspend fun getPastHomeworks(semesterId: Long, subjectName: String) =
        homeworkDao.getPast(semesterId, subjectName).map()

    suspend fun getHomeworksCount(semesterId: Long, subjectName: String) =
        homeworkDao.getCount(semesterId, subjectName)

    suspend fun deleteHomework(homework: HomeworkNew) = homeworkDao.delete(homework)

    // endregion

    suspend fun renameSubject(semesterId: Long, oldName: String, newName: String) {
        lessonDao.renameSubject(semesterId, oldName, newName)
        homeworkDao.renameSubject(semesterId, oldName, newName)
    }

    private fun <T : Comparable<T>> List<T>.map() = toImmutableSortedSet()
    private fun <T : Comparable<T>> LiveData<List<T>>.map() =
        map { it.toImmutableSortedSet() }.toKtx()
}
