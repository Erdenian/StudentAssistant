package com.erdenian.studentassistant.database.di

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.erdenian.studentassistant.database.ScheduleDatabase

internal fun buildDatabase() =
    Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), ScheduleDatabase::class.java).build()
