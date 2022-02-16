package ru.erdenian.studentassistant.database.di

import android.app.Application
import androidx.room.Room
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import ru.erdenian.studentassistant.database.ScheduleDatabase

fun databaseKodein(application: Application, databaseName: String? = null) = DI.direct {
    bindSingleton {
        val builder =
            if (databaseName == null) {
                Room.inMemoryDatabaseBuilder(application, ScheduleDatabase::class.java)
            } else {
                Room.databaseBuilder(application, ScheduleDatabase::class.java, databaseName)
            }
        builder.build()
    }

    bindProvider { instance<ScheduleDatabase>().semesterDao }
    bindProvider { instance<ScheduleDatabase>().lessonDao }
    bindProvider { instance<ScheduleDatabase>().homeworkDao }
}
