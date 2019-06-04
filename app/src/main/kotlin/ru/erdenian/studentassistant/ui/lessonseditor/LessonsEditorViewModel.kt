package ru.erdenian.studentassistant.ui.lessonseditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.shopify.livedataktx.MutableLiveDataKtx
import com.shopify.livedataktx.switchMap
import ru.erdenian.studentassistant.extensions.liveDataOf
import ru.erdenian.studentassistant.repository.ScheduleRepository
import ru.erdenian.studentassistant.repository.immutableSortedSetOf

class LessonsEditorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScheduleRepository(application)

    val semesterId = MutableLiveDataKtx<Long>()
    val semester = semesterId.switchMap { repository.getSemester(it) }

    fun getLessons(weekday: Int) = semester.switchMap { semester ->
        semester
            ?.let { repository.getLessons(it.id, weekday) }
            ?: liveDataOf(immutableSortedSetOf())
    }

    suspend fun deleteSemester() = repository.delete(checkNotNull(semester.value))
}
