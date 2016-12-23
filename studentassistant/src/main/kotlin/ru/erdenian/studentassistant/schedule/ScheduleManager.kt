package ru.erdenian.studentassistant.schedule

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.common.base.Joiner
import com.google.common.collect.ImmutableSortedSet
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import java.util.*


object ScheduleManager {

    private lateinit var scheduleDBHelper: ScheduleDBHelper

    fun initialize(context: Context) {
        scheduleDBHelper = ScheduleDBHelper(context)
    }

    private val semestersCache: MutableMap<Long, Semester> by lazy {
        val semesters = TreeMap<Long, Semester>()

        scheduleDBHelper.readableDatabase.query(ScheduleDBHelper.Tables.TABLE_SEMESTERS, null,
                null, null, null, null, ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_FIRST_DAY).use {

            if (it.moveToFirst()) {
                val idColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_ID)
                val nameColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_NAME)
                val firstDayColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_FIRST_DAY)
                val lastDayColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_LAST_DAY)

                val dateFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.DATE_PATTERN)

                do {
                    val id = it.getLong(idColumnIndex)
                    semesters.put(id, Semester(it.getString(nameColumnIndex),
                            dateFormatter.parseLocalDate(it.getString(firstDayColumnIndex)),
                            dateFormatter.parseLocalDate(it.getString(lastDayColumnIndex)), id))
                } while (it.moveToNext())
            }
        }
        semesters
    }

    private var lessonsCache: MutableMap<Long, Lesson>? = null
        get() {
            if (field == null) {
                val selectedSemesterId = selectedSemesterId
                if (semestersCache.isNotEmpty() && (selectedSemesterId != null)) {
                    field = LinkedHashMap(readLessonsFromDb(selectedSemesterId).associateBy({ it.id }, { it }))
                }
            }
            return field
        }

    private var homeworksCache: MutableMap<Long, Homework>? = null
        get() {
            if (field == null) {
                val selectedSemesterId = selectedSemesterId
                if (semestersCache.isNotEmpty() && (selectedSemesterId != null)) {
                    field = LinkedHashMap(readHomeworksFromDb(selectedSemesterId).associateBy({ it.id }, { it }))
                }
            }
            return field
        }

    private var onScheduleUpdateListener: OnScheduleUpdateListener? = null
    fun setOnScheduleUpdateListener(value: OnScheduleUpdateListener?) {
        onScheduleUpdateListener = value
    }

    var selectedSemesterId: Long? = null
        get() {
            if ((field == null) && (semesters.isNotEmpty())) {
                field = semesters.last().id

                val today = LocalDate.now()
                for ((name, firstDay, lastDay, id) in semesters) {
                    if (!today.isBefore(firstDay) && !today.isAfter(lastDay)) {
                        field = id
                        break
                    }
                }
            }
            return field
        }
        set(value) {
            if (value != null) {
                for ((name, firstDay, lastDay, id) in semesters)
                    if (value == id) {
                        field = value
                    }
            } else {
                field = value
            }
            if (field == value) {
                lessonsCache = null
                homeworksCache = null
            } else throw IllegalArgumentException("Семестра с id $value нет")
        }

    val selectedSemesterIndex: Int?
        get() {
            val index = semesters.indexOfFirst { it.id == selectedSemesterId }
            if (index >= 0) return index
            else return null
        }

    val selectedSemester: Semester?
        get() = getSemester(selectedSemesterId!!)

    val semesters: ImmutableSortedSet<Semester>
        get() = ImmutableSortedSet.copyOf(semestersCache.values)

    fun getSemester(id: Long): Semester? = semestersCache[id]

    val semestersNames: List<String>
        get() = semesters.map { it.name }

    fun getSubjects(semesterId: Long): ImmutableSortedSet<String> =
            ImmutableSortedSet.copyOf(getLessons(semesterId).map { it.subjectName })

    val hasLessons: Boolean
        get() {
            semesters.forEach { if (getLessons(it.id).isNotEmpty()) return true }
            return false
        }

    @Synchronized private fun readLessonsFromDb(semesterId: Long): ImmutableSortedSet<Lesson> {
        val lessons = sortedSetOf<Lesson>()

        val dateFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.DATE_PATTERN)
        val timeFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.TIME_PATTERN)

        val db = scheduleDBHelper.readableDatabase

        db.query(ScheduleDBHelper.Tables.TABLE_LESSONS_PREFIX + semesterId, null, null, null, null, null, null).use {

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

                    val teachersTmp = it.getString(teachersColumnIndex)?.
                            split(ScheduleDBHelper.ARRAY_ITEMS_SEPARATOR)?.filter(String::isNotBlank)
                    val classroomsTmp = it.getString(classroomsColumnIndex)?.
                            split(ScheduleDBHelper.ARRAY_ITEMS_SEPARATOR)?.filter(String::isNotBlank)

                    val teachers = if (teachersTmp != null) ImmutableSortedSet.copyOf(teachersTmp)
                    else ImmutableSortedSet.of()

                    val classrooms = if (classroomsTmp != null) ImmutableSortedSet.copyOf(classroomsTmp)
                    else ImmutableSortedSet.of()

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

                                val weeks = it.getString(weeksColumnIndex).
                                        split(ScheduleDBHelper.ARRAY_ITEMS_SEPARATOR).map(String::toBoolean)

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

                    lessons.add(Lesson(it.getString(subjectNameColumnIndex), it.getString(typeColumnIndex) ?: "",
                            teachers, classrooms,
                            timeFormatter.parseLocalTime(it.getString(startTimeColumnIndex)),
                            timeFormatter.parseLocalTime(it.getString(endTimeColumnIndex)),
                            lessonRepeat, lessonId))

                } while (it.moveToNext())
            }
        }

        db.close()

        return ImmutableSortedSet.copyOf(lessons)
    }

    private fun readHomeworksFromDb(semesterId: Long): ImmutableSortedSet<Homework> {
        val homeworks = sortedSetOf<Homework>()

        val dateFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.DATE_PATTERN)

        scheduleDBHelper.readableDatabase.use {
            it.query(ScheduleDBHelper.Tables.TABLE_HOMEWORKS_PREFIX + semesterId, null, null, null, null, null, null).use {

                val subjectNameColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableHomeworks.COLUMN_HOMEWORK_SUBJECT_NAME)
                val descriptionColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableHomeworks.COLUMN_HOMEWORK_DESCRIPTION)
                val deadlineColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableHomeworks.COLUMN_HOMEWORK_DEADLINE)
                val idColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableHomeworks.COLUMN_HOMEWORK_ID)

                if (it.moveToFirst())
                    do {
                        homeworks += Homework(it.getString(subjectNameColumnIndex), it.getString(descriptionColumnIndex),
                                dateFormatter.parseLocalDate(it.getString(deadlineColumnIndex)), it.getLong(idColumnIndex))
                    } while (it.moveToNext())
            }
        }
        return ImmutableSortedSet.copyOf(homeworks)
    }

    fun getLessons(semesterId: Long): ImmutableSortedSet<Lesson> {
        val lessonsCache = lessonsCache

        if ((semesterId == selectedSemesterId) && (lessonsCache != null)) {
            return ImmutableSortedSet.copyOf(lessonsCache.map { it.value })
        } else {
            return readLessonsFromDb(semesterId)
        }
    }

    fun getLessons(semesterId: Long, day: LocalDate): ImmutableSortedSet<Lesson> =
            ImmutableSortedSet.copyOf(getLessons(semesterId).filter {
                it.lessonRepeat.repeatsOnDay(day, getSemester(semesterId)!!.getWeekNumber(day))
            })

    fun getLessons(semesterId: Long, weekday: Int): ImmutableSortedSet<Lesson> =
            ImmutableSortedSet.copyOf(getLessons(semesterId).filter {
                (it.lessonRepeat is LessonRepeat.ByWeekday) && it.lessonRepeat.repeatsOnWeekday(weekday)
            })

    fun getLesson(semesterId: Long, lessonId: Long): Lesson? {
        val lessonsCache = lessonsCache

        if ((semesterId == selectedSemesterId) && (lessonsCache != null)) {
            return lessonsCache[lessonId]
        }

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

                val teachersTmp = it.getString(teachersColumnIndex)?.
                        split(ScheduleDBHelper.ARRAY_ITEMS_SEPARATOR)?.filter(String::isNotBlank)
                val classroomsTmp = it.getString(classroomsColumnIndex)?.
                        split(ScheduleDBHelper.ARRAY_ITEMS_SEPARATOR)?.filter(String::isNotBlank)

                val teachers = if (teachersTmp != null) ImmutableSortedSet.copyOf(teachersTmp) else ImmutableSortedSet.of()
                val classrooms = if (classroomsTmp != null) ImmutableSortedSet.copyOf(classroomsTmp) else ImmutableSortedSet.of()

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

                            val weeks = it.getString(weeksColumnIndex).
                                    split(ScheduleDBHelper.ARRAY_ITEMS_SEPARATOR).map(String::toBoolean)

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

                lesson = Lesson(it.getString(subjectNameColumnIndex), it.getString(typeColumnIndex) ?: "",
                        teachers, classrooms,
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

        semestersCache.put(semesterId, semester.copy(id = semesterId))

        return semesterId
    }

    fun updateSemester(semester: Semester) {
        //Todo: код, создающий патчи

        val cv = ContentValues()
        val dateFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.DATE_PATTERN)

        cv.put(ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_NAME, semester.name)
        cv.put(ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_FIRST_DAY, dateFormatter.print(semester.firstDay))
        cv.put(ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_LAST_DAY, dateFormatter.print(semester.lastDay))

        scheduleDBHelper.writableDatabase.use {
            it.update(ScheduleDBHelper.Tables.TABLE_SEMESTERS, cv,
                    "${ScheduleDBHelper.TableSemesters.COLUMN_SEMESTER_ID} = ${semester.id}", null)
        }

        semestersCache.put(semester.id, semester)
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

        semestersCache.remove(id)
        if (selectedSemesterId == id) selectedSemesterId = null
    }

    fun addLesson(semesterId: Long, lesson: Lesson): Long {
        //Todo: код, создающий патчи

        val cv = ContentValues()
        val timeFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.TIME_PATTERN)
        val joiner = Joiner.on(ScheduleDBHelper.ARRAY_ITEMS_SEPARATOR)

        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_SUBJECT_NAME, lesson.subjectName)
        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_TYPE, if (lesson.type.isNotBlank()) lesson.type else null)
        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_TEACHERS,
                if (lesson.teachers.isNotEmpty()) joiner.join(lesson.teachers) else null)
        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_CLASSROOMS,
                if (lesson.classrooms.isNotEmpty()) joiner.join(lesson.classrooms) else null)
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

        if (semesterId == selectedSemesterId) lessonsCache!!.put(lessonId, lesson.copy(id = lessonId))

        return lessonId
    }

    fun updateLesson(semesterId: Long, lesson: Lesson) {
        //Todo: код, создающий патчи

        val oldLesson = getLesson(semesterId, lesson.id)!!

        val cv = ContentValues()
        val timeFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.TIME_PATTERN)
        val joiner = Joiner.on(ScheduleDBHelper.ARRAY_ITEMS_SEPARATOR)

        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_SUBJECT_NAME, lesson.subjectName)
        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_TYPE, if (lesson.type.isNotBlank()) lesson.type else null)
        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_TEACHERS,
                if (lesson.teachers.isNotEmpty()) joiner.join(lesson.teachers) else null)
        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_CLASSROOMS,
                if (lesson.classrooms.isNotEmpty()) joiner.join(lesson.classrooms) else null)
        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_START_TIME, timeFormatter.print(lesson.startTime))
        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_END_TIME, timeFormatter.print(lesson.endTime))

        cv.put(ScheduleDBHelper.TableLessons.COLUMN_LESSON_REPEAT_TYPE, when (lesson.lessonRepeat) {
            is LessonRepeat.ByWeekday -> ScheduleDBHelper.TableLessons.COLUMN_LESSON_REPEAT_TYPE_BY_WEEKDAY
            is LessonRepeat.ByDates -> ScheduleDBHelper.TableLessons.COLUMN_LESSON_REPEAT_TYPE_BY_DATES
            else -> throw IllegalArgumentException("Неизвестный тип повторений: ${lesson.lessonRepeat}")
        })

        scheduleDBHelper.writableDatabase.use {
            it.update(ScheduleDBHelper.Tables.TABLE_LESSONS_PREFIX + semesterId, cv,
                    "${ScheduleDBHelper.TableLessons.COLUMN_LESSON_ID} = ${lesson.id}", null)

            cv.clear()

            when (lesson.lessonRepeat) {
                is LessonRepeat.ByWeekday -> {
                    cv.put(ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_LESSON_ID, lesson.id)
                    cv.put(ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_WEEKDAY, lesson.lessonRepeat.weekday)
                    cv.put(ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_WEEKS, joiner.join(lesson.lessonRepeat.weeks))

                    it.update(ScheduleDBHelper.Tables.TABLE_BY_WEEKDAY_PREFIX + semesterId, cv,
                            "${ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_LESSON_ID} = ${lesson.id}", null)
                }
                is LessonRepeat.ByDates -> {
                    val dateFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.DATE_PATTERN)
                    for (date in lesson.lessonRepeat.dates) {
                        cv.put(ScheduleDBHelper.TableByDates.COLUMN_BY_DATES_LESSON_ID, lesson.id)
                        cv.put(ScheduleDBHelper.TableByDates.COLUMN_BY_DATES_DATE, dateFormatter.print(date))

                        it.update(ScheduleDBHelper.Tables.TABLE_BY_DATES_PREFIX + semesterId, cv,
                                "${ScheduleDBHelper.TableByDates.COLUMN_BY_DATES_LESSON_ID} = ${lesson.id}", null)
                    }
                }
                else -> throw IllegalArgumentException("Неизвестный тип повторений: ${lesson.lessonRepeat}")
            }
        }

        if (semesterId == selectedSemesterId) lessonsCache!!.put(lesson.id, lesson)

        if (getLessons(semesterId).filter { it.subjectName == oldLesson.subjectName }.isEmpty())
            getHomeworks(semesterId).filter { it.subjectName == oldLesson.subjectName }.forEach {
                updateHomework(semesterId, it.copy(subjectName = lesson.subjectName))
            }
    }

    fun removeLesson(semesterId: Long, lessonId: Long) {
        //Todo: код, создающий патчи

        val lesson = getLesson(semesterId, lessonId)!!

        scheduleDBHelper.writableDatabase.use {
            it.delete(ScheduleDBHelper.Tables.TABLE_LESSONS_PREFIX + semesterId,
                    "${ScheduleDBHelper.TableLessons.COLUMN_LESSON_ID} = $lessonId", null)

            it.delete(ScheduleDBHelper.Tables.TABLE_BY_WEEKDAY_PREFIX + semesterId,
                    "${ScheduleDBHelper.TableByWeekday.COLUMN_BY_WEEKDAY_LESSON_ID} = $lessonId", null)

            it.delete(ScheduleDBHelper.Tables.TABLE_BY_DATES_PREFIX + semesterId,
                    "${ScheduleDBHelper.TableByDates.COLUMN_BY_DATES_LESSON_ID} = $lessonId", null)
        }

        if (semesterId == selectedSemesterId) lessonsCache!!.remove(lessonId)

        if (getLessons(semesterId).filter { it.subjectName == lesson.subjectName }.isEmpty())
            getHomeworks(semesterId).filter { it.subjectName == lesson.subjectName }.forEach {
                removeHomework(semesterId, it.id)
            }
    }

    fun addHomework(semesterId: Long, homework: Homework): Long {
        //Todo: код, создающий патчи

        val cv = ContentValues()
        val dateFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.DATE_PATTERN)

        cv.put(ScheduleDBHelper.TableHomeworks.COLUMN_HOMEWORK_SUBJECT_NAME, homework.subjectName)
        cv.put(ScheduleDBHelper.TableHomeworks.COLUMN_HOMEWORK_DESCRIPTION, homework.description)
        cv.put(ScheduleDBHelper.TableHomeworks.COLUMN_HOMEWORK_DEADLINE, dateFormatter.print(homework.deadline))

        var homeworkId = homework.id

        scheduleDBHelper.writableDatabase.use {
            homeworkId = it.insert(ScheduleDBHelper.Tables.TABLE_HOMEWORKS_PREFIX + semesterId, null, cv)
        }

        if (semesterId == selectedSemesterId) homeworksCache!!.put(homeworkId, homework.copy(id = homeworkId))

        return homeworkId
    }

    fun updateHomework(semesterId: Long, homework: Homework) {
        //Todo: код, создающий патчи

        val cv = ContentValues()
        val dateFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.DATE_PATTERN)

        cv.put(ScheduleDBHelper.TableHomeworks.COLUMN_HOMEWORK_SUBJECT_NAME, homework.subjectName)
        cv.put(ScheduleDBHelper.TableHomeworks.COLUMN_HOMEWORK_DESCRIPTION, homework.description)
        cv.put(ScheduleDBHelper.TableHomeworks.COLUMN_HOMEWORK_DEADLINE, dateFormatter.print(homework.deadline))

        scheduleDBHelper.writableDatabase.use {
            it.update(ScheduleDBHelper.Tables.TABLE_HOMEWORKS_PREFIX + semesterId, cv,
                    "${ScheduleDBHelper.TableHomeworks.COLUMN_HOMEWORK_ID} = ${homework.id}", null)
        }

        if (semesterId == selectedSemesterId) homeworksCache!!.put(homework.id, homework)
    }

    fun removeHomework(semesterId: Long, homeworkId: Long) {
        //Todo: код, создающий патчи

        scheduleDBHelper.writableDatabase.use {
            it.delete(ScheduleDBHelper.Tables.TABLE_HOMEWORKS_PREFIX + semesterId,
                    "${ScheduleDBHelper.TableHomeworks.COLUMN_HOMEWORK_ID} = $homeworkId", null)
        }

        if (semesterId == selectedSemesterId) homeworksCache!!.remove(homeworkId)
    }

    fun getHomeworks(semesterId: Long): ImmutableSortedSet<Homework> {
        val homeworksCache = homeworksCache

        if ((semesterId == selectedSemesterId) && (homeworksCache != null)) {
            return ImmutableSortedSet.copyOf(homeworksCache.map { it.value })
        } else {
            return readHomeworksFromDb(semesterId)
        }
    }

    fun getHomeworks(semesterId: Long, lessonId: Long, date: LocalDate): ImmutableSortedSet<Homework> =
            ImmutableSortedSet.copyOf(getHomeworks(semesterId).filter {
                (it.subjectName == getLesson(semesterId, lessonId)!!.subjectName) && (it.deadline == date)
            })

    fun getActualHomeworks(semesterId: Long): ImmutableSortedSet<Homework> {
        val today = LocalDate.now()
        return ImmutableSortedSet.copyOf(getHomeworks(semesterId).filter { !it.deadline.isBefore(today) })
    }

    fun getActualHomeworks(semesterId: Long, lessonId: Long): ImmutableSortedSet<Homework> {
        val today = LocalDate.now()
        return ImmutableSortedSet.copyOf(getHomeworks(semesterId).filter {
            (it.subjectName == getLesson(semesterId, lessonId)!!.subjectName) && !it.deadline.isBefore(today)
        })
    }

    fun getPastHomeworks(semesterId: Long): ImmutableSortedSet<Homework> {
        val today = LocalDate.now()
        return ImmutableSortedSet.copyOf(getHomeworks(semesterId).filter { it.deadline.isBefore(today) })
    }

    fun getHomework(semesterId: Long, homeworkId: Long): Homework? {
        val homeworksCache = homeworksCache

        if ((semesterId == selectedSemesterId) && (homeworksCache != null)) {
            return homeworksCache[homeworkId]
        }

        var homework: Homework? = null

        val dateFormatter = DateTimeFormat.forPattern(ScheduleDBHelper.DATE_PATTERN)

        scheduleDBHelper.readableDatabase.use {
            it.query(ScheduleDBHelper.Tables.TABLE_HOMEWORKS_PREFIX + semesterId, null,
                    "${ScheduleDBHelper.TableHomeworks.COLUMN_HOMEWORK_ID} = $homeworkId", null, null, null, null).use {

                if (it.moveToFirst()) {
                    val subjectNameColumnIndex = it.getColumnIndexOrThrow(
                            ScheduleDBHelper.TableHomeworks.COLUMN_HOMEWORK_SUBJECT_NAME)
                    val descriptionColumnIndex = it.getColumnIndexOrThrow(
                            ScheduleDBHelper.TableHomeworks.COLUMN_HOMEWORK_DESCRIPTION)
                    val deadlineColumnIndex = it.getColumnIndexOrThrow(ScheduleDBHelper.TableHomeworks.COLUMN_HOMEWORK_DEADLINE)

                    homework = Homework(it.getString(subjectNameColumnIndex), it.getString(descriptionColumnIndex),
                            dateFormatter.parseLocalDate(it.getString(deadlineColumnIndex)), homeworkId)
                }
            }
        }
        return homework
    }

    class ScheduleDBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

        companion object {
            private const val DB_NAME = "schedule.db"
            private const val DB_VERSION = 1

            const val DATE_PATTERN = "yyyy.MM.dd"
            const val TIME_PATTERN = "HH:mm"
            const val ARRAY_ITEMS_SEPARATOR = ", "
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
            const val COLUMN_HOMEWORK_ID = "_id"
            const val COLUMN_HOMEWORK_SUBJECT_NAME = "subject_name"
            const val COLUMN_HOMEWORK_DESCRIPTION = "description"
            const val COLUMN_HOMEWORK_DEADLINE = "deadline"
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
                |                 ${TableHomeworks.COLUMN_HOMEWORK_ID}             INTEGER PRIMARY KEY AUTOINCREMENT,
                |                 ${TableHomeworks.COLUMN_HOMEWORK_SUBJECT_NAME}   TEXT NOT NULL,
                |                 ${TableHomeworks.COLUMN_HOMEWORK_DESCRIPTION}    TEXT NOT NULL,
                |                 ${TableHomeworks.COLUMN_HOMEWORK_DEADLINE}       TEXT NOT NULL);
                """.trimMargin()

            fun renameTableLessons(oldSemesterId: Long, newSemesterId: Long) =
                    "ALTER TABLE ${Tables.TABLE_LESSONS_PREFIX + oldSemesterId} " +
                            "RENAME TO ${Tables.TABLE_LESSONS_PREFIX + newSemesterId};"

            fun renameTableByWeekday(oldSemesterId: Long, newSemesterId: Long) =
                    "ALTER TABLE ${Tables.TABLE_BY_WEEKDAY_PREFIX + oldSemesterId} " +
                            "RENAME TO ${Tables.TABLE_BY_WEEKDAY_PREFIX + newSemesterId};"

            fun renameTableByDates(oldSemesterId: Long, newSemesterId: Long) =
                    "ALTER TABLE ${Tables.TABLE_BY_DATES_PREFIX + oldSemesterId} " +
                            "RENAME TO ${Tables.TABLE_BY_DATES_PREFIX + newSemesterId};"

            fun renameTableHomeworks(oldSemesterId: Long, newSemesterId: Long) =
                    "ALTER TABLE ${Tables.TABLE_HOMEWORKS_PREFIX + oldSemesterId} " +
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