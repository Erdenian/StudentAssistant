package ru.erdenian.studentassistant.repository

import com.shopify.livedataktx.toNullableKtx
import ru.erdenian.studentassistant.database.dao.HomeworkDao
import ru.erdenian.studentassistant.database.entity.HomeworkEntity
import ru.erdenian.studentassistant.database.entity.LessonEntity

@Suppress("TooManyFunctions")
class HomeworkRepository(private val homeworkDao: HomeworkDao) : BaseRepository() {

    suspend fun insert(homework: HomeworkEntity) = homeworkDao.insert(homework)

    suspend fun get(semesterId: Long, homeworkId: Long) = homeworkDao.get(semesterId, homeworkId)
    fun get(homework: HomeworkEntity) = homeworkDao.getLive(homework.semesterId, homework.id).toNullableKtx()

    suspend fun delete(homework: HomeworkEntity) = homeworkDao.delete(homework)

    fun get(semesterId: Long) = homeworkDao.get(semesterId).map()
    suspend fun getCount(semesterId: Long) = homeworkDao.getCount(semesterId)

    fun get(semesterId: Long, subjectName: String) = homeworkDao.get(semesterId, subjectName).map()
    fun get(lesson: LessonEntity) = homeworkDao.get(lesson.semesterId, lesson.subjectName).map()
    suspend fun getCount(semesterId: Long, subjectName: String) = homeworkDao.getCount(semesterId, subjectName)

    suspend fun hasHomeworks(semesterId: Long, subjectName: String) = homeworkDao.hasHomeworks(semesterId, subjectName)

    fun getActual(semesterId: Long) = homeworkDao.getActual(semesterId).map()
    fun getActual(lesson: LessonEntity) = homeworkDao.getActual(lesson.semesterId, lesson.subjectName).map()
    fun getPast(semesterId: Long) = homeworkDao.getPast(semesterId).map()
}
