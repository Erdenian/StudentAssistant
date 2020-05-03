package ru.erdenian.studentassistant.database.di

import android.app.Application
import androidx.room.Room
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import ru.erdenian.studentassistant.database.ScheduleDatabase

fun databaseKodein(application: Application, databaseName: String? = null) = Kodein.direct {
    bind() from singleton {
        val builder =
            if (databaseName == null) Room.inMemoryDatabaseBuilder(application, ScheduleDatabase::class.java)
            else Room.databaseBuilder(application, ScheduleDatabase::class.java, databaseName)
        builder.build()
    }

    bind() from provider { instance<ScheduleDatabase>().semesterDao }
    bind() from provider { instance<ScheduleDatabase>().lessonDao }
    bind() from provider { instance<ScheduleDatabase>().homeworkDao }
}
