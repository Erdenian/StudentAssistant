package ru.erdenian.studentassistant.ui.lessoninformation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.shopify.livedataktx.MutableLiveDataKtx
import com.shopify.livedataktx.switchMap
import ru.erdenian.studentassistant.extensions.asLiveData
import ru.erdenian.studentassistant.extensions.liveDataOf
import ru.erdenian.studentassistant.extensions.setIfEmpty
import ru.erdenian.studentassistant.repository.ScheduleRepository
import ru.erdenian.studentassistant.repository.entity.LessonNew
import ru.erdenian.studentassistant.repository.immutableSortedSetOf

class LessonInformationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScheduleRepository(application)

    private val privateLesson = MutableLiveDataKtx<LessonNew>()

    fun init(lesson: LessonNew) {
        privateLesson.setIfEmpty(lesson)
    }

    val lesson = privateLesson.asLiveData.switchMap { lesson ->
        liveDataOf(lesson, repository.getLesson(lesson))
    }

    val homeworks = lesson.switchMap { lesson ->
        lesson?.let { repository.getActualHomeworks(it) } ?: liveDataOf(immutableSortedSetOf())
    }
}
