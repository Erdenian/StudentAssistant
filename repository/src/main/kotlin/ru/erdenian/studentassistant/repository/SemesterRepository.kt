package ru.erdenian.studentassistant.repository

import com.shopify.livedataktx.LiveDataKtx
import com.shopify.livedataktx.toNullableKtx
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.database.dao.SemesterDao
import ru.erdenian.studentassistant.database.entity.SemesterEntity
import ru.erdenian.studentassistant.entity.ImmutableSortedSet

class SemesterRepository(private val semesterDao: SemesterDao) : BaseRepository() {

    suspend fun insert(name: String, firstDay: LocalDate, lastDay: LocalDate) {
        semesterDao.insert(SemesterEntity(name, firstDay, lastDay))
    }

    suspend fun update(id: Long, name: String, firstDay: LocalDate, lastDay: LocalDate): Unit =
        semesterDao.update(SemesterEntity(name, firstDay, lastDay, id))

    suspend fun delete(id: Long): Unit = semesterDao.delete(id)

    fun getAll(): LiveDataKtx<ImmutableSortedSet<SemesterEntity>> = semesterDao.getAll().map()
    fun get(semesterId: Long): LiveDataKtx<SemesterEntity?> = semesterDao.get(semesterId).toNullableKtx()
    fun getNames(): LiveDataKtx<ImmutableSortedSet<String>> = semesterDao.getNames().map()
}
