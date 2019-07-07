package ru.erdenian.studentassistant.di

import android.app.Application
import androidx.room.Room
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import ru.erdenian.studentassistant.repository.ScheduleDatabase

fun databaseKodein(application: Application) = Kodein.direct {
    bind() from singleton {
        Room.databaseBuilder(
            application.applicationContext,
            ScheduleDatabase::class.java,
            "schedule.db"
        ).build()
    }

    bind() from provider { instance<ScheduleDatabase>().semesterDao }
    bind() from provider { instance<ScheduleDatabase>().lessonDao }
    bind() from provider { instance<ScheduleDatabase>().homeworkDao }
}
