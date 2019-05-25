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
import ru.erdenian.studentassistant.repository.immutableSortedSetOf

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScheduleRepository(application)

    val allSemesters = repository.getAllSemesters()
    val semestersNames = repository.getSemestersNames()
    val selectedSemester = MutableLiveDataKtx<SemesterNew?>()

    fun getLessons(day: LocalDate) = MediatorLiveDataKtx<ImmutableSortedSet<LessonNew>>().apply {
        val onChanged = Observer<ImmutableSortedSet<LessonNew>> { value = it }
        var lessonsLiveData: LiveDataKtx<ImmutableSortedSet<LessonNew>>? = null
        addSource(selectedSemester, Observer { semester ->
            lessonsLiveData?.let { removeSource(it) }
            lessonsLiveData = null

            if (semester != null) {
                val data = repository.getLessons(semester, day)
                addSource(data, onChanged)
            } else value = immutableSortedSetOf()
        })
    }

    private val semestersObserver = Observer<ImmutableSortedSet<SemesterNew>> { value ->
        when (selectedSemester.safeValue) {
            null, !in allSemesters.value -> selectedSemester.value = value.find {
                LocalDate.now() in it.firstDay..it.lastDay
            } ?: value.lastOrNull()
        }
    }

    init {
        allSemesters.observeForever(semestersObserver)
    }

    override fun onCleared() {
        allSemesters.removeObserver(semestersObserver)
    }
}
