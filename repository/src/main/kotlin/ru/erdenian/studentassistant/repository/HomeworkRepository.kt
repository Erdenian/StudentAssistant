package ru.erdenian.studentassistant.repository

import com.shopify.livedataktx.LiveDataKtx
import com.shopify.livedataktx.map
import com.shopify.livedataktx.toNullableKtx
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.database.dao.HomeworkDao
import ru.erdenian.studentassistant.database.entity.HomeworkEntity
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.entity.ImmutableSortedSet

@Suppress("TooManyFunctions")
class HomeworkRepository(private val homeworkDao: HomeworkDao) : BaseRepository() {

    // region Primary actions

    suspend fun insert(subjectName: String, description: String, deadline: LocalDate, semesterId: Long) {
        homeworkDao.insert(HomeworkEntity(subjectName, description, deadline, semesterId))
    }

    suspend fun update(id: Long, subjectName: String, description: String, deadline: LocalDate, semesterId: Long) =
        homeworkDao.update(HomeworkEntity(subjectName, description, deadline, semesterId, id))

    suspend fun delete(id: Long): Unit = homeworkDao.delete(id)

    // endregion

    // region Homeworks

    suspend fun get(id: Long): Homework? = homeworkDao.get(id)

    fun getLive(id: Long): LiveDataKtx<Homework?> = homeworkDao.getLive(id).toNullableKtx().map { it }

    fun getAll(semesterId: Long): LiveDataKtx<ImmutableSortedSet<HomeworkEntity>> = homeworkDao.getAll(semesterId).map()

    suspend fun getCount(semesterId: Long): Int = homeworkDao.getCount(semesterId)

    // endregion

    // region By subject name

    fun get(semesterId: Long, subjectName: String): LiveDataKtx<ImmutableSortedSet<HomeworkEntity>> =
        homeworkDao.get(semesterId, subjectName).map()

    suspend fun getCount(semesterId: Long, subjectName: String): Int = homeworkDao.getCount(semesterId, subjectName)

    suspend fun hasHomeworks(semesterId: Long, subjectName: String): Boolean = homeworkDao.hasHomeworks(semesterId, subjectName)

    // endregion

    // region By deadline

    fun getActual(semesterId: Long): LiveDataKtx<ImmutableSortedSet<HomeworkEntity>> = homeworkDao.getActual(semesterId).map()

    fun getPast(semesterId: Long): LiveDataKtx<ImmutableSortedSet<HomeworkEntity>> = homeworkDao.getPast(semesterId).map()

    fun getActual(semesterId: Long, subjectName: String): LiveDataKtx<ImmutableSortedSet<HomeworkEntity>> =
        homeworkDao.getActual(semesterId, subjectName).map()

    // endregion
}
