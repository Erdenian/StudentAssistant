package ru.erdenian.studentassistant.repository.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDate
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.kodein.di.generic.instance
import ru.erdenian.studentassistant.di.databaseKodein
import ru.erdenian.studentassistant.repository.ScheduleDatabase
import ru.erdenian.studentassistant.repository.entity.Semester
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class SemesterAndroidTest {

    private val kodein = databaseKodein(ApplicationProvider.getApplicationContext())

    private lateinit var database: ScheduleDatabase
    private lateinit var semesterDao: SemesterDao

    @BeforeEach
    fun setUp() {
        database = kodein.instance()
        semesterDao = kodein.instance()
    }

    @AfterEach
    fun tearDown() {
        database.close()
    }

    @Test
    fun semesterInsertTest() = runBlocking {
        assertTrue(semesterDao.getAll().waitValue().isEmpty())
        val semester = Semester(
            "name",
            LocalDate.now().minusDays(1),
            LocalDate.now()
        )
        semesterDao.insert(semester)
        assertEquals(semester, semesterDao.getAll().waitValue().single())
    }

    private suspend fun <T> LiveData<T>.waitValue() = suspendCoroutine<T> { continuation ->
        GlobalScope.launch(Dispatchers.Main) {
            observeForever(object : Observer<T> {
                override fun onChanged(t: T) {
                    removeObserver(this)
                    continuation.resume(t)
                }
            })
        }
    }
}
