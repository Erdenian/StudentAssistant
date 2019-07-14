package ru.erdenian.studentassistant.di

import android.app.Application
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.kodein.di.weakReference
import ru.erdenian.studentassistant.model.repository.HomeworkRepository
import ru.erdenian.studentassistant.model.repository.LessonRepository
import ru.erdenian.studentassistant.model.repository.SemesterRepository

fun repositoryModule(application: Application) = Kodein.Module(name = "Repository") {
    val db = databaseKodein(application)

    bind() from singleton(ref = weakReference) { SemesterRepository(db.instance()) }
    bind() from singleton(ref = weakReference) { LessonRepository(db.instance()) }
    bind() from singleton(ref = weakReference) { HomeworkRepository(db.instance()) }
}
