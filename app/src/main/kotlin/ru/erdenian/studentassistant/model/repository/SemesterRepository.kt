package ru.erdenian.studentassistant.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.shopify.livedataktx.toKtx
import com.shopify.livedataktx.toNullableKtx
import ru.erdenian.studentassistant.model.dao.SemesterDao
import ru.erdenian.studentassistant.model.entity.Semester
import ru.erdenian.studentassistant.model.toImmutableSortedSet

@Suppress("TooManyFunctions")
class SemesterRepository(private val semesterDao: SemesterDao) {

    suspend fun insert(semester: Semester) = semesterDao.insert(semester)
    suspend fun delete(semester: Semester) = semesterDao.delete(semester)

    fun getAll() = semesterDao.getAll().map()
    fun get(semesterId: Long) = semesterDao.get(semesterId).toNullableKtx()
    fun getNames() = semesterDao.getNames().map()

    private fun <T : Comparable<T>> List<T>.map() = toImmutableSortedSet()
    private fun <T : Comparable<T>> LiveData<List<T>>.map() =
        map { it.toImmutableSortedSet() }.toKtx()
}
