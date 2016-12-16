package ru.erdenian.studentassistant.schedule

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.common.base.Joiner
import com.google.common.collect.ImmutableSortedSet
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat


object ScheduleManager {

    private lateinit var scheduleDBHelper: ScheduleDBHelper

    fun initialize(context: Context) {
        scheduleDBHelper = ScheduleDBHelper(context)
    }

    private var onScheduleUpdateListener: OnScheduleUpdateListener? = null
    fun setOnScheduleUpdateListener(value: OnScheduleUpdateListener?) {
        onScheduleUpdateListener = value
    }

    /*var selectedSemesterId1: Long? = null
        get() {
            if (field == null) {
                if (semesters.isNotEmpty()) {
                    field = semesters.last().id

                    val today = LocalDate.now()
                    for ((name, firstDay, lastDay, id) in semesters) {
                        if (!today.isBefore(firstDay) && !today.isAfter(lastDay)) {
                            field = id
                            break
                        }
                    }
                }
            }
            return field
        }
        set(value) {
            for ((name, firstDay, lastDay, id) in semesters)
                if (value == id) {
                    field = value
                    return
                }
            throw IllegalArgumentException("Семестра с id $value нет")
        }*/

    //val selectedSemester1: Semester? = getSemester(selectedSemesterId1!!)

