package ru.erdenian.studentassistant.ui.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import com.shopify.livedataktx.LiveDataKtx
import com.shopify.livedataktx.MediatorLiveDataKtx
import com.shopify.livedataktx.MutableLiveDataKtx
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.repository.ImmutableSortedSet
import ru.erdenian.studentassistant.repository.ScheduleRepository
import ru.erdenian.studentassistant.repository.entity.LessonNew
import ru.erdenian.studentassistant.repository.entity.SemesterNew
import ru.erdenian.studentassistant.repository.toImmutableSortedSet

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val scheduleRepository = ScheduleRepository(application)

    val allSemesters = scheduleRepository.getAllSemesters()
    val semestersNames = scheduleRepository.getSemestersNames()
    val selectedSemester = MutableLiveDataKtx<SemesterNew?>()
    private var privateLessons: LiveDataKtx<ImmutableSortedSet<LessonNew>>? = null
    private val privateLessonsMediator = MediatorLiveDataKtx<ImmutableSortedSet<LessonNew>>()
    val lessons: LiveDataKtx<ImmutableSortedSet<LessonNew>> get() = privateLessonsMediator

    private val semestersObserver = Observer<ImmutableSortedSet<SemesterNew>> { value ->
        when (selectedSemester.safeValue) {
            null, !in allSemesters.value -> selectedSemester.value = value.find {
                LocalDate.now() in it.firstDay..it.lastDay
            } ?: value.lastOrNull()
        }
    }

    private val selectedSemesterObserver = Observer<SemesterNew?> { value ->
        val mediatorObserver = Observer<ImmutableSortedSet<LessonNew>> {
            privateLessonsMediator.value = it
        }
        if (value == null) {
            privateLessons?.let { privateLessonsMediator.removeSource(it) }
            privateLessons = null
            privateLessonsMediator.value = setOf<LessonNew>().toImmutableSortedSet()
        } else {
            privateLessons?.let { privateLessonsMediator.removeSource(it) }
            privateLessons = scheduleRepository.getLessons(value.id).also { lessons ->
                privateLessonsMediator.addSource(lessons, mediatorObserver)
            }
        }
    }

    init {
        allSemesters.observeForever(semestersObserver)
        selectedSemester.observeForever(selectedSemesterObserver)
    }

    override fun onCleared() {
        allSemesters.removeObserver(semestersObserver)
        selectedSemester.removeObserver(selectedSemesterObserver)
    }
}
