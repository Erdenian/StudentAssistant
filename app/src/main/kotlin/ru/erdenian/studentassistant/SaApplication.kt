package ru.erdenian.studentassistant

import android.app.Application
import org.joda.time.LocalTime
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import ru.erdenian.studentassistant.repository.SelectedSemesterRepository
import ru.erdenian.studentassistant.repository.di.repositoryModule

class SaApplication : Application(), KodeinAware {

    @Suppress("MagicNumber")
    override val kodein by Kodein.lazy {
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

        val selectedSemesterRepository by kodein.instance<SelectedSemesterRepository>()
        selectedSemesterRepository.activate()
    }
}
