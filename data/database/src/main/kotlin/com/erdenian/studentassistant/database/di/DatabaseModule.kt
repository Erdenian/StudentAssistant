package com.erdenian.studentassistant.database.di

import android.app.Application
import androidx.room.Room
import com.erdenian.studentassistant.database.ScheduleDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    internal fun scheduleDatabase(application: Application) =
        Room.databaseBuilder(application, ScheduleDatabase::class.java, "schedule.db").build()

    @Provides
    internal fun semesterDao(database: ScheduleDatabase) = database.semesterDao

    @Provides
    internal fun lessonDao(database: ScheduleDatabase) = database.lessonDao

    @Provides
    internal fun homeworkDao(database: ScheduleDatabase) = database.homeworkDao
}
