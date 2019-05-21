package ru.erdenian.studentassistant.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.erdenian.studentassistant.repository.dao.HomeworkDao
import ru.erdenian.studentassistant.repository.dao.LessonDao
import ru.erdenian.studentassistant.repository.dao.SemesterDao
import ru.erdenian.studentassistant.repository.entity.HomeworkNew
import ru.erdenian.studentassistant.repository.entity.LessonNew
import ru.erdenian.studentassistant.repository.entity.SemesterNew

@Database(
    entities = [
        SemesterNew::class,
        LessonNew::class,
        HomeworkNew::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ScheduleDatabase : RoomDatabase() {

    abstract val semesterDao: SemesterDao
    abstract val lessonDao: LessonDao
    abstract val homeworkDao: HomeworkDao

    companion object : SingletonHolder<ScheduleDatabase, Context>({
        Room.databaseBuilder(
            it.applicationContext,
            ScheduleDatabase::class.java,
            "schedule1.db"
        )
            // Todo: убрать
            .fallbackToDestructiveMigration()
            .build()
    })
}
