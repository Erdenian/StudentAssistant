package ru.erdenian.studentassistant.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.database.dao.SemesterDao
import ru.erdenian.studentassistant.database.entity.SemesterEntity
import ru.erdenian.studentassistant.entity.Semester

class SelectedSemesterRepository(semesterDao: SemesterDao) {

    private val selectedLiveDataPrivate: MutableLiveData<Semester?> = MediatorLiveData<Semester?>().apply {
        var previousSemesters = emptyList<SemesterEntity>()

        addSource(semesterDao.getAllLiveData()) { semesters ->
            val now = LocalDate.now()
            fun List<Semester>.default() = find { now in it.firstDay..it.lastDay } ?: lastOrNull()

            value = when {
                (value == null) -> semesters.default()
                (semesters.size > previousSemesters.size) -> (semesters - previousSemesters).first()
                else -> semesters.find { it.id == value?.id } ?: semesters.default()
            }
            previousSemesters = semesters
        }
    }
    val selectedLiveData: LiveData<Semester?> get() = selectedLiveDataPrivate

    val selected: Semester get() = checkNotNull(selectedLiveData.value)

    fun selectSemester(semester: Semester) {
        selectedLiveDataPrivate.value = semester
    }

    private val observer = Observer<Any?> {}

    fun activate(): Unit = selectedLiveData.observeForever(observer)
    fun deactivate(): Unit = selectedLiveData.removeObserver(observer)
}
