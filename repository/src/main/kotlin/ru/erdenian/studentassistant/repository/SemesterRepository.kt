package ru.erdenian.studentassistant.repository

import com.shopify.livedataktx.toNullableKtx
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.repository.dao.SemesterDao

class SemesterRepository(private val semesterDao: SemesterDao) : BaseRepository() {

    suspend fun insert(semester: Semester) = semesterDao.insert(semester)
    suspend fun delete(semester: Semester) = semesterDao.delete(semester)

    fun getAll() = semesterDao.getAll().map()
    fun get(semesterId: Long) = semesterDao.get(semesterId).toNullableKtx()
    fun getNames() = semesterDao.getNames().map()
}
