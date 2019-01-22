package ru.erdenian.studentassistant.localdata.dao

import androidx.room.*
import org.joda.time.LocalDate
import org.joda.time.Period
import ru.erdenian.studentassistant.localdata.entity.LessonNew
import ru.erdenian.studentassistant.localdata.entity.SemesterNew
import ru.erdenian.studentassistant.schedule.LessonRepeat

@Dao
interface LessonDao {

    private companion object {
        const val DEFAULT_DURATION_MILLIS = 5_400_000
    }

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

    @Query("SELECT DISTINCT subject_name FROM lessons WHERE semester_id = :semesterId ORDER BY subject_name")
    fun getSubjects(semesterId: Long): List<String>

    @Query("SELECT DISTINCT type FROM lessons WHERE semester_id = :semesterId ORDER BY type")
    fun getTypes(semesterId: Long): List<String>

    @Query("SELECT DISTINCT teachers FROM lessons WHERE semester_id = :semesterId ORDER BY teachers")
    fun getTeachers(semesterId: Long): List<String>

    @Query("SELECT DISTINCT classrooms FROM lessons WHERE semester_id = :semesterId ORDER BY classrooms")
    fun getClassrooms(semesterId: Long): List<String>

    @Query("SELECT IFNULL((SELECT end_time - start_time as duration FROM lessons WHERE semester_id = :semesterId GROUP BY duration ORDER BY COUNT(duration) desc), $DEFAULT_DURATION_MILLIS)")
    fun getLessonLength(semesterId: Long): Period

    @Query("UPDATE lessons SET subject_name = :newName WHERE semester_id = :semesterId AND subject_name = :oldName")
    fun renameSubject(semesterId: Long, oldName: String, newName: String)

    @Delete
    fun delete(lesson: LessonNew)

    @Deprecated("Only for debugging")
    @Query("DELETE FROM lessons")
    fun deleteAll()

    @Deprecated("Only for debugging")
    @Query("DELETE FROM lessons WHERE semester_id = :semesterId")
    fun deleteAll(semesterId: Long)
}
