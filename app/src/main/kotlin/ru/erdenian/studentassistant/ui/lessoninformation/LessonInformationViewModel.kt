package ru.erdenian.studentassistant.ui.lessoninformation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.shopify.livedataktx.MutableLiveDataKtx
import com.shopify.livedataktx.switchMap
import ru.erdenian.studentassistant.extensions.asLiveData
import ru.erdenian.studentassistant.extensions.liveDataOf
import ru.erdenian.studentassistant.extensions.setIfEmpty
import ru.erdenian.studentassistant.repository.ScheduleRepository
import ru.erdenian.studentassistant.repository.entity.Lesson
import ru.erdenian.studentassistant.repository.immutableSortedSetOf

class LessonInformationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScheduleRepository(application)

    private val privateLesson = MutableLiveDataKtx<Lesson>()

    fun init(lesson: Lesson) {
        privateLesson.setIfEmpty(lesson)
    }

    val lesson = privateLesson.asLiveData.switchMap { lesson ->
        liveDataOf(lesson, repository.getLesson(lesson))
    }

    val homeworks = lesson.switchMap { lesson ->
        lesson?.let { repository.getActualHomeworks(it) } ?: liveDataOf(immutableSortedSetOf())
    }
}
