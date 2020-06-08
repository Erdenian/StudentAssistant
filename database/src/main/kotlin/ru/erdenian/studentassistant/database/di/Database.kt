package ru.erdenian.studentassistant.database.di

import android.app.Application
import androidx.room.Room
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider
import org.kodein.di.singleton
import ru.erdenian.studentassistant.database.ScheduleDatabase

fun databaseKodein(application: Application, databaseName: String? = null) = DI.direct {
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
