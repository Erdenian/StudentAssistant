package ru.erdenian.studentassistant.ui.lessoninformation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.switchMap
import com.shopify.livedataktx.MutableLiveDataKtx
import com.shopify.livedataktx.toKtx
import com.shopify.livedataktx.toNullableKtx
import ru.erdenian.studentassistant.repository.ScheduleRepository
import ru.erdenian.studentassistant.repository.entity.Lesson
import ru.erdenian.studentassistant.repository.immutableSortedSetOf
import ru.erdenian.studentassistant.utils.asLiveData
import ru.erdenian.studentassistant.utils.liveDataOf
import ru.erdenian.studentassistant.utils.setIfEmpty

class LessonInformationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScheduleRepository(application)

    private val privateLesson = MutableLiveDataKtx<Lesson>()

    fun init(lesson: Lesson) {
        privateLesson.setIfEmpty(lesson)
    }

    val lesson = privateLesson.asLiveData.switchMap { lesson ->
        liveDataOf(lesson, repository.getLesson(lesson))
    }.toNullableKtx()

    val homeworks = lesson.switchMap { lesson ->
        lesson?.let { repository.getActualHomeworks(it) } ?: liveDataOf(
            immutableSortedSetOf()
        )
    }.toKtx()
}
