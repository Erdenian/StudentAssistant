package com.erdenian.studentassistant.database.di

import android.app.Application
import androidx.room.Room
import com.erdenian.studentassistant.database.ScheduleDatabase
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule(
    private val databaseName: String
) {

    // Avoid providing ScheduleDatabase through Dagger
    // to overcome 'Supertypes of the following classes cannot be resolved' problem
    private var scheduleDatabase: ScheduleDatabase? = null
    private fun getDatabase(application: Application): ScheduleDatabase =
        scheduleDatabase ?: synchronized(this) {
            scheduleDatabase ?: run {
                Room.databaseBuilder(application, ScheduleDatabase::class.java, databaseName)
                    .build()
                    .also { scheduleDatabase = it }
            }
        }

    @Provides
    fun semesterDao(application: Application) = getDatabase(application).semesterDao

    @Provides
    fun lessonDao(application: Application) = getDatabase(application).lessonDao

    @Provides
    fun homeworkDao(application: Application) = getDatabase(application).homeworkDao
}
