package com.erdenian.studentassistant.repository.di

import android.app.Application
import androidx.room.Room
import com.erdenian.studentassistant.repository.RepositoryConfig
import com.erdenian.studentassistant.repository.database.ScheduleDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class DatabaseModule {

    @Singleton
    @Provides
    internal fun scheduleDatabase(application: Application, repositoryConfig: RepositoryConfig) =
        Room.databaseBuilder(application, ScheduleDatabase::class.java, repositoryConfig.databaseName).build()

    @Provides
    internal fun semesterDao(database: ScheduleDatabase) = database.semesterDao

    @Provides
    internal fun lessonDao(database: ScheduleDatabase) = database.lessonDao

    @Provides
    internal fun homeworkDao(database: ScheduleDatabase) = database.homeworkDao
}
