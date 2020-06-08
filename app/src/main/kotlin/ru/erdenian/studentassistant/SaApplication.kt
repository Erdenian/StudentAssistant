package ru.erdenian.studentassistant

import android.app.Application
import org.joda.time.LocalTime
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import ru.erdenian.studentassistant.repository.SelectedSemesterRepository
import ru.erdenian.studentassistant.repository.di.repositoryModule

class SaApplication : Application(), DIAware {

    @Suppress("MagicNumber")
    override val di by DI.lazy {
        val app = this@SaApplication
        import(
            repositoryModule(
                app,
                "schedule.db",
                LocalTime(9, 0)
            )
        )
    }

    override fun onCreate() {
        super.onCreate()

        val selectedSemesterRepository by di.instance<SelectedSemesterRepository>()
        selectedSemesterRepository.activate()
    }
}
