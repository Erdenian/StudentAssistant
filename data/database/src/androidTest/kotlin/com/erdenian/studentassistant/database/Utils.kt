package com.erdenian.studentassistant.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider

internal fun buildDatabase() =
    Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), ScheduleDatabase::class.java).build()
