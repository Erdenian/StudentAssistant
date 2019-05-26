package ru.erdenian.studentassistant.ui.lessonseditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import com.shopify.livedataktx.LiveDataKtx
import com.shopify.livedataktx.MediatorLiveDataKtx
import com.shopify.livedataktx.MutableLiveDataKtx
import ru.erdenian.studentassistant.repository.ImmutableSortedSet
import ru.erdenian.studentassistant.repository.ScheduleRepository
import ru.erdenian.studentassistant.repository.entity.LessonNew
import ru.erdenian.studentassistant.repository.entity.SemesterNew
import ru.erdenian.studentassistant.repository.immutableSortedSetOf

class LessonsEditorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScheduleRepository(application)

    val semesterId = MutableLiveDataKtx<Long>()
    val semester: LiveDataKtx<SemesterNew?> = MediatorLiveDataKtx<SemesterNew?>().apply {
        val onChanged = Observer<SemesterNew?> { value = it }
        var semesterLiveData: LiveDataKtx<SemesterNew?>? = null
        addSource(semesterId, Observer { semesterId ->
            semesterLiveData?.let { removeSource(it) }
            semesterLiveData = null

            if (semesterId != null) {
                val data = repository.getSemester(semesterId)
                addSource(data, onChanged)
            } else value = null
        })
    }

    fun getLessons(weekday: Int) = MediatorLiveDataKtx<ImmutableSortedSet<LessonNew>>().apply {
        val onChanged = Observer<ImmutableSortedSet<LessonNew>> { value = it }
        var lessonsLiveData: LiveDataKtx<ImmutableSortedSet<LessonNew>>? = null
        addSource(semester, Observer { semester ->
            lessonsLiveData?.let { removeSource(it) }
            lessonsLiveData = null

            if (semester != null) {
                val data = repository.getLessons(semester.id, weekday)
                addSource(data, onChanged)
            } else value = immutableSortedSetOf()
        })
    }

    suspend fun deleteSemester() = repository.delete(checkNotNull(semester.value))
}
