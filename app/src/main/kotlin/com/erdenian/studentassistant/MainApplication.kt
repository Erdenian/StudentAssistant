package com.erdenian.studentassistant

import android.app.Application
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import org.kodein.di.DI
import org.kodein.di.DIAware
import com.erdenian.studentassistant.repository.di.repositoryModule

@Suppress("unused")
internal class MainApplication : Application(), DIAware {

    override val di by DI.lazy {
        val app = this@MainApplication
        import(repositoryModule(app, @OptIn(DelicateCoroutinesApi::class) GlobalScope, "schedule.db"))
    }
}
