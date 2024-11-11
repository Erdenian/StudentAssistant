package com.erdenian.studentassistant.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.erdenian.studentassistant.repository.database.dao.HomeworkDao
import com.erdenian.studentassistant.repository.database.dao.LessonDao
import com.erdenian.studentassistant.repository.database.dao.SemesterDao
import com.erdenian.studentassistant.repository.database.entity.ByDateEntity
import com.erdenian.studentassistant.repository.database.entity.ByWeekdayEntity
import com.erdenian.studentassistant.repository.database.entity.ClassroomEntity
import com.erdenian.studentassistant.repository.database.entity.HomeworkEntity
import com.erdenian.studentassistant.repository.database.entity.LessonEntity
import com.erdenian.studentassistant.repository.database.entity.SemesterEntity
import com.erdenian.studentassistant.repository.database.entity.TeacherEntity

@Database(
    entities = [
        SemesterEntity::class,
        LessonEntity::class,
        TeacherEntity::class,
        ClassroomEntity::class,
        ByWeekdayEntity::class,
        ByDateEntity::class,
        HomeworkEntity::class,
    ],
    version = 1,
)
@TypeConverters(Converters::class)
internal abstract class ScheduleDatabase : RoomDatabase() {

    abstract val semesterDao: SemesterDao
    abstract val lessonDao: LessonDao
    abstract val homeworkDao: HomeworkDao
}
