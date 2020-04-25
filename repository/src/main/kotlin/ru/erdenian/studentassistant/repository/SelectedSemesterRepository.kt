package ru.erdenian.studentassistant.repository

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.database.dao.SemesterDao
import ru.erdenian.studentassistant.database.entity.SemesterEntity
import ru.erdenian.studentassistant.entity.Semester

class SelectedSemesterRepository(semesterDao: SemesterDao) {

    val selectedLiveData: MutableLiveData<Semester?> = MediatorLiveData<Semester?>().apply {
        var previousSemesters = emptyList<SemesterEntity>()

        addSource(semesterDao.getAllLiveData()) { semesters ->
            fun List<Semester>.default() = find { LocalDate.now() in it.firstDay..it.lastDay } ?: semesters.lastOrNull()

            value = when {
                (value == null) -> semesters.default()
                (semesters.size > previousSemesters.size) -> (semesters - previousSemesters).first()
                else -> semesters.find { it.id == value?.id } ?: semesters.default()
            }
            previousSemesters = semesters
        }
    }

    val selected: Semester get() = checkNotNull(selectedLiveData.value)

    private val observer = Observer<Any?> {}

    fun activate(): Unit = selectedLiveData.observeForever(observer)
    fun deactivate(): Unit = selectedLiveData.removeObserver(observer)
}
