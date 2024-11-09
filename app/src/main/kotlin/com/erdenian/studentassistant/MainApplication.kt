package com.erdenian.studentassistant

import android.app.Application
import com.erdenian.studentassistant.database.di.DatabaseModule
import com.erdenian.studentassistant.di.DaggerMainComponent
import com.erdenian.studentassistant.di.MainComponent
import com.erdenian.studentassistant.repository.di.RepositoryModule
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

internal class MainApplication : Application() {

    val mainComponent: MainComponent by lazy {
        DaggerMainComponent.factory().create(
            this,
            DatabaseModule("schedule.db"),
            RepositoryModule(@OptIn(DelicateCoroutinesApi::class) GlobalScope, "settings"),
        )
    }
}
