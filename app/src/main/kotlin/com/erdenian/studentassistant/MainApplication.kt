package com.erdenian.studentassistant

import android.app.Application
import com.erdenian.studentassistant.database.di.DatabaseModule
import com.erdenian.studentassistant.di.ApplicationModule
import com.erdenian.studentassistant.di.DaggerMainComponent
import com.erdenian.studentassistant.di.MainComponent
import com.erdenian.studentassistant.repository.di.RepositoryModule
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

@Suppress("unused")
internal class MainApplication : Application() {

    val mainComponent: MainComponent by lazy {
        DaggerMainComponent.builder()
            .applicationModule(ApplicationModule(this))
            .repositoryModule(
                RepositoryModule(@OptIn(DelicateCoroutinesApi::class) GlobalScope, "settings")
            )
            .databaseModule(
                DatabaseModule("schedule.db")
            )
            .build()
    }
}
