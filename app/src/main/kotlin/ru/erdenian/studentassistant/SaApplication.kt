package ru.erdenian.studentassistant

import android.app.Application
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import ru.erdenian.studentassistant.repository.SelectedSemesterRepository
import ru.erdenian.studentassistant.repository.di.repositoryModule

@Suppress("unused")
class SaApplication : Application(), DIAware {

    override val di by DI.lazy {
        val app = this@SaApplication
        import(repositoryModule(app, "schedule.db"))
    }

    override fun onCreate() {
        super.onCreate()

        val selectedSemesterRepository by di.instance<SelectedSemesterRepository>()
        selectedSemesterRepository.activate()
    }
}
