package ru.erdenian.studentassistant.ui.semestereditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import com.shopify.livedataktx.LiveDataKtx
import com.shopify.livedataktx.MediatorLiveDataKtx
import com.shopify.livedataktx.MutableLiveDataKtx
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.repository.ScheduleRepository
import ru.erdenian.studentassistant.repository.entity.SemesterNew

class SemesterEditorViewModel(application: Application) : AndroidViewModel(application) {

    enum class Error {
        SEMESTER_EXISTS,
        WRONG_DATES
    }

    private val ranges = listOf(
        DateTimeConstants.FEBRUARY..DateTimeConstants.MAY,
        DateTimeConstants.SEPTEMBER..DateTimeConstants.DECEMBER
    )

    private val repository = ScheduleRepository(application)

    val name = MutableLiveDataKtx<String>().apply { value = "" }
    val firstDay = MutableLiveDataKtx<LocalDate>()
    val lastDay = MutableLiveDataKtx<LocalDate>()

    val error: LiveDataKtx<Error?> = MediatorLiveDataKtx<Error?>().apply {
        val onChanged = Observer<Any?> {
            value = when {
                name.value in semestersNames.value -> Error.SEMESTER_EXISTS
                firstDay.value >= lastDay.value -> Error.WRONG_DATES
                else -> null
            }
        }

        addSource(name, onChanged)
        addSource(firstDay, onChanged)
        addSource(lastDay, onChanged)
    }

    private val semestersNames = repository.getSemestersNames()

    suspend fun save(): Long {
        check(error.value == null)
        return SemesterNew(
            name.value,
            firstDay.value,
            lastDay.value
        ).also { repository.insertSemester(it) }.id
    }

    init {
        val today = LocalDate.now().withDayOfMonth(1)
        val range = ranges.find { today.monthOfYear <= it.endInclusive } ?: ranges.first()
        firstDay.value = today.withMonthOfYear(range.start)
        lastDay.value = today.withMonthOfYear(range.endInclusive)
    }
}
