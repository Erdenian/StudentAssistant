package com.erdenian.studentassistant

import android.app.Application
import com.erdenian.studentassistant.repository.di.repositoryModule
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import org.kodein.di.DI
import org.kodein.di.DIAware

@Suppress("unused")
internal class MainApplication : Application(), DIAware {

    override val di by DI.lazy {
        val app = this@MainApplication
        import(repositoryModule(app, @OptIn(DelicateCoroutinesApi::class) GlobalScope, "schedule.db"))
    }
}
