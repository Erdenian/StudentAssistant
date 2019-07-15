package ru.erdenian.studentassistant.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.erdenian.studentassistant.model.dao.HomeworkDao
import ru.erdenian.studentassistant.model.dao.LessonDao
import ru.erdenian.studentassistant.model.dao.SemesterDao
import ru.erdenian.studentassistant.model.entity.Homework
import ru.erdenian.studentassistant.model.entity.Lesson
import ru.erdenian.studentassistant.model.entity.Semester

@Database(
    entities = [
        Semester::class,
        Lesson::class,
        Homework::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ScheduleDatabase : RoomDatabase() {

    abstract val semesterDao: SemesterDao
    abstract val lessonDao: LessonDao
    abstract val homeworkDao: HomeworkDao

    companion object : SingletonHolder<ScheduleDatabase, Context>({ context ->
        Room.databaseBuilder(
            context.applicationContext,
            ScheduleDatabase::class.java,
            "schedule.db"
        ).build()
    })
}