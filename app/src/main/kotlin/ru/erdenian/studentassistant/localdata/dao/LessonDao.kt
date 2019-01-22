package ru.erdenian.studentassistant.localdata.dao

import androidx.room.*
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.localdata.entity.LessonNew
import ru.erdenian.studentassistant.localdata.entity.SemesterNew
import ru.erdenian.studentassistant.schedule.LessonRepeat

@Dao
interface LessonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(lesson: LessonNew)

    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId ORDER BY start_time, end_time, _id")
    fun get(semesterId: Long): List<LessonNew>

    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId AND _id = :lessonId")
    fun get(semesterId: Long, lessonId: Long): LessonNew?

    fun get(semester: SemesterNew, day: LocalDate): List<LessonNew> {
        val weekNumber = semester.getWeekNumber(day)
        return get(semester.id).filter { it.lessonRepeat.repeatsOnDay(day, weekNumber) }
    }

    fun get(semesterId: Long, weekday: Int): List<LessonNew> = get(semesterId).filter {
        (it.lessonRepeat as? LessonRepeat.ByWeekday)?.repeatsOnWeekday(weekday) ?: false
    }

    @Query("SELECT * FROM lessons WHERE semester_id = :semesterId AND subject_name = :subjectName ORDER BY start_time, end_time, _id")
    fun get(semesterId: Long, subjectName: String): List<LessonNew>

    @Delete
    fun delete(lesson: LessonNew)

    @Query("DELETE FROM lessons")
    fun deleteAll()

    @Query("DELETE FROM lessons WHERE semester_id = :semesterId")
    fun deleteAll(semesterId: Long)
}