    val semesters: ImmutableSortedSet<Semester>
        get() {
            val semesters = sortedSetOf<Semester>()

            scheduleDBHelper.readableDatabase.query(ScheduleDBHelper.Tables.TABLE_SEMESTERS, null,
                    null, null, null, null, ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_FIRST_DAY).use {

                if (it.moveToFirst()) {
                    val idColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_ID)
                    val nameColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_NAME)
                    val firstDayColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_FIRST_DAY)
                    val lastDayColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_LAST_DAY)

                    val dateFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.DATE_PATTERN)

                    do {
                        semesters.add(Semester(it.getString(nameColumnIndex), dateFormatter.parseLocalDate(it.getString(firstDayColumnIndex)),
                                dateFormatter.parseLocalDate(it.getString(lastDayColumnIndex)), it.getLong(idColumnIndex)))
                    } while (it.moveToNext())
                }
            }

            return ImmutableSortedSet.copyOf(semesters)
        }

    fun getSemester(id: Long): Semester? {
        scheduleDBHelper.readableDatabase.query(ScheduleDBHelper.Tables.TABLE_SEMESTERS, null,
                "${ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_ID} = $id", null, null, null, null).use {

            if (it.moveToFirst()) {
                val nameColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_NAME)
                val firstDayColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_FIRST_DAY)
                val lastDayColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_LAST_DAY)

                val dateFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.DATE_PATTERN)

                return Semester(it.getString(nameColumnIndex), dateFormatter.parseLocalDate(it.getString(firstDayColumnIndex)),
                        dateFormatter.parseLocalDate(it.getString(lastDayColumnIndex)), id)
            } else return null
        }
    }

    val semestersNames: List<String>
        get() = semesters.map { it.name }

    fun getLessons(semesterId: Long): ImmutableSortedSet<Lesson> {
        val lessons = sortedSetOf<Lesson>()

        val dateFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.DATE_PATTERN)
        val timeFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.TIME_PATTERN)

        val db = scheduleDBHelper.readableDatabase

        db.query(ScheduleDBHelper.Tables.TABLE_LESSONS_PREFIX + semesterId, null,
                null, null, null, null, null).use {

            if (it.moveToFirst()) {
                val idColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableLessons.COLUMN_LESSON_ID)
                val subjectNameColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableLessons.COLUMN_LESSON_SUBJECT_NAME)
                val typeColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableLessons.COLUMN_LESSON_TYPE)
                val teachersColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableLessons.COLUMN_LESSON_TEACHERS)
                val classroomsColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableLessons.COLUMN_LESSON_CLASSROOMS)
                val startTimeColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableLessons.COLUMN_LESSON_START_TIME)
                val endTimeColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableLessons.COLUMN_LESSON_END_TIME)
                val repeatTypeColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableLessons.COLUMN_LESSON_REPEAT_TYPE)

                do {
                    val lessonId = it.getLong(idColumnIndex)

                    val lessonRepeat = when (it.getInt(repeatTypeColumnIndex)) {
                        ScheduleDBHelper.TableLessons.COLUMN_LESSON_REPEAT_TYPE_BY_WEEKDAY -> {
                            db.query(ScheduleDBHelper.Tables.TABLE_BY_WEEKDAY_PREFIX + semesterId,
                                    arrayOf(ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_WEEKDAY,
                                            ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_WEEKS),
                                    "${ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_LESSON_ID} = $lessonId",
                                    null, null, null, null).use {

                                val weekdayColumnIndex =
                                        it.getColumnIndexOrThrow(ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_WEEKDAY)
                                val weeksColumnIndex =
                                        it.getColumnIndexOrThrow(ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_WEEKS)

                                it.moveToFirst()

                                val weeks = it.getString(weeksColumnIndex).split(", ").map { it.toBoolean() }

                                LessonRepeat.ByWeekday(it.getInt(weekdayColumnIndex), weeks)
                            }
                        }
                        ScheduleDBHelper.TableLessons.COLUMN_LESSON_REPEAT_TYPE_BY_DATES -> {
                            db.query(ScheduleDBHelper.Tables.TABLE_BY_DATES_PREFIX + semesterId,
                                    arrayOf(ScheduleDBHelper.TableByDates.COLUMN_BY_DATES_DATE),
                                    "${ScheduleDBHelper.TableByDates.COLUMN_BY_DATES_LESSON_ID} = $lessonId",
                                    null, null, null, null).use {

                                val dates = mutableListOf<LocalDate>()

                                if (it.moveToFirst()) {
                                    val datesColumnIndex = it.getColumnIndex(ScheduleDBHelper.TableByDates.COLUMN_BY_DATES_DATE)
                                    do {
                                        dates.add(dateFormatter.parseLocalDate(it.getString(datesColumnIndex)))
                                        dates.add(dateFormatter.parseLocalDate(it.getString(datesColumnIndex)))
                                    } while (it.moveToNext())
                                }

                                LessonRepeat.ByDates(ImmutableSortedSet.copyOf(dates))
                            }
                        }
                        else -> throw IllegalStateException("Неизвестный тип повторений: ${it.getInt(repeatTypeColumnIndex)}")
                    }

                    lessons.add(Lesson(it.getString(subjectNameColumnIndex), it.getString(typeColumnIndex),
                            ImmutableSortedSet.copyOf(it.getString(teachersColumnIndex).split(", ")),
                            ImmutableSortedSet.copyOf(it.getString(classroomsColumnIndex).split(", ")),
                            timeFormatter.parseLocalTime(it.getString(startTimeColumnIndex)),
                            timeFormatter.parseLocalTime(it.getString(endTimeColumnIndex)),
                            lessonRepeat, lessonId))

                } while (it.moveToNext())
            }
        }

        db.close()

        return ImmutableSortedSet.copyOf(lessons)
    }

    fun getLessons(semesterId: Long, day: LocalDate): ImmutableSortedSet<Lesson> {
        val semester = getSemester(semesterId)
        val weekNumber = Days.daysBetween(semester!!.firstDay.minusDays(semester.firstDay.dayOfWeek - 1), day).days / 7

        return ImmutableSortedSet.copyOf(getLessons(semesterId).filter { it.lessonRepeat.repeatsOnDay(day, weekNumber) })
    }

    fun getLessons(semesterId: Long, weekday: Int): ImmutableSortedSet<Lesson> {
        return ImmutableSortedSet.copyOf(getLessons(semesterId).filter {
            (it.lessonRepeat is LessonRepeat.ByWeekday) && it.lessonRepeat.repeatsOnWeekday(weekday)
        })
    }

    fun getLesson(semesterId: Long, lessonId: Long): Lesson? {
        var lesson: Lesson? = null

        val dateFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.DATE_PATTERN)
        val timeFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.TIME_PATTERN)

        val db = scheduleDBHelper.readableDatabase

        db.query(ScheduleDBHelper.Tables.TABLE_LESSONS_PREFIX + semesterId, null,
                "${ScheduleDBHelper.TableLessons.COLUMN_LESSON_ID} = $lessonId", null, null, null, null).use {

            if (it.moveToFirst()) {
                val subjectNameColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableLessons.COLUMN_LESSON_SUBJECT_NAME)
                val typeColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableLessons.COLUMN_LESSON_TYPE)
                val teachersColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableLessons.COLUMN_LESSON_TEACHERS)
                val classroomsColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableLessons.COLUMN_LESSON_CLASSROOMS)
                val startTimeColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableLessons.COLUMN_LESSON_START_TIME)
                val endTimeColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableLessons.COLUMN_LESSON_END_TIME)
                val repeatTypeColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableLessons.COLUMN_LESSON_REPEAT_TYPE)


                val lessonRepeat = when (it.getInt(repeatTypeColumnIndex)) {
                    ScheduleDBHelper.TableLessons.COLUMN_LESSON_REPEAT_TYPE_BY_WEEKDAY -> {
                        db.query(ScheduleDBHelper.Tables.TABLE_BY_WEEKDAY_PREFIX + semesterId,
                                arrayOf(ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_WEEKDAY,
                                        ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_WEEKDAY),
                                "${ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_LESSON_ID} = $lessonId",
                                null, null, null, null).use {

                            val weekdayColumnIndex =
                                    it.getColumnIndexOrThrow(ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_WEEKDAY)
                            val weeksColumnIndex =
                                    it.getColumnIndexOrThrow(ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_WEEKS)

                            val weeks = it.getString(weeksColumnIndex).split(", ").map { it.toBoolean() }

                            LessonRepeat.ByWeekday(it.getInt(weekdayColumnIndex), weeks)
                        }
                    }
                    ScheduleDBHelper.TableLessons.COLUMN_LESSON_REPEAT_TYPE_BY_DATES -> {
                        db.query(ScheduleDBHelper.Tables.TABLE_BY_DATES_PREFIX + semesterId,
                                arrayOf(ScheduleDBHelper.TableByDates.COLUMN_BY_DATES_DATE),
                                "${ScheduleDBHelper.TableByDates.COLUMN_BY_DATES_LESSON_ID} = $lessonId",
                                null, null, null, null).use {

                            val dates = mutableListOf<LocalDate>()

                            if (it.moveToFirst()) {
                                val datesColumnIndex = it.getColumnIndex(ScheduleDBHelper.TableByDates.COLUMN_BY_DATES_DATE)
                                do {
                                    dates.add(dateFormatter.parseLocalDate(it.getString(datesColumnIndex)))
                                } while (it.moveToNext())
                            }

                            LessonRepeat.ByDates(ImmutableSortedSet.copyOf(dates))
                        }
                    }
                    else -> throw IllegalStateException("Неизвестный тип повторений: ${it.getInt(repeatTypeColumnIndex)}")
                }

                lesson = Lesson(it.getString(subjectNameColumnIndex), it.getString(typeColumnIndex),
                        ImmutableSortedSet.copyOf(it.getString(teachersColumnIndex).split(", ")),
                        ImmutableSortedSet.copyOf(it.getString(classroomsColumnIndex).split(", ")),
                        timeFormatter.parseLocalTime(it.getString(startTimeColumnIndex)),
                        timeFormatter.parseLocalTime(it.getString(endTimeColumnIndex)),
                        lessonRepeat, lessonId)
            }
        }

        db.close()

        return lesson
    }

    fun addSemester(semester: Semester): Long {
        //Todo: код, создающий патчи

        val cv = ContentValues()
        val dateFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.DATE_PATTERN)

        cv.put(ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_NAME, semester.name)
        cv.put(ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_FIRST_DAY, dateFormatter.print(semester.firstDay))
        cv.put(ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_LAST_DAY, dateFormatter.print(semester.lastDay))

        var semesterId = semester.id

        scheduleDBHelper.writableDatabase.use {
            semesterId = it.insert(ScheduleDBHelper.Tables.TABLE_SEMESTERS, null, cv)

            it.execSQL(ScheduleDBHelper.Queries.createTableLessons(semesterId))
            it.execSQL(ScheduleDBHelper.Queries.createTableByWeekday(semesterId))
            it.execSQL(ScheduleDBHelper.Queries.createTableByDates(semesterId))
            it.execSQL(ScheduleDBHelper.Queries.createTableHomeworks(semesterId))
        }

        return semesterId
    }

    fun updateSemester(semester: Semester) {
        //Todo: код, создающий патчи

        val oldSemesterId = getSemester(semester.id)!!.id

        val cv = ContentValues()
        val dateFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.DATE_PATTERN)

        cv.put(ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_NAME, semester.name)
        cv.put(ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_FIRST_DAY, dateFormatter.print(semester.firstDay))
        cv.put(ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_LAST_DAY, dateFormatter.print(semester.lastDay))

        scheduleDBHelper.writableDatabase.use {
            it.update(ScheduleDBHelper.Tables.TABLE_SEMESTERS, cv,
                    "${ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_ID} = ${semester.id}", null)

            it.execSQL(ScheduleDBHelper.Queries.renameTableLessons(oldSemesterId, semester.id))
            it.execSQL(ScheduleDBHelper.Queries.renameTableByWeekday(oldSemesterId, semester.id))
            it.execSQL(ScheduleDBHelper.Queries.renameTableByDates(oldSemesterId, semester.id))
            it.execSQL(ScheduleDBHelper.Queries.renameTableHomeworks(oldSemesterId, semester.id))
        }
    }

    fun removeSemester(id: Long) {
        //Todo: код, создающий патчи

        scheduleDBHelper.writableDatabase.use {
            it.delete(ScheduleDBHelper.Tables.TABLE_SEMESTERS, "${ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_ID} = $id", null)

            it.execSQL(ScheduleDBHelper.Queries.deleteTableLessons(id))
            it.execSQL(ScheduleDBHelper.Queries.deleteTableByWeekday(id))
            it.execSQL(ScheduleDBHelper.Queries.deleteTableByDates(id))
            it.execSQL(ScheduleDBHelper.Queries.deleteTableHomeworks(id))
        }
    }

    fun addLesson(semesterId: Long, lesson: Lesson): Long {
        //Todo: код, создающий патчи

        val cv = ContentValues()
        val timeFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.TIME_PATTERN)
        val joiner = Joiner.on(", ")

        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_SUBJECT_NAME, lesson.subjectName)
        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_TYPE, lesson.type)
        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_TEACHERS, joiner.join(lesson.teachers))
        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_CLASSROOMS, joiner.join(lesson.classrooms))
        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_START_TIME, timeFormatter.print(lesson.startTime))
        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_END_TIME, timeFormatter.print(lesson.endTime))

        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_REPEAT_TYPE, when (lesson.lessonRepeat) {
            is LessonRepeat.ByWeekday -> ScheduleDBHelper.TableLessons.COLUMN_LESSON_REPEAT_TYPE_BY_WEEKDAY
            is LessonRepeat.ByDates -> ScheduleDBHelper.TableLessons.COLUMN_LESSON_REPEAT_TYPE_BY_DATES
            else -> throw IllegalArgumentException("Неизвестный тип повторений: ${lesson.lessonRepeat}")
        })

        var lessonId = lesson.id

        scheduleDBHelper.writableDatabase.use {
            lessonId = it.insert(ScheduleDBHelper.Tables.TABLE_LESSONS_PREFIX + semesterId, null, cv)

            cv.clear()

            when (lesson.lessonRepeat) {
                is LessonRepeat.ByWeekday -> {
                    cv.put(ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_LESSON_ID, lessonId)
                    cv.put(ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_WEEKDAY, lesson.lessonRepeat.weekday)
                    cv.put(ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_WEEKS, joiner.join(lesson.lessonRepeat.weeks))

                    it.insert(ScheduleDBHelper.Tables.TABLE_BY_WEEKDAY_PREFIX + semesterId, null, cv)
                }
                is LessonRepeat.ByDates -> {
                    val dateFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.DATE_PATTERN)
                    for (date in lesson.lessonRepeat.dates) {
                        cv.put(ScheduleDBHelper.TableByDates.COLUMN_BY_DATES_LESSON_ID, lessonId)
                        cv.put(ScheduleDBHelper.TableByDates.COLUMN_BY_DATES_DATE, dateFormatter.print(date))

                        it.insert(ScheduleDBHelper.Tables.TABLE_BY_DATES_PREFIX + semesterId, null, cv)
                    }
                }
                else -> throw IllegalArgumentException("Неизвестный тип повторений: ${lesson.lessonRepeat}")
            }
        }

        return lessonId
    }

    fun updateLesson(semesterId: Long, lesson: Lesson) {
        //Todo: код, создающий патчи

        val cv = ContentValues()
        val timeFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.TIME_PATTERN)
        val joiner = Joiner.on(", ")

        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_SUBJECT_NAME, lesson.subjectName)
        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_TYPE, lesson.type)
        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_TEACHERS, joiner.join(lesson.teachers))
        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_CLASSROOMS, joiner.join(lesson.classrooms))
        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_START_TIME, timeFormatter.print(lesson.startTime))
        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_END_TIME, timeFormatter.print(lesson.endTime))

        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_REPEAT_TYPE, when (lesson.lessonRepeat) {
            is LessonRepeat.ByWeekday -> ScheduleDBHelper.TableLessons.COLUMN_LESSON_REPEAT_TYPE_BY_WEEKDAY
            is LessonRepeat.ByDates -> ScheduleDBHelper.TableLessons.COLUMN_LESSON_REPEAT_TYPE_BY_DATES
            else -> throw IllegalArgumentException("Неизвестный тип повторений: ${lesson.lessonRepeat}")
        })

        scheduleDBHelper.writableDatabase.use {
            val lessonId = it.update(ScheduleDBHelper.Tables.TABLE_LESSONS_PREFIX + semesterId, cv,
                    "${ScheduleDBHelper.TableLessons.COLUMN_LESSON_ID} = ${lesson.id}", null)

            cv.clear()

            when (lesson.lessonRepeat) {
                is LessonRepeat.ByWeekday -> {
                    cv.put(ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_LESSON_ID, lessonId)
                    cv.put(ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_WEEKDAY, lesson.lessonRepeat.weekday)
                    cv.put(ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_WEEKS, joiner.join(lesson.lessonRepeat.weeks))

                    it.update(ScheduleDBHelper.Tables.TABLE_BY_WEEKDAY_PREFIX + semesterId, cv,
                            "${ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_LESSON_ID} = ${lesson.id}", null)
                }
                is LessonRepeat.ByDates -> {
                    val dateFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.DATE_PATTERN)
                    for (date in lesson.lessonRepeat.dates) {
                        cv.put(ScheduleDBHelper.TableByDates.COLUMN_BY_DATES_LESSON_ID, lessonId)
                        cv.put(ScheduleDBHelper.TableByDates.COLUMN_BY_DATES_DATE, dateFormatter.print(date))

                        it.update(ScheduleDBHelper.Tables.TABLE_BY_DATES_PREFIX + semesterId, cv,
                                "${ScheduleDBHelper.TableByDates.COLUMN_BY_DATES_LESSON_ID} = ${lesson.id}", null)
                    }
                }
                else -> throw IllegalArgumentException("Неизвестный тип повторений: ${lesson.lessonRepeat}")
            }
        }
    }

    fun removeLesson(semesterId: Long, lessonId: Long) {
        //Todo: код, создающий патчи

        scheduleDBHelper.writableDatabase.use {
            it.delete(ScheduleDBHelper.Tables.TABLE_LESSONS_PREFIX + semesterId,
                    "${ScheduleDBHelper.TableLessons.COLUMN_LESSON_ID} = $lessonId", null)

            it.delete(ScheduleDBHelper.Tables.TABLE_BY_WEEKDAY_PREFIX + semesterId,
                    "${ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_LESSON_ID} = $lessonId", null)

            it.delete(ScheduleDBHelper.Tables.TABLE_BY_DATES_PREFIX + semesterId,
                    "${ScheduleDBHelper.TableByDates.COLUMN_BY_DATES_LESSON_ID} = $lessonId", null)
        }
    }

    class ScheduleDBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

        companion object {
            private const val DB_NAME = "schedule.db"
            private const val DB_VERSION = 1

            const val DATE_PATTERN = "yyyy.MM.dd"
            const val TIME_PATTERN = "HH:mm"
        }

        object Tables {
            const val TABLE_SEMESTERS = "semesters"
            const val TABLE_LESSONS_PREFIX = "lessons_"
            const val TABLE_BY_WEEKDAY_PREFIX = "by_weekday_"
            const val TABLE_BY_DATES_PREFIX = "by_dates_"
            const val TABLE_HOMEWORKS_PREFIX = "homeworks_"
        }

        object TableSemesters {
            const val COLUMN_SEMESTER_ID = "_id"
            const val COLUMN_SEMESTER_NAME = "name"
            const val COLUMN_SEMESTER_FIRST_DAY = "first_day"
            const val COLUMN_SEMESTER_LAST_DAY = "last_day"
        }

        object TableLessons {
            const val COLUMN_LESSON_ID = "_id"
            const val COLUMN_LESSON_SUBJECT_NAME = "subject_name"
            const val COLUMN_LESSON_TYPE = "type"
            const val COLUMN_LESSON_TEACHERS = "teachers"
            const val COLUMN_LESSON_CLASSROOMS = "classrooms"
            const val COLUMN_LESSON_START_TIME = "start_time"
            const val COLUMN_LESSON_END_TIME = "end_time"
            const val COLUMN_LESSON_REPEAT_TYPE = "repeat_type"
            const val COLUMN_LESSON_REPEAT_TYPE_BY_WEEKDAY = 1
            const val COLUMN_LESSON_REPEAT_TYPE_BY_DATES = 2
        }

        object TableByWeekday {
            const val COLUMN_BY_WEEKDAY_ID = "_id"
            const val COLUMN_BY_WEEKDAY_LESSON_ID = "lesson_id"
            const val COLUMN_BY_WEEKDAY_WEEKDAY = "weekday"
            const val COLUMN_BY_WEEKDAY_WEEKS = "weeks"
        }

        object TableByDates {
            const val COLUMN_BY_DATES_ID = "_id"
            const val COLUMN_BY_DATES_LESSON_ID = "lesson_id"
            const val COLUMN_BY_DATES_DATE = "date"
        }

        object TableHomeworks {
            const val COLUMN_HOMEWORKS_ID = "_id"
            const val COLUMN_HOMEWORKS_SUBJECT_NAME = "subject_name"
            const val COLUMN_HOMEWORKS_DESCRIPTION = "description"
            const val COLUMN_HOMEWORKS_DEADLINE = "deadline"
        }

        object Queries {

            fun createTableSemesters() = """
                |CREATE TABLE ${Tables.TABLE_SEMESTERS} (
                |               ${TableSemesters.COLUMN_SEMESTER_ID}        INTEGER PRIMARY KEY AUTOINCREMENT,
                |               ${TableSemesters.COLUMN_SEMESTER_NAME}      TEXT NOT NULL,
                |               ${TableSemesters.COLUMN_SEMESTER_FIRST_DAY} TEXT NOT NULL,
                |               ${TableSemesters.COLUMN_SEMESTER_LAST_DAY}  TEXT NOT NULL);
                """.trimMargin()

            fun createTableLessons(semesterId: Long) = """
                |CREATE TABLE ${Tables.TABLE_LESSONS_PREFIX + semesterId} (
                |               ${TableLessons.COLUMN_LESSON_ID}            INTEGER PRIMARY KEY AUTOINCREMENT,
                |               ${TableLessons.COLUMN_LESSON_SUBJECT_NAME}  TEXT NOT NULL,
                |               ${TableLessons.COLUMN_LESSON_TYPE}          TEXT,
                |               ${TableLessons.COLUMN_LESSON_TEACHERS}      TEXT,
                |               ${TableLessons.COLUMN_LESSON_CLASSROOMS}    TEXT,
                |               ${TableLessons.COLUMN_LESSON_START_TIME}    TEXT NOT NULL,
                |               ${TableLessons.COLUMN_LESSON_END_TIME}      TEXT NOT NULL,
                |               ${TableLessons.COLUMN_LESSON_REPEAT_TYPE}   INTEGER NOT NULL);
                """.trimMargin()

            fun createTableByWeekday(semesterId: Long) = """
                |CREATE TABLE ${Tables.TABLE_BY_WEEKDAY_PREFIX + semesterId} (
                |                 ${TableByWeekday.COLUMN_BY_WEEKDAY_ID}        INTEGER PRIMARY KEY AUTOINCREMENT,
                |                 ${TableByWeekday.COLUMN_BY_WEEKDAY_LESSON_ID} INTEGER NOT NULL,
                |                 ${TableByWeekday.COLUMN_BY_WEEKDAY_WEEKDAY}   INTEGER NOT NULL,
                |                 ${TableByWeekday.COLUMN_BY_WEEKDAY_WEEKS}     TEXT NOT NULL);
                """.trimMargin()

            fun createTableByDates(semesterId: Long) = """
                |CREATE TABLE ${Tables.TABLE_BY_DATES_PREFIX + semesterId} (
                |                 ${TableByDates.COLUMN_BY_DATES_ID}        INTEGER PRIMARY KEY AUTOINCREMENT,
                |                 ${TableByDates.COLUMN_BY_DATES_LESSON_ID} INTEGER NOT NULL,
                |                 ${TableByDates.COLUMN_BY_DATES_DATE}      TEXT NOT NULL);
                """.trimMargin()

            fun createTableHomeworks(semesterId: Long) = """
                |CREATE TABLE ${Tables.TABLE_HOMEWORKS_PREFIX + semesterId} (
                |                 ${TableHomeworks.COLUMN_HOMEWORKS_ID}             INTEGER PRIMARY KEY AUTOINCREMENT,
                |                 ${TableHomeworks.COLUMN_HOMEWORKS_SUBJECT_NAME}   TEXT NOT NULL,
                |                 ${TableHomeworks.COLUMN_HOMEWORKS_DESCRIPTION}    TEXT NOT NULL,
                |                 ${TableHomeworks.COLUMN_HOMEWORKS_DEADLINE}       TEXT NOT NULL);
                """.trimMargin()

            fun renameTableLessons(oldSemesterId: Long, newSemesterId: Long) =
                    "ALTER TABLE ${Tables.TABLE_LESSONS_PREFIX + oldSemesterId}" +
                            "RENAME TO ${Tables.TABLE_LESSONS_PREFIX + newSemesterId};"

            fun renameTableByWeekday(oldSemesterId: Long, newSemesterId: Long) =
                    "ALTER TABLE ${Tables.TABLE_BY_WEEKDAY_PREFIX + oldSemesterId}" +
                            "RENAME TO ${Tables.TABLE_BY_WEEKDAY_PREFIX + newSemesterId};"

            fun renameTableByDates(oldSemesterId: Long, newSemesterId: Long) =
                    "ALTER TABLE ${Tables.TABLE_BY_DATES_PREFIX + oldSemesterId}" +
                            "RENAME TO ${Tables.TABLE_BY_DATES_PREFIX + newSemesterId};"

            fun renameTableHomeworks(oldSemesterId: Long, newSemesterId: Long) =
                    "ALTER TABLE ${Tables.TABLE_HOMEWORKS_PREFIX + oldSemesterId}" +
                            "RENAME TO ${Tables.TABLE_HOMEWORKS_PREFIX + newSemesterId};"

            fun deleteTableLessons(semesterId: Long) = "DROP TABLE IF EXISTS ${Tables.TABLE_LESSONS_PREFIX + semesterId};"

            fun deleteTableByWeekday(semesterId: Long) = "DROP TABLE IF EXISTS ${Tables.TABLE_BY_WEEKDAY_PREFIX + semesterId};"

            fun deleteTableByDates(semesterId: Long) = "DROP TABLE IF EXISTS ${Tables.TABLE_BY_DATES_PREFIX + semesterId};"

            fun deleteTableHomeworks(semesterId: Long) = "DROP TABLE IF EXISTS ${Tables.TABLE_HOMEWORKS_PREFIX + semesterId};"

        }

        override fun onCreate(db: SQLiteDatabase) = db.execSQL(Queries.createTableSemesters())

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) =
                throw UnsupportedOperationException("not implemented")
    }
}