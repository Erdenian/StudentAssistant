package com.erdenian.studentassistant.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.erdenian.studentassistant.database.dao.HomeworkDao
import com.erdenian.studentassistant.database.dao.LessonDao
import com.erdenian.studentassistant.database.dao.SemesterDao
import com.erdenian.studentassistant.database.entity.ByDateEntity
import com.erdenian.studentassistant.database.entity.ByWeekdayEntity
import com.erdenian.studentassistant.database.entity.ClassroomEntity
import com.erdenian.studentassistant.database.entity.HomeworkEntity
import com.erdenian.studentassistant.database.entity.LessonEntity
import com.erdenian.studentassistant.database.entity.SemesterEntity
import com.erdenian.studentassistant.database.entity.TeacherEntity

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
