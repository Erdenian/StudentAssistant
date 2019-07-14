package ru.erdenian.studentassistant.model.repository

import com.shopify.livedataktx.toNullableKtx
import ru.erdenian.studentassistant.model.dao.SemesterDao
import ru.erdenian.studentassistant.model.entity.Semester

class SemesterRepository(private val semesterDao: SemesterDao) : BaseRepository() {

    suspend fun insert(semester: Semester) = semesterDao.insert(semester)
    suspend fun delete(semester: Semester) = semesterDao.delete(semester)

    fun getAll() = semesterDao.getAll().map()
    fun get(semesterId: Long) = semesterDao.get(semesterId).toNullableKtx()
    fun getNames() = semesterDao.getNames().map()
}
