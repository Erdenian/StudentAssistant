package ru.erdenian.studentassistant.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.switchMap
import com.shopify.livedataktx.MediatorLiveDataKtx
import com.shopify.livedataktx.toKtx
import com.shopify.livedataktx.toNullableKtx
import org.joda.time.LocalDate
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.entity.immutableSortedSetOf
import ru.erdenian.studentassistant.repository.HomeworkRepository
import ru.erdenian.studentassistant.repository.LessonRepository
import ru.erdenian.studentassistant.repository.SemesterRepository
import ru.erdenian.studentassistant.utils.asLiveData
import ru.erdenian.studentassistant.utils.liveDataOf

class MainViewModel(application: Application) : AndroidViewModel(application), KodeinAware {

    override val kodein by kodein()
    private val semesterRepository: SemesterRepository by instance()
    private val lessonRepository: LessonRepository by instance()
    private val homeworkRepository: HomeworkRepository by instance()

    val allSemesters = semesterRepository.getAll()

    private var unknownSemester: Semester? = null
    private val selectedSemesterPrivate = MediatorLiveDataKtx<Semester?>().apply {
        addSource(allSemesters, Observer { semesters ->
            fun Semester.find() = semesters.find { it.id == id }
            postValue(
                unknownSemester?.find() ?: value?.find() ?: semesters.find { semester ->
                    LocalDate.now() in semester.firstDay..semester.lastDay
                } ?: semesters.lastOrNull()
            )
            unknownSemester = null
        })
    }
    val selectedSemester = selectedSemesterPrivate.asLiveData
    fun selectSemester(semester: Semester) {
        if (semester in allSemesters.value) selectedSemesterPrivate.value = semester
        else unknownSemester = semester
    }

    val hasLessons = selectedSemester.asLiveData.switchMap { semester ->
        semester?.let { s ->
            @Suppress("UNCHECKED_CAST")
            lessonRepository.hasLessons(s.id) as LiveData<Boolean?>
        } ?: liveDataOf(null)
    }.toNullableKtx()

    fun getLessons(day: LocalDate) = selectedSemester.asLiveData.switchMap { semester ->
        semester?.let { lessonRepository.get(it, day) } ?: liveDataOf(immutableSortedSetOf())
    }.toKtx()

    fun getActualHomeworks() = selectedSemester.asLiveData.switchMap { semester ->
        semester?.let { homeworkRepository.getActual(it.id) } ?: liveDataOf(immutableSortedSetOf())
    }.toKtx()

    fun getPastHomeworks() = selectedSemester.asLiveData.switchMap { semester ->
        semester?.let { homeworkRepository.getPast(it.id) } ?: liveDataOf(immutableSortedSetOf())
    }.toKtx()

    suspend fun delete(homework: Homework) = homeworkRepository.delete(homework)
}
