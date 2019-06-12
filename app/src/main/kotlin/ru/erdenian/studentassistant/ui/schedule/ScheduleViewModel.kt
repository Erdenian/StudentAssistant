package ru.erdenian.studentassistant.ui.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import com.shopify.livedataktx.MediatorLiveDataKtx
import com.shopify.livedataktx.MutableLiveDataKtx
import com.shopify.livedataktx.switchMap
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.extensions.asLiveData
import ru.erdenian.studentassistant.extensions.liveDataOf
import ru.erdenian.studentassistant.repository.ScheduleRepository
import ru.erdenian.studentassistant.repository.entity.SemesterNew
import ru.erdenian.studentassistant.repository.immutableSortedSetOf

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScheduleRepository(application)

    val allSemesters = repository.getSemesters()
    val selectedSemester: MutableLiveDataKtx<SemesterNew?> =
        MediatorLiveDataKtx<SemesterNew?>().apply {
            addSource(allSemesters, Observer { semesters ->
                if (value !in semesters) value = semesters.find { semester ->
                    LocalDate.now() in semester.firstDay..semester.lastDay
                } ?: semesters.lastOrNull()
            })
        }

    fun getLessons(day: LocalDate) = selectedSemester.asLiveData.switchMap { semester ->
        semester?.let { repository.getLessons(it, day) } ?: liveDataOf(immutableSortedSetOf())
    }
}
