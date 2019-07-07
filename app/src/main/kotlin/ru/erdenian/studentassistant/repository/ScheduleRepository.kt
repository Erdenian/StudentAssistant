package ru.erdenian.studentassistant.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.shopify.livedataktx.toKtx
import com.shopify.livedataktx.toNullableKtx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.repository.dao.HomeworkDao
import ru.erdenian.studentassistant.repository.dao.LessonDao
import ru.erdenian.studentassistant.repository.dao.SemesterDao
import ru.erdenian.studentassistant.repository.entity.Homework
import ru.erdenian.studentassistant.repository.entity.Lesson
import ru.erdenian.studentassistant.repository.entity.LessonRepeat
import ru.erdenian.studentassistant.repository.entity.Semester

@Suppress("TooManyFunctions")
class ScheduleRepository(
    private val semesterDao: SemesterDao,
    private val lessonDao: LessonDao,
    private val homeworkDao: HomeworkDao
) {

    @Deprecated("Only for debugging")
    suspend fun clear() = withContext(Dispatchers.IO) {
        listOf(
            async { deleteSemesters() },
            async { deleteLessons() },
            async { deleteHomeworks() }
        ).awaitAll()
    }

    // region Semesters

    suspend fun insert(semester: Semester) = semesterDao.insert(semester)
    suspend fun delete(semester: Semester) = semesterDao.delete(semester)

    @Deprecated("Only for debugging")
    suspend fun deleteSemesters() = semesterDao.deleteAll()

    fun getSemesters() = semesterDao.getAll().map()
    fun getSemester(semesterId: Long) = semesterDao.get(semesterId).toNullableKtx()
    fun getSemestersNames() = semesterDao.getNames().map()

    // endregion

    // region Lessons

    suspend fun insert(lesson: Lesson) {
        val oldLesson = getLesson(lesson.semesterId, lesson.id)
        lessonDao.insert(lesson)
        if (
            (oldLesson != null) &&
            (oldLesson.subjectName != lesson.subjectName) &&
            !hasLessons(lesson.semesterId, oldLesson.subjectName)
        ) renameSubject(lesson.semesterId, oldLesson.subjectName, lesson.subjectName)
    }

    suspend fun getLesson(semesterId: Long, lessonId: Long) = lessonDao.get(semesterId, lessonId)

    fun getLesson(lesson: Lesson) = lessonDao.getLive(lesson.semesterId, lesson.id).toNullableKtx()

    suspend fun delete(lesson: Lesson) {
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

    fun getLessons(semester: Semester, day: LocalDate) =
        getLessons(semester.id).map { lessons ->
            val weekNumber = semester.getWeekNumber(day)
            lessons.filter { it.lessonRepeat.repeatsOnDay(day, weekNumber) }
        }.map()

    fun getLessons(semesterId: Long, weekday: Int) =
        getLessons(semesterId).map { lessons ->
            lessons.filter { lesson ->
                if (lesson.lessonRepeat !is LessonRepeat.ByWeekday) false
                else lesson.lessonRepeat.repeatsOnWeekday(weekday)
            }
        }.map()

    // endregion

    // region Homeworks

    suspend fun insert(homework: Homework) = homeworkDao.insert(homework)

    suspend fun getHomework(semesterId: Long, homeworkId: Long) =
        homeworkDao.get(semesterId, homeworkId)

    fun getHomework(homework: Homework) =
        homeworkDao.getLive(homework.semesterId, homework.id).toNullableKtx()

    suspend fun delete(homework: Homework) = homeworkDao.delete(homework)

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

    fun getHomeworks(lesson: Lesson) =
        homeworkDao.get(lesson.semesterId, lesson.subjectName).map()

    suspend fun getHomeworksCount(semesterId: Long, subjectName: String) =
        homeworkDao.getCount(semesterId, subjectName)

    suspend fun hasHomeworks(semesterId: Long, subjectName: String) =
        homeworkDao.hasHomeworks(semesterId, subjectName)

    fun getActualHomeworks(semesterId: Long) = homeworkDao.getActual(semesterId).map()
    fun getPastHomeworks(semesterId: Long) = homeworkDao.getPast(semesterId).map()

    fun getActualHomeworks(semesterId: Long, subjectName: String) =
        homeworkDao.getActual(semesterId, subjectName).map()

    fun getActualHomeworks(lesson: Lesson) =
        homeworkDao.getActual(lesson.semesterId, lesson.subjectName).map()

    fun getPastHomeworks(semesterId: Long, subjectName: String) =
        homeworkDao.getPast(semesterId, subjectName).map()

    fun getPastHomeworks(lesson: Lesson) =
        homeworkDao.getPast(lesson.semesterId, lesson.subjectName).map()

    // endregion

    suspend fun renameSubject(semesterId: Long, oldName: String, newName: String) {
        lessonDao.renameSubject(semesterId, oldName, newName)
        homeworkDao.renameSubject(semesterId, oldName, newName)
    }

    private fun <T : Comparable<T>> List<T>.map() = toImmutableSortedSet()
    private fun <T : Comparable<T>> LiveData<List<T>>.map() =
        map { it.toImmutableSortedSet() }.toKtx()
}
