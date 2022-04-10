package com.erdenian.studentassistant

import android.app.Application
import com.erdenian.studentassistant.database.di.DatabaseModule
import com.erdenian.studentassistant.di.AppComponent
import com.erdenian.studentassistant.di.AppModule
import com.erdenian.studentassistant.di.DaggerAppComponent
import com.erdenian.studentassistant.repository.di.RepositoryModule
import com.erdenian.studentassistant.repository.di.repositoryModule
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import org.kodein.di.DI
import org.kodein.di.DIAware

@Suppress("unused")
internal class MainApplication : Application(), DIAware {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .repositoryModule(
                RepositoryModule(@OptIn(DelicateCoroutinesApi::class) GlobalScope, "settings")
            )
            .databaseModule(
                DatabaseModule("schedule.db")
            )
            .build()
    }

    override val di by DI.lazy {
        val app = this@MainApplication
        import(repositoryModule(app, @OptIn(DelicateCoroutinesApi::class) GlobalScope, "schedule.db"))
    }
}
