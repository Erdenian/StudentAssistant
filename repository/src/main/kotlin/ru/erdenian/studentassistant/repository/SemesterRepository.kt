package ru.erdenian.studentassistant.repository

import com.shopify.livedataktx.toNullableKtx
import ru.erdenian.studentassistant.database.dao.SemesterDao
import ru.erdenian.studentassistant.database.entity.SemesterEntity

class SemesterRepository(private val semesterDao: SemesterDao) : BaseRepository() {

    suspend fun insert(semester: SemesterEntity) = semesterDao.insert(semester)
    suspend fun delete(semester: SemesterEntity) = semesterDao.delete(semester)

    fun getAll() = semesterDao.getAll().map()
    fun get(semesterId: Long) = semesterDao.get(semesterId).toNullableKtx()
    fun getNames() = semesterDao.getNames().map()
}
