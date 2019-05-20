package ru.erdenian.studentassistant.repository

import android.content.Context
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.repository.dao.HomeworkDao
import ru.erdenian.studentassistant.repository.dao.LessonDao
import ru.erdenian.studentassistant.repository.dao.SemesterDao
import ru.erdenian.studentassistant.repository.entity.HomeworkNew
import ru.erdenian.studentassistant.repository.entity.LessonNew
import ru.erdenian.studentassistant.repository.entity.SemesterNew
import ru.erdenian.studentassistant.schedule.LessonRepeat

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
    fun insertSemester(semester: SemesterNew) = semesterDao.insert(semester)

    val allSemesters = semesterDao.getAll()
    fun getSemester(semesterId: Long) = semesterDao.get(semesterId)
    fun getSemestersNames() = semesterDao.getNames()
    fun getLessonsCount(semesterId: Long) = semesterDao.lessonsCount(semesterId)
    fun hasLessons(semesterId: Long) = semesterDao.hasLessons(semesterId)
    fun delete(semester: SemesterNew) = semesterDao.delete(semester)
    // endregion

    // region Lessons
    fun insertLesson(lesson: LessonNew) = lessonDao.insert(lesson)

    fun getLesson(semesterId: Long, lessonId: Long) = lessonDao.get(semesterId, lessonId)
    fun getLessons(semesterId: Long) = lessonDao.get(semesterId)
    fun getLessons(semesterId: Long, subjectName: String) = lessonDao.get(semesterId, subjectName)
    fun getSubjects(semesterId: Long) = lessonDao.getSubjects(semesterId).separate()
    fun getTypes(semesterId: Long) = lessonDao.getTypes(semesterId).separate()
    fun getTeachers(semesterId: Long) = lessonDao.getTeachers(semesterId).separate()
    fun getClassrooms(semesterId: Long) = lessonDao.getClassrooms(semesterId).separate()
    fun getLessonLength(semesterId: Long) = lessonDao.getLessonLength(semesterId)
    fun getLessonsCount(semesterId: Long, subjectName: String) =
        lessonDao.getCount(semesterId, subjectName)

    fun deleteLesson(lesson: LessonNew) {
        lessonDao.delete(lesson)
        if (getLessonsCount(lesson.semesterId, lesson.subjectName) == 0) {
            homeworkDao.delete(lesson.semesterId, lesson.subjectName)
        }
    }

    fun getLessons(semester: SemesterNew, day: LocalDate): List<LessonNew> {
        val weekNumber = semester.getWeekNumber(day)
        return getLessons(semester.id).value?.filter {
            it.lessonRepeat.repeatsOnDay(day, weekNumber)
        } ?: emptyList()
    }

    fun getLessons(semesterId: Long, weekday: Int) = getLessons(semesterId).value?.filter {
        (it.lessonRepeat as? LessonRepeat.ByWeekday)?.repeatsOnWeekday(weekday) ?: false
    } ?: emptyList()
    // endregion

    // region Homework
    fun insertHomework(homework: HomeworkNew) = homeworkDao.insert(homework)

    fun getHomework(semesterId: Long, homeworkId: Long) = homeworkDao.get(semesterId, homeworkId)
    fun getHomeworks(semesterId: Long) = homeworkDao.get(semesterId)
    fun getHomeworks(semesterId: Long, subjectName: String) =
        homeworkDao.get(semesterId, subjectName)

    fun getActualHomeworks(semesterId: Long) = homeworkDao.getActual(semesterId)
    fun getActualHomeworks(semesterId: Long, subjectName: String) =
        homeworkDao.getActual(semesterId, subjectName)

    fun getPastHomeworks(semesterId: Long) = homeworkDao.getPast(semesterId)
    fun getPastHomeworks(semesterId: Long, subjectName: String) =
        homeworkDao.getPast(semesterId, subjectName)

    fun getHomeworksCount(semesterId: Long, subjectName: String) =
        homeworkDao.getCount(semesterId, subjectName)

    fun deleteHomework(homework: HomeworkNew) = homeworkDao.delete(homework)
    // endregion

    fun renameSubject(semesterId: Long, oldName: String, newName: String) {
        lessonDao.renameSubject(semesterId, oldName, newName)
        homeworkDao.renameSubject(semesterId, oldName, newName)
    }

    private fun List<String>.separate() = asSequence()
        .flatMap { it.split(Converters.SEPARATOR).asSequence() }
        .distinct()
        .toList()
}
