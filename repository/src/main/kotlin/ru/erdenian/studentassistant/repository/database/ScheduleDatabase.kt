package ru.erdenian.studentassistant.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.erdenian.studentassistant.entity.Converters
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.repository.database.dao.HomeworkDao
import ru.erdenian.studentassistant.repository.database.dao.LessonDao
import ru.erdenian.studentassistant.repository.database.dao.SemesterDao

@Database(
    entities = [
        Semester::class,
        Lesson::class,
        Homework::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
internal abstract class ScheduleDatabase : RoomDatabase() {

    abstract val semesterDao: SemesterDao
    abstract val lessonDao: LessonDao
    abstract val homeworkDao: HomeworkDao
}
