package com.erdenian.studentassistant.database.di

import android.app.Application
import androidx.room.Room
import com.erdenian.studentassistant.database.ScheduleDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule(private val databaseName: String) {

    @Singleton
    @Provides
    internal fun scheduleDatabase(application: Application) =
        Room.databaseBuilder(application, ScheduleDatabase::class.java, databaseName).build()

    @Provides
    internal fun semesterDao(database: ScheduleDatabase) = database.semesterDao

    @Provides
    internal fun lessonDao(database: ScheduleDatabase) = database.lessonDao

    @Provides
    internal fun homeworkDao(database: ScheduleDatabase) = database.homeworkDao
}
