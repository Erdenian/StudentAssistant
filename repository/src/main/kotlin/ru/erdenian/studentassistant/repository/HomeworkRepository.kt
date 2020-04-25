package ru.erdenian.studentassistant.repository

import com.shopify.livedataktx.LiveDataKtx
import com.shopify.livedataktx.toNullableKtx
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.database.dao.HomeworkDao
import ru.erdenian.studentassistant.database.entity.HomeworkEntity
import ru.erdenian.studentassistant.database.entity.LessonEntity
import ru.erdenian.studentassistant.entity.ImmutableSortedSet

@Suppress("TooManyFunctions")
class HomeworkRepository(private val homeworkDao: HomeworkDao) : BaseRepository() {

    suspend fun insert(subjectName: String, description: String, deadline: LocalDate, semesterId: Long) {
        homeworkDao.insert(HomeworkEntity(subjectName, description, deadline, semesterId))
    }

    suspend fun update(id: Long, subjectName: String, description: String, deadline: LocalDate, semesterId: Long) =
        homeworkDao.update(HomeworkEntity(subjectName, description, deadline, semesterId, id))

    suspend fun delete(homework: HomeworkEntity): Unit = homeworkDao.delete(homework)

    suspend fun get(semesterId: Long, homeworkId: Long): HomeworkEntity? = homeworkDao.get(semesterId, homeworkId)
    fun get(homework: HomeworkEntity): LiveDataKtx<HomeworkEntity?> =
        homeworkDao.getLive(homework.semesterId, homework.id).toNullableKtx()

    fun get(semesterId: Long): LiveDataKtx<ImmutableSortedSet<HomeworkEntity>> = homeworkDao.get(semesterId).map()
    suspend fun getCount(semesterId: Long): Int = homeworkDao.getCount(semesterId)

    fun get(semesterId: Long, subjectName: String): LiveDataKtx<ImmutableSortedSet<HomeworkEntity>> =
        homeworkDao.get(semesterId, subjectName).map()

    fun get(lesson: LessonEntity): LiveDataKtx<ImmutableSortedSet<HomeworkEntity>> =
        homeworkDao.get(lesson.semesterId, lesson.subjectName).map()

    suspend fun getCount(semesterId: Long, subjectName: String): Int = homeworkDao.getCount(semesterId, subjectName)

    suspend fun hasHomeworks(semesterId: Long, subjectName: String): Boolean = homeworkDao.hasHomeworks(semesterId, subjectName)

    fun getActual(semesterId: Long): LiveDataKtx<ImmutableSortedSet<HomeworkEntity>> = homeworkDao.getActual(semesterId).map()
    fun getActual(lesson: LessonEntity): LiveDataKtx<ImmutableSortedSet<HomeworkEntity>> =
        homeworkDao.getActual(lesson.semesterId, lesson.subjectName).map()

    fun getPast(semesterId: Long): LiveDataKtx<ImmutableSortedSet<HomeworkEntity>> = homeworkDao.getPast(semesterId).map()
}
