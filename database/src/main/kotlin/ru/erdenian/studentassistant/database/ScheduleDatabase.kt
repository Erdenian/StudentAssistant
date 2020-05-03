package ru.erdenian.studentassistant.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.erdenian.studentassistant.database.dao.HomeworkDao
import ru.erdenian.studentassistant.database.dao.LessonDao
import ru.erdenian.studentassistant.database.dao.SemesterDao
import ru.erdenian.studentassistant.database.entity.ByDateEntity
import ru.erdenian.studentassistant.database.entity.ByWeekdayEntity
import ru.erdenian.studentassistant.database.entity.ClassroomEntity
import ru.erdenian.studentassistant.database.entity.HomeworkEntity
import ru.erdenian.studentassistant.database.entity.LessonEntity
import ru.erdenian.studentassistant.database.entity.SemesterEntity
import ru.erdenian.studentassistant.database.entity.TeacherEntity

@Database(
    entities = [
        SemesterEntity::class,
        LessonEntity::class,
        TeacherEntity::class,
        ClassroomEntity::class,
        ByWeekdayEntity::class,
        ByDateEntity::class,
        HomeworkEntity::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
internal abstract class ScheduleDatabase : RoomDatabase() {

    abstract val semesterDao: SemesterDao
    abstract val lessonDao: LessonDao
    abstract val homeworkDao: HomeworkDao
}
