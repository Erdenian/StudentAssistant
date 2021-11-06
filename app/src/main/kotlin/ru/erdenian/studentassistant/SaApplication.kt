package ru.erdenian.studentassistant

import android.app.Application
import kotlinx.coroutines.GlobalScope
import org.kodein.di.DI
import org.kodein.di.DIAware
import ru.erdenian.studentassistant.repository.di.repositoryModule

@Suppress("unused")
class SaApplication : Application(), DIAware {

    override val di by DI.lazy {
        val app = this@SaApplication
        import(repositoryModule(app, GlobalScope, "schedule.db"))
    }
}
