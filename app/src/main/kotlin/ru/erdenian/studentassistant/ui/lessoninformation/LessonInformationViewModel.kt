package ru.erdenian.studentassistant.ui.lessoninformation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.shopify.livedataktx.MutableLiveDataKtx
import com.shopify.livedataktx.switchMap
import ru.erdenian.studentassistant.repository.ScheduleRepository
import ru.erdenian.studentassistant.repository.entity.LessonNew

class LessonInformationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScheduleRepository(application)

    val lesson = MutableLiveDataKtx<LessonNew>()
    val homeworks = lesson.switchMap { repository.getActualHomeworks(it) }
}
