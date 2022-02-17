package com.erdenian.studentassistant.repository.di

import android.app.Application
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.kodein.di.singleton
import org.kodein.di.softReference
import com.erdenian.studentassistant.database.di.databaseKodein
import com.erdenian.studentassistant.repository.HomeworkRepository
import com.erdenian.studentassistant.repository.LessonRepository
import com.erdenian.studentassistant.repository.SelectedSemesterRepository
import com.erdenian.studentassistant.repository.SemesterRepository
import com.erdenian.studentassistant.repository.SettingsRepository

fun repositoryModule(
    application: Application,
    applicationCoroutineScope: CoroutineScope,
    databaseName: String? = null
) = DI.Module(name = "Repository") {
    val db = databaseKodein(application, databaseName)

    bindSingleton { SelectedSemesterRepository(applicationCoroutineScope, db.instance()) }

    bind {
        singleton(ref = softReference) {
            SettingsRepository(application.getSharedPreferences("settings", Context.MODE_PRIVATE))
        }
    }

    bind { singleton(ref = softReference) { SemesterRepository(db.instance(), instance()) } }
    bind { singleton(ref = softReference) { LessonRepository(db.instance(), instance(), instance()) } }
    bind { singleton(ref = softReference) { HomeworkRepository(db.instance(), instance()) } }
}
