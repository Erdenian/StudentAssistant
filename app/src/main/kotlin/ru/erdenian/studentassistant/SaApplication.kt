package ru.erdenian.studentassistant

import android.app.Application
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import ru.erdenian.studentassistant.di.repositoryModule

class SaApplication : Application(), KodeinAware {

    override val kodein by Kodein.lazy {
        val app = this@SaApplication
        import(repositoryModule(app))
    }
}
