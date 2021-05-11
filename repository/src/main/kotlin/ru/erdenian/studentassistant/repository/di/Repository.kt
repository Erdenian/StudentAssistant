package ru.erdenian.studentassistant.repository.di

import android.app.Application
import android.content.Context
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.kodein.di.singleton
import org.kodein.di.softReference
import ru.erdenian.studentassistant.database.di.databaseKodein
import ru.erdenian.studentassistant.repository.HomeworkRepository
import ru.erdenian.studentassistant.repository.LessonRepository
import ru.erdenian.studentassistant.repository.SelectedSemesterRepository
import ru.erdenian.studentassistant.repository.SemesterRepository
import ru.erdenian.studentassistant.repository.SettingsRepository

fun repositoryModule(
    application: Application,
    databaseName: String? = null
) = DI.Module(name = "Repository") {
    val db = databaseKodein(application, databaseName)

    bindSingleton { SelectedSemesterRepository(db.instance()) }

    bind {
        singleton(ref = softReference) {
            SettingsRepository(application.getSharedPreferences("settings", Context.MODE_PRIVATE))
        }
    }

    bind { singleton(ref = softReference) { SemesterRepository(db.instance()) } }
    bind { singleton(ref = softReference) { LessonRepository(db.instance(), instance(), instance()) } }
    bind { singleton(ref = softReference) { HomeworkRepository(db.instance(), instance()) } }
}
