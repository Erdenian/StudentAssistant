package com.erdenian.studentassistant

import android.app.Application
import com.erdenian.studentassistant.database.di.DatabaseModule
import com.erdenian.studentassistant.di.MainComponentHolder
import com.erdenian.studentassistant.repository.di.RepositoryModule
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

internal class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MainComponentHolder.create(
            application = this,
            databaseModule = DatabaseModule("schedule.db"),
            repositoryModule = RepositoryModule(@OptIn(DelicateCoroutinesApi::class) GlobalScope, "settings"),
        )
    }
}
