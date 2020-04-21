package ru.erdenian.studentassistant.repository

import com.shopify.livedataktx.toNullableKtx
import ru.erdenian.studentassistant.database.dao.HomeworkDao
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.entity.Lesson

@Suppress("TooManyFunctions")
class HomeworkRepository(private val homeworkDao: HomeworkDao) : BaseRepository() {

    suspend fun insert(homework: Homework) = homeworkDao.insert(homework)

    suspend fun get(semesterId: Long, homeworkId: Long) = homeworkDao.get(semesterId, homeworkId)
    fun get(homework: Homework) =
        homeworkDao.getLive(homework.semesterId, homework.id).toNullableKtx()

    suspend fun delete(homework: Homework) = homeworkDao.delete(homework)

    fun get(semesterId: Long) = homeworkDao.get(semesterId).map()
    suspend fun getCount(semesterId: Long) = homeworkDao.getCount(semesterId)

    fun get(semesterId: Long, subjectName: String) =
        homeworkDao.get(semesterId, subjectName).map()

    fun get(lesson: Lesson) =
        homeworkDao.get(lesson.semesterId, lesson.subjectName).map()

    suspend fun getCount(semesterId: Long, subjectName: String) =
        homeworkDao.getCount(semesterId, subjectName)

    suspend fun hasHomeworks(semesterId: Long, subjectName: String) =
        homeworkDao.hasHomeworks(semesterId, subjectName)

    fun getActual(semesterId: Long) = homeworkDao.getActual(semesterId).map()
    fun getPast(semesterId: Long) = homeworkDao.getPast(semesterId).map()

    fun getActual(lesson: Lesson) =
        homeworkDao.getActual(lesson.semesterId, lesson.subjectName).map()
}
