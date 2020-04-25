package ru.erdenian.studentassistant.repository.di

import android.app.Application
import org.joda.time.LocalTime
import org.joda.time.Period
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.kodein.di.weakReference
import ru.erdenian.studentassistant.database.di.databaseKodein
import ru.erdenian.studentassistant.repository.HomeworkRepository
import ru.erdenian.studentassistant.repository.LessonRepository
import ru.erdenian.studentassistant.repository.SemesterRepository

fun repositoryModule(application: Application) = Kodein.Module(name = "Repository") {
    val db = databaseKodein(application, "schedule.db")

    bind() from singleton(ref = weakReference) { SemesterRepository(db.instance()) }
    bind() from singleton(ref = weakReference) {
        LessonRepository(
            db.instance(),
            LocalTime(9, 0),
            Period.minutes(90),
            Period.minutes(10)
        )
    }
    bind() from singleton(ref = weakReference) { HomeworkRepository(db.instance()) }
}
