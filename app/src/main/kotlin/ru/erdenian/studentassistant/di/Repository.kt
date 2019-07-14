package ru.erdenian.studentassistant.di

import android.app.Application
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.kodein.di.weakReference
import ru.erdenian.studentassistant.model.ScheduleRepository

fun repositoryModule(application: Application) = Kodein.Module(name = "Repository") {
    val db = databaseKodein(application)

    bind() from singleton(ref = weakReference) {
        ScheduleRepository(db.instance(), db.instance(), db.instance())
    }
}
