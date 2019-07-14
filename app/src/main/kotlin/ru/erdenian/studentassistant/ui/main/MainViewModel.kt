package ru.erdenian.studentassistant.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.switchMap
import com.shopify.livedataktx.MediatorLiveDataKtx
import com.shopify.livedataktx.MutableLiveDataKtx
import com.shopify.livedataktx.toKtx
import org.joda.time.LocalDate
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import ru.erdenian.studentassistant.model.ScheduleRepository
import ru.erdenian.studentassistant.model.entity.Semester
import ru.erdenian.studentassistant.model.immutableSortedSetOf
import ru.erdenian.studentassistant.utils.asLiveData
import ru.erdenian.studentassistant.utils.liveDataOf

class MainViewModel(application: Application) : AndroidViewModel(application), KodeinAware {

    override val kodein by kodein()

    private val repository: ScheduleRepository by instance()

    val allSemesters = repository.getSemesters()
    val selectedSemester: MutableLiveDataKtx<Semester?> =
        MediatorLiveDataKtx<Semester?>().apply {
            addSource(allSemesters, Observer { semesters ->
                if (value !in semesters) value = semesters.find { semester ->
                    LocalDate.now() in semester.firstDay..semester.lastDay
                } ?: semesters.lastOrNull()
            })
        }

    fun getLessons(day: LocalDate) = selectedSemester.asLiveData.switchMap { semester ->
        semester?.let { repository.getLessons(it, day) } ?: liveDataOf(
            immutableSortedSetOf()
        )
    }.toKtx()

    fun getActualHomeworks() = selectedSemester.asLiveData.switchMap { semester ->
        semester?.let { repository.getActualHomeworks(it.id) } ?: liveDataOf(
            immutableSortedSetOf()
        )
    }.toKtx()

    fun getPastHomeworks() = selectedSemester.asLiveData.switchMap { semester ->
        semester?.let { repository.getPastHomeworks(it.id) } ?: liveDataOf(
            immutableSortedSetOf()
        )
    }.toKtx()
}
