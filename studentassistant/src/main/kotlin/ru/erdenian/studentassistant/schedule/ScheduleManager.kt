package ru.erdenian.studentassistant.schedule

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.fatboyindustrial.gsonjodatime.Converters
import com.google.common.base.Joiner
import com.google.common.collect.ImmutableSortedSet
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.jetbrains.anko.defaultSharedPreferences
import org.joda.time.LocalDate
import org.joda.time.Period
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import ru.erdenian.studentassistant.netty.nettyQuery
import java.lang.ref.WeakReference


object ScheduleManager {

  var semesterToSyncId: Long = -1
  lateinit var context: Context

  private lateinit var dbHelper: ScheduleDBHelper

  fun initialize(context: Context) {
    dbHelper = ScheduleDBHelper(context)
    this.context = context
    semesterToSyncId = context.defaultSharedPreferences.getLong("semester_to_sync_id", -1)
  }


  //region Кэш

  private val semestersCache: MutableMap<Long, Semester> by lazy {
    val semesters = mutableMapOf<Long, Semester>()
    dbHelper.getSemesters { semesters.put(it.id, it) }
    semesters
  }

  private var lessonsCache: MutableMap<Long, Lesson>? = null
    get() {
      if (field == null) {
        val selectedSemesterId = selectedSemesterId ?: return null
        if (semestersCache.isNotEmpty()) {
          val lessons = mutableMapOf<Long, Lesson>()
          dbHelper.getLessons(selectedSemesterId) { lessons.put(it.id, it) }
          field = lessons
        }
      }
      return field
    }

  private var homeworksCache: MutableMap<Long, Homework>? = null
    get() {
      if (field == null) {
        val selectedSemesterId = selectedSemesterId ?: return null
        if (semestersCache.isNotEmpty()) {
          val homeworks = mutableMapOf<Long, Homework>()
          dbHelper.getHomeworks(selectedSemesterId) { homeworks.put(it.id, it) }
          field = homeworks
        }
      }
      return field
    }

  //endregion


  //region Слушатели

  private var onScheduleUpdateListeners = mutableListOf<WeakReference<OnScheduleUpdateListener>>()

  fun addOnScheduleUpdateListener(value: OnScheduleUpdateListener) {
    onScheduleUpdateListeners.add(WeakReference(value))
    clearOnScheduleUpdateListeners()
  }

  private fun runScheduleUpdateListeners() {
    onScheduleUpdateListeners.forEach { it.get()?.onScheduleUpdate() }
    clearOnScheduleUpdateListeners()
  }

  private fun clearOnScheduleUpdateListeners() {
    onScheduleUpdateListeners = onScheduleUpdateListeners.filter { it.get() != null }.toMutableList()
  }

  //endregion


  //region Выбранный семестр

  var selectedSemesterId: Long? = null
    get() = if ((field == null) && (semesters.isNotEmpty())) {
      val today = LocalDate.now()
      field = semesters.find { !today.isBefore(it.firstDay) && !today.isAfter(it.lastDay) }?.id ?: semesters.lastOrNull()?.id
      field
    } else field
    set(value) {
      if (field != value) {
        field = if (value != null) getSemester(value)!!.id else null
        lessonsCache = null
        homeworksCache = null
      }
    }

  val selectedSemesterIndex get() = semesters.indexOfFirst { it.id == selectedSemesterId }.takeIf { it >= 0 }!!

  val selectedSemester get() = getSemester(selectedSemesterId!!)

  //endregion


  //region Получение семестров

  val semesters: ImmutableSortedSet<Semester> get() = ImmutableSortedSet.copyOf(semestersCache.values)

  fun getSemester(id: Long) = semestersCache[id]

  val semestersNames get() = semesters.map { it.name }

  fun getSubjects(semesterId: Long): ImmutableSortedSet<String> =
      ImmutableSortedSet.copyOf(getLessons(semesterId).map { it.subjectName })

  fun getTypes(semesterId: Long): ImmutableSortedSet<String> =
      ImmutableSortedSet.copyOf(getLessons(semesterId).map { it.type })

  fun getTeachers(semesterId: Long): ImmutableSortedSet<String> {
    val teachers = sortedSetOf<String>()
    getLessons(semesterId).forEach { teachers.addAll(it.teachers) }
    return ImmutableSortedSet.copyOf(teachers)
  }

  fun getClassrooms(semesterId: Long): ImmutableSortedSet<String> {
    val classrooms = sortedSetOf<String>()
    getLessons(semesterId).forEach { classrooms.addAll(it.classrooms) }
    return ImmutableSortedSet.copyOf(classrooms)
  }

  fun getLessonLength(semesterId: Long): Period {
    var max: Period? = null
    var maxCount = -1
    getLessons(semesterId).groupBy { Period(it.startTime, it.endTime) }.forEach { period, lessons ->
      if (lessons.size > maxCount) {
        maxCount = lessons.size
        max = period
      }
    }
    return max ?: Period(1, 30, 0, 0)
  }

  val hasLessons: Boolean get() {
    semesters.forEach { if (getLessons(it.id).isNotEmpty()) return true }
    return false
  }

  //endregion


  //region Получение пар

  fun getLesson(semesterId: Long, lessonId: Long): Lesson? =
      if (semesterId == selectedSemesterId) lessonsCache!![lessonId]
      else dbHelper.getLesson(semesterId, lessonId)

  fun getLessons(semesterId: Long, predicate: (Lesson) -> Boolean = { true }): ImmutableSortedSet<Lesson> =
      if (semesterId == selectedSemesterId) {
        ImmutableSortedSet.copyOf(lessonsCache!!.filter { predicate(it.value) }.map { it.value })
      } else {
        val lessons = sortedSetOf<Lesson>()
        dbHelper.getLessons(semesterId) { if (predicate(it)) lessons += it }
        ImmutableSortedSet.copyOf(lessons)
      }

  fun getLessons(semesterId: Long, day: LocalDate): ImmutableSortedSet<Lesson> =
      getLessons(semesterId) { it.lessonRepeat.repeatsOnDay(day, getSemester(semesterId)!!.getWeekNumber(day)) }

  fun getLessons(semesterId: Long, weekday: Int): ImmutableSortedSet<Lesson> =
      getLessons(semesterId) { (it.lessonRepeat is LessonRepeat.ByWeekday) && it.lessonRepeat.repeatsOnWeekday(weekday) }

  fun getLessons(semesterId: Long, subjectName: String): ImmutableSortedSet<Lesson> =
      getLessons(semesterId) { it.subjectName == subjectName }

  //endregion


  //region Получение домашних заданий

  fun getHomework(semesterId: Long, homeworkId: Long): Homework? =
      if (semesterId == selectedSemesterId) homeworksCache!![homeworkId]
      else dbHelper.getHomework(semesterId, homeworkId)

  fun getHomeworks(semesterId: Long, predicate: (Homework) -> Boolean = { true }): ImmutableSortedSet<Homework> =
      if (semesterId == selectedSemesterId) {
        ImmutableSortedSet.copyOf(homeworksCache!!.filter { predicate(it.value) }.map { it.value })
      } else {
        val homeworks = sortedSetOf<Homework>()
        dbHelper.getHomeworks(semesterId) { if (predicate(it)) homeworks += it }
        ImmutableSortedSet.copyOf(homeworks)
      }

  fun getHomeworks(semesterId: Long, subjectName: String): ImmutableSortedSet<Homework> =
      if (semesterId == selectedSemesterId) {
        ImmutableSortedSet.copyOf(homeworksCache!!.filter { it.value.subjectName == subjectName }.map { it.value })
      } else {
        val homeworks = sortedSetOf<Homework>()
        dbHelper.getHomeworks(semesterId, subjectName) { homeworks += it }
        ImmutableSortedSet.copyOf(homeworks)
      }

  fun getActualHomeworks(semesterId: Long): ImmutableSortedSet<Homework> {
    val today = LocalDate.now()
    return getHomeworks(semesterId) { !it.deadline.isBefore(today) }
  }

  fun getActualHomeworks(semesterId: Long, lessonId: Long): ImmutableSortedSet<Homework> {
    val today = LocalDate.now()
    return getHomeworks(semesterId) {
      (it.subjectName == getLesson(semesterId, lessonId)!!.subjectName) && !it.deadline.isBefore(today)
    }
  }

  fun getPastHomeworks(semesterId: Long): ImmutableSortedSet<Homework> {
    val today = LocalDate.now()
    return getHomeworks(semesterId) { it.deadline.isBefore(today) }
  }

  //endregion


  //region Редактирование семестра

  fun addSemester(semester: Semester) {
    //Todo: код, создающий патчи

    dbHelper.insertSemester(semester)
    semestersCache.put(semester.id, semester)

    selectedSemesterId = semester.id

    if (context.defaultSharedPreferences.getString("login", "").isNotEmpty() && (ScheduleManager.semesterToSyncId < 0)) {
      ScheduleManager.semesterToSyncId = semester.id
      ScheduleManager.context.defaultSharedPreferences.edit().apply {
        putLong("semester_to_sync_id", ScheduleManager.semesterToSyncId)
      }.apply()
    }

    if (semester.id == semesterToSyncId) {
      context.defaultSharedPreferences.let {
        nettyQuery("${it.getString("login", null)};${it.getString("password", null)}::addsemester::${Converters.registerAll(GsonBuilder()).create().toJson(semester)}")
      }
    }

    runScheduleUpdateListeners()
  }

  fun updateSemester(semester: Semester) {
    //Todo: код, создающий патчи

    dbHelper.updateSemester(semester)
    semestersCache.put(semester.id, semester)

    if (semester.id == semesterToSyncId) {
      context.defaultSharedPreferences.let {
        nettyQuery("${it.getString("login", null)};${it.getString("password", null)}::updatesemester::${Converters.registerAll(GsonBuilder()).create().toJson(semester)}")
      }
    }

    runScheduleUpdateListeners()
  }

  fun removeSemester(id: Long) {
    //Todo: код, создающий патчи

    dbHelper.deleteSemester(id)
    semestersCache.remove(id)

    if (selectedSemesterId == id) selectedSemesterId = null

    runScheduleUpdateListeners()
  }

  //endregion


  //region Редактирование пар

  fun addLesson(semesterId: Long, lesson: Lesson) {
    //Todo: код, создающий патчи

    dbHelper.insertLesson(semesterId, lesson)
    if (semesterId == selectedSemesterId) lessonsCache!!.put(lesson.id, lesson)

    if (semesterId == semesterToSyncId) {
      context.defaultSharedPreferences.let {
        nettyQuery("${it.getString("login", null)};${it.getString("password", null)}::addlesson::$semesterId;${Converters.registerAll(GsonBuilder()).create().toJson(lesson)}")
      }
    }

    runScheduleUpdateListeners()
  }

  fun updateLesson(semesterId: Long, lesson: Lesson) {
    //Todo: код, создающий патчи

    val oldSubjectName = getLesson(semesterId, lesson.id)!!.subjectName

    dbHelper.updateLesson(semesterId, lesson)
    if (semesterId == selectedSemesterId) lessonsCache!!.put(lesson.id, lesson)

    if (getLessons(semesterId, oldSubjectName).isEmpty())
      updateHomeworks(semesterId, oldSubjectName, lesson.subjectName)

    if (semesterId == semesterToSyncId) {
      context.defaultSharedPreferences.let {
        nettyQuery("${it.getString("login", null)};${it.getString("password", null)}::updatelesson::$semesterId;${Converters.registerAll(GsonBuilder()).create().toJson(lesson)}")
      }
    }

    runScheduleUpdateListeners()
  }

  fun updateLessons(semesterId: Long, oldSubjectName: String, newSubjectName: String) {
    //Todo: код, создающий патчи

    dbHelper.updateLessons(semesterId, oldSubjectName, newSubjectName)

    if (semesterId == selectedSemesterId) {
      lessonsCache!!.forEach { (id, lesson) ->
        if (lesson.subjectName == oldSubjectName)
          lessonsCache!!.put(id, lesson.copy(subjectName = newSubjectName))
      }
      homeworksCache!!.forEach { (id, homework) ->
        if (homework.subjectName == oldSubjectName)
          homeworksCache!!.put(id, homework.copy(subjectName = newSubjectName))
      }
    }

    if (semesterId == semesterToSyncId) {
      context.defaultSharedPreferences.let {
        val semester = ScheduleManager.getSemester(ScheduleManager.semesterToSyncId)
        val semesterJson = Gson().toJson(semester)
        val lessons = ScheduleManager.getLessons(ScheduleManager.semesterToSyncId)
        val lessonsJson = Gson().toJson(lessons)

        nettyQuery("${it.getString("login", null)};${it.getString("password", null)}::updateschedule::$semesterJson;$lessonsJson")
      }
    }

    runScheduleUpdateListeners()
  }

  fun removeLesson(semesterId: Long, lessonId: Long) {
    //Todo: код, создающий патчи

    val subjectName = getLesson(semesterId, lessonId)!!.subjectName

    dbHelper.deleteLesson(semesterId, lessonId)
    if (semesterId == selectedSemesterId) lessonsCache!!.remove(lessonId)

    if (getLessons(semesterId, subjectName).isEmpty())
      removeHomeworks(semesterId, subjectName)

    if (semesterId == semesterToSyncId) {
      context.defaultSharedPreferences.let {
        nettyQuery("${it.getString("login", null)};${it.getString("password", null)}::removelesson::$lessonId")
      }
    }

    runScheduleUpdateListeners()
  }

  //endregion


  //region Редактирование заданий

  fun addHomework(semesterId: Long, homework: Homework) {
    //Todo: код, создающий патчи

    dbHelper.insertHomework(semesterId, homework)
    if (semesterId == selectedSemesterId) homeworksCache!!.put(homework.id, homework)

    runScheduleUpdateListeners()
  }

  fun updateHomework(semesterId: Long, homework: Homework) {
    //Todo: код, создающий патчи

    dbHelper.updateHomework(semesterId, homework)
    if (semesterId == selectedSemesterId) homeworksCache!!.put(homework.id, homework)

    runScheduleUpdateListeners()
  }

  fun updateHomeworks(semesterId: Long, oldSubjectName: String, newSubjectName: String) {
    //Todo: код, создающий патчи

    dbHelper.updateHomeworks(semesterId, oldSubjectName, newSubjectName)

    if (semesterId == selectedSemesterId) homeworksCache!!.forEach { (id, homework) ->
      if (homework.subjectName == oldSubjectName)
        homeworksCache!!.put(id, homework.copy(subjectName = newSubjectName))
    }

    runScheduleUpdateListeners()
  }

  fun removeHomework(semesterId: Long, homeworkId: Long) {
    //Todo: код, создающий патчи

    dbHelper.deleteHomework(semesterId, homeworkId)
    if (semesterId == selectedSemesterId) homeworksCache!!.remove(homeworkId)

    runScheduleUpdateListeners()
  }

  fun removeHomeworks(semesterId: Long, subjectName: String) {
    //Todo: код, создающий патчи

    dbHelper.deleteHomeworks(semesterId, subjectName)

    if (semesterId == selectedSemesterId)
      homeworksCache = homeworksCache!!.filter { it.value.subjectName != subjectName }.toMutableMap()

    runScheduleUpdateListeners()
  }

  //endregion


  class ScheduleDBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    private companion object {
      const val DB_NAME = "schedule.db"
      const val DB_VERSION = 1

      const val DATE_PATTERN = "yyyy.MM.dd"
      const val TIME_PATTERN = "HH:mm"
      const val ARRAY_ITEMS_SEPARATOR = ","

      val dateFormatter: DateTimeFormatter = DateTimeFormat.forPattern(DATE_PATTERN)
      val timeFormatter: DateTimeFormatter = DateTimeFormat.forPattern(TIME_PATTERN)
      val joiner: Joiner = Joiner.on(ARRAY_ITEMS_SEPARATOR)
    }

    private object Tables {

      const val SEMESTERS = "semesters"
      const val LESSONS = "lessons"
      const val BY_WEEKDAY = "by_weekday"
      const val BY_DATES = "by_dates"
      const val HOMEWORKS = "homeworks"

      object Semesters {
        const val ID = "_id"
        const val NAME = "name"
        const val FIRST_DAY = "first_day"
        const val LAST_DAY = "last_day"
      }

      object Lessons {
        const val ID = "_id"
        const val SUBJECT_NAME = "subject_name"
        const val TYPE = "type"
        const val TEACHERS = "teachers"
        const val CLASSROOMS = "classrooms"
        const val START_TIME = "start_time"
        const val END_TIME = "end_time"
        const val REPEAT_TYPE = "repeat_type"
        const val REPEAT_TYPE_BY_WEEKDAY = 1
        const val REPEAT_TYPE_BY_DATES = 2
        const val SEMESTER_ID = "semester_id"
      }

      object ByWeekday {
        const val LESSON_ID = "_id"
        const val WEEKDAY = "weekday"
        const val WEEKS = "weeks"
      }

      object ByDates {
        const val LESSON_ID = "_id"
        const val DATES = "dates"
      }

      object Homeworks {
        const val ID = "_id"
        const val SUBJECT_NAME = "subject_name"
        const val DESCRIPTION = "description"
        const val DEADLINE = "deadline"
        const val SEMESTER_ID = "semester_id"
      }
    }

    private object Queries {

      const val createTableSemesters = """
                CREATE TABLE ${Tables.SEMESTERS}(
                    ${Tables.Semesters.ID}          INTEGER PRIMARY KEY,
                    ${Tables.Semesters.NAME}        TEXT NOT NULL,
                    ${Tables.Semesters.FIRST_DAY}   TEXT NOT NULL,
                    ${Tables.Semesters.LAST_DAY}    TEXT NOT NULL);
                """

      const val createTableLessons = """
                CREATE TABLE ${Tables.LESSONS}(
                    ${Tables.Lessons.ID}            INTEGER PRIMARY KEY,
                    ${Tables.Lessons.SUBJECT_NAME}  TEXT NOT NULL,
                    ${Tables.Lessons.TYPE}          TEXT,
                    ${Tables.Lessons.TEACHERS}      TEXT,
                    ${Tables.Lessons.CLASSROOMS}    TEXT,
                    ${Tables.Lessons.START_TIME}    TEXT NOT NULL,
                    ${Tables.Lessons.END_TIME}      TEXT NOT NULL,
                    ${Tables.Lessons.REPEAT_TYPE}   INTEGER NOT NULL,
                    ${Tables.Lessons.SEMESTER_ID}   INTEGER NOT NULL
                                                    REFERENCES ${Tables.SEMESTERS}(${Tables.Semesters.ID})
                                                    ON DELETE CASCADE);
                """

      const val createTableByWeekday = """
                CREATE TABLE ${Tables.BY_WEEKDAY}(
                    ${Tables.ByWeekday.LESSON_ID}   INTEGER PRIMARY KEY
                                                    REFERENCES ${Tables.LESSONS}(${Tables.Lessons.ID})
                                                    ON DELETE CASCADE,
                    ${Tables.ByWeekday.WEEKDAY}     INTEGER NOT NULL,
                    ${Tables.ByWeekday.WEEKS}       TEXT NOT NULL);
                """

      const val createTableByDates = """
                CREATE TABLE ${Tables.BY_DATES}(
                    ${Tables.ByDates.LESSON_ID} INTEGER PRIMARY KEY
                                                REFERENCES ${Tables.LESSONS}(${Tables.Lessons.ID})
                                                ON DELETE CASCADE,
                    ${Tables.ByDates.DATES}     TEXT NOT NULL);
                """

      const val createTableHomeworks = """
                CREATE TABLE ${Tables.HOMEWORKS}(
                    ${Tables.Homeworks.ID}              INTEGER PRIMARY KEY,
                    ${Tables.Homeworks.SUBJECT_NAME}    TEXT NOT NULL,
                    ${Tables.Homeworks.DESCRIPTION}     TEXT NOT NULL,
                    ${Tables.Homeworks.DEADLINE}        TEXT NOT NULL,
                    ${Tables.Homeworks.SEMESTER_ID}     INTEGER NOT NULL
                                                        REFERENCES ${Tables.SEMESTERS}(${Tables.Semesters.ID})
                                                        ON DELETE CASCADE);
                """

      const val getLessons = """
                SELECT ${Tables.LESSONS}.${Tables.Lessons.ID},
                       ${Tables.LESSONS}.${Tables.Lessons.SUBJECT_NAME},
                       ${Tables.LESSONS}.${Tables.Lessons.TYPE},
                       ${Tables.LESSONS}.${Tables.Lessons.TEACHERS},
                       ${Tables.LESSONS}.${Tables.Lessons.CLASSROOMS},
                       ${Tables.LESSONS}.${Tables.Lessons.START_TIME},
                       ${Tables.LESSONS}.${Tables.Lessons.END_TIME},
                       ${Tables.LESSONS}.${Tables.Lessons.REPEAT_TYPE},
                       ${Tables.BY_WEEKDAY}.${Tables.ByWeekday.WEEKDAY},
                       ${Tables.BY_WEEKDAY}.${Tables.ByWeekday.WEEKS},
                       ${Tables.BY_DATES}.${Tables.ByDates.DATES}
                FROM ${Tables.LESSONS}
                LEFT OUTER JOIN ${Tables.BY_WEEKDAY} ON
                                ${Tables.LESSONS}.${Tables.Lessons.ID} =
                                ${Tables.BY_WEEKDAY}.${Tables.ByWeekday.LESSON_ID}
                LEFT OUTER JOIN ${Tables.BY_DATES} ON
                                ${Tables.LESSONS}.${Tables.Lessons.ID} =
                                ${Tables.BY_DATES}.${Tables.ByDates.LESSON_ID}
                WHERE ${Tables.LESSONS}.${Tables.Lessons.SEMESTER_ID} = ?;
                """

      const val getLesson = """
                SELECT ${Tables.LESSONS}.${Tables.Lessons.ID},
                       ${Tables.LESSONS}.${Tables.Lessons.SUBJECT_NAME},
                       ${Tables.LESSONS}.${Tables.Lessons.TYPE},
                       ${Tables.LESSONS}.${Tables.Lessons.TEACHERS},
                       ${Tables.LESSONS}.${Tables.Lessons.CLASSROOMS},
                       ${Tables.LESSONS}.${Tables.Lessons.START_TIME},
                       ${Tables.LESSONS}.${Tables.Lessons.END_TIME},
                       ${Tables.LESSONS}.${Tables.Lessons.REPEAT_TYPE},
                       ${Tables.BY_WEEKDAY}.${Tables.ByWeekday.WEEKDAY},
                       ${Tables.BY_WEEKDAY}.${Tables.ByWeekday.WEEKS},
                       ${Tables.BY_DATES}.${Tables.ByDates.DATES}
                FROM ${Tables.LESSONS}
                LEFT OUTER JOIN ${Tables.BY_WEEKDAY} ON
                                ${Tables.LESSONS}.${Tables.Lessons.ID} =
                                ${Tables.BY_WEEKDAY}.${Tables.ByWeekday.LESSON_ID}
                LEFT OUTER JOIN ${Tables.BY_DATES} ON
                                ${Tables.LESSONS}.${Tables.Lessons.ID} =
                                ${Tables.BY_DATES}.${Tables.ByDates.LESSON_ID}
                WHERE ${Tables.LESSONS}.${Tables.Lessons.SEMESTER_ID} = ? AND
                      ${Tables.LESSONS}.${Tables.Lessons.ID} = ?;
                """

      const val replaceByWeekday = """
                REPLACE INTO ${Tables.BY_WEEKDAY}(${Tables.ByWeekday.LESSON_ID}, ${Tables.ByWeekday.WEEKDAY},
                                                  ${Tables.ByWeekday.WEEKS}) VALUES(?, ?, ?);
                """

      const val replaceByDates = """
                REPLACE INTO ${Tables.BY_DATES}(${Tables.ByDates.LESSON_ID}, ${Tables.ByDates.DATES}) VALUES(?, ?);
                """
    }

    override fun onOpen(db: SQLiteDatabase) {
      super.onOpen(db)
      if (!db.isReadOnly) db.execSQL("PRAGMA foreign_keys = ON;")
    }

    override fun onCreate(db: SQLiteDatabase) {
      db.beginTransaction()

      try {
        db.execSQL(Queries.createTableSemesters)
        db.execSQL(Queries.createTableLessons)
        db.execSQL(Queries.createTableByWeekday)
        db.execSQL(Queries.createTableByDates)
        db.execSQL(Queries.createTableHomeworks)

        db.setTransactionSuccessful()
      } finally {
        db.endTransaction()
      }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) =
        throw UnsupportedOperationException("not implemented")


    //region Чтение из БД

    //region Запросы к БД

    fun getSemesters(block: (Semester) -> Unit) = readableDatabase.use {
      it.query(Tables.SEMESTERS, null, null, null, null, null, Tables.Semesters.FIRST_DAY).use {
        if (it.moveToFirst()) {
          val indexes = it.semesterColumnsIndexes
          do block(it.createSemester(indexes))
          while (it.moveToNext())
        }
      }
    }

    fun getLessons(semesterId: Long, block: (Lesson) -> Unit) = readableDatabase.use {
      it.rawQuery(Queries.getLessons, arrayOf(semesterId.toString())).use {
        if (it.moveToFirst()) {
          val indexes = it.lessonColumnsIndexes
          do block(it.createLesson(indexes))
          while (it.moveToNext())
        }
      }
    }

    fun getLesson(semesterId: Long, lessonId: Long) = readableDatabase.use {
      it.rawQuery(Queries.getLesson, arrayOf(semesterId.toString(), lessonId.toString())).use {
        if (it.moveToFirst()) it.createLesson(it.lessonColumnsIndexes)
        else null
      }
    }

    private val homeworksColumns = arrayOf(
        Tables.Homeworks.ID,
        Tables.Homeworks.SUBJECT_NAME,
        Tables.Homeworks.DESCRIPTION,
        Tables.Homeworks.DEADLINE
    )

    fun getHomeworks(semesterId: Long, block: (Homework) -> Unit) = readableDatabase.use {
      it.query(Tables.HOMEWORKS, homeworksColumns, "${Tables.Homeworks.SEMESTER_ID} = ?",
          arrayOf(semesterId.toString()), null, null, Tables.Homeworks.DEADLINE).use {
        if (it.moveToFirst()) {
          val indexes = it.homeworkColumnsIndexes
          do block(it.createHomework(indexes))
          while (it.moveToNext())
        }
      }
    }

    fun getHomeworks(semesterId: Long, subjectName: String, block: (Homework) -> Unit) = readableDatabase.use {
      it.query(Tables.HOMEWORKS, homeworksColumns, "${Tables.Homeworks.SEMESTER_ID} = ? AND ${Tables.Homeworks.SUBJECT_NAME} = ?",
          arrayOf(semesterId.toString(), subjectName), null, null, Tables.Homeworks.DEADLINE).use {
        if (it.moveToFirst()) {
          val indexes = it.homeworkColumnsIndexes
          do block(it.createHomework(indexes))
          while (it.moveToNext())
        }
      }
    }

    fun getHomework(semesterId: Long, homeworkId: Long) = readableDatabase.use {
      it.query(Tables.HOMEWORKS, homeworksColumns, "${Tables.Homeworks.SEMESTER_ID} = ? AND ${Tables.Homeworks.ID} = ?",
          arrayOf(semesterId.toString(), homeworkId.toString()), null, null, Tables.Homeworks.DEADLINE).use {
        if (it.moveToFirst()) it.createHomework(it.homeworkColumnsIndexes)
        else null
      }
    }

    //endregion


    //region Создатели объектов из курсора

    private fun Cursor.createSemester(indexes: SemesterColumnsIndexes): Semester {
      val name = getString(indexes.name)
      val firstDay = dateFormatter.parseLocalDate(getString(indexes.firstDay))
      val lastDay = dateFormatter.parseLocalDate(getString(indexes.lastDay))
      val id = getLong(indexes.id)

      return Semester(name, firstDay, lastDay, id)
    }

    private fun Cursor.createLesson(indexes: LessonColumnsIndexes): Lesson {
      val subjectName = getString(indexes.subjectName)!!
      val type = getString(indexes.type) ?: ""

      val teachers = ImmutableSortedSet.copyOf(getString(indexes.teachers)?.
          split(ARRAY_ITEMS_SEPARATOR) ?: ImmutableSortedSet.of())
      val classrooms = ImmutableSortedSet.copyOf(getString(indexes.classrooms)?.
          split(ARRAY_ITEMS_SEPARATOR) ?: ImmutableSortedSet.of())

      val startTime = timeFormatter.parseLocalTime(getString(indexes.startTime))
      val endTime = timeFormatter.parseLocalTime(getString(indexes.endTime))

      val lessonRepeat = when (getInt(indexes.repeatType)) {
        Tables.Lessons.REPEAT_TYPE_BY_WEEKDAY -> {
          val weekday = getInt(indexes.weekday)
          val weeks = getString(indexes.weeks).split(ARRAY_ITEMS_SEPARATOR).map(String::toBoolean)
          LessonRepeat.ByWeekday(weekday, weeks)
        }
        Tables.Lessons.REPEAT_TYPE_BY_DATES -> {
          val datesStrings = getString(indexes.dates).split(ARRAY_ITEMS_SEPARATOR)
          val dates = datesStrings.map { dateFormatter.parseLocalDate(it) }
          LessonRepeat.ByDates(ImmutableSortedSet.copyOf(dates))
        }
        else -> throw IllegalStateException("Неизвестный тип повторений: ${getInt(indexes.repeatType)}")
      }

      val id = getLong(indexes.id)

      return Lesson(subjectName, type, teachers, classrooms, startTime, endTime, lessonRepeat, id)
    }

    private fun Cursor.createHomework(indexes: HomeworkColumnsIndexes): Homework {
      val subjectName = getString(indexes.subjectName)
      val description = getString(indexes.description)
      val deadline = dateFormatter.parseLocalDate(getString(indexes.deadline))
      val id = getLong(indexes.id)

      return Homework(subjectName, description, deadline, id)
    }

    //endregion


    //region Получение индексов

    private val Cursor.semesterColumnsIndexes: SemesterColumnsIndexes get() = SemesterColumnsIndexes(
        name = getColumnIndexOrThrow(Tables.Semesters.NAME),
        firstDay = getColumnIndexOrThrow(Tables.Semesters.FIRST_DAY),
        lastDay = getColumnIndexOrThrow(Tables.Semesters.LAST_DAY),
        id = getColumnIndexOrThrow(Tables.Semesters.ID)
    )

    private val Cursor.lessonColumnsIndexes: LessonColumnsIndexes get() = LessonColumnsIndexes(
        subjectName = getColumnIndexOrThrow(Tables.Lessons.SUBJECT_NAME),
        type = getColumnIndexOrThrow(Tables.Lessons.TYPE),
        teachers = getColumnIndexOrThrow(Tables.Lessons.TEACHERS),
        classrooms = getColumnIndexOrThrow(Tables.Lessons.CLASSROOMS),
        startTime = getColumnIndexOrThrow(Tables.Lessons.START_TIME),
        endTime = getColumnIndexOrThrow(Tables.Lessons.END_TIME),
        repeatType = getColumnIndexOrThrow(Tables.Lessons.REPEAT_TYPE),
        weekday = getColumnIndexOrThrow(Tables.ByWeekday.WEEKDAY),
        weeks = getColumnIndexOrThrow(Tables.ByWeekday.WEEKS),
        dates = getColumnIndex(Tables.ByDates.DATES),
        id = getColumnIndexOrThrow(Tables.Lessons.ID)
    )

    private val Cursor.homeworkColumnsIndexes: HomeworkColumnsIndexes get() = HomeworkColumnsIndexes(
        subjectName = getColumnIndexOrThrow(Tables.Homeworks.SUBJECT_NAME),
        description = getColumnIndexOrThrow(Tables.Homeworks.DESCRIPTION),
        deadline = getColumnIndexOrThrow(Tables.Homeworks.DEADLINE),
        id = getColumnIndexOrThrow(Tables.Homeworks.ID)
    )

    //endregion


    //region Контейнеры для индексов

    private data class SemesterColumnsIndexes(val name: Int, val firstDay: Int, val lastDay: Int, val id: Int)

    private data class LessonColumnsIndexes(val subjectName: Int, val type: Int,
                                            val teachers: Int, val classrooms: Int,
                                            val startTime: Int, val endTime: Int,
                                            val repeatType: Int, val weekday: Int, val weeks: Int, val dates: Int,
                                            val id: Int)

    private data class HomeworkColumnsIndexes(val subjectName: Int, val description: Int, val deadline: Int, val id: Int)

    //endregion

    //endregion


    //region Запись в БД

    //region Добавление

    fun insertSemester(semester: Semester) = writableDatabase.use {
      it.insertOrThrow(Tables.SEMESTERS, null, semester.contentValues)
    }

    fun insertLesson(semesterId: Long, lesson: Lesson) = writableDatabase.use {
      it.beginTransaction()

      try {
        it.insertOrThrow(Tables.LESSONS, null, lesson.toContentValues(semesterId))

        when (lesson.lessonRepeat) {
          is LessonRepeat.ByWeekday -> it.insertOrThrow(Tables.BY_WEEKDAY, null, lesson.lessonRepeat.toContentValues(lesson.id))
          is LessonRepeat.ByDates -> it.insertOrThrow(Tables.BY_DATES, null, lesson.lessonRepeat.toContentValues(lesson.id))
          else -> throw IllegalArgumentException("Неизвестный тип повторений: ${lesson.lessonRepeat}")
        }

        it.setTransactionSuccessful()
      } finally {
        it.endTransaction()
      }
    }

    fun insertHomework(semesterId: Long, homework: Homework) = writableDatabase.use {
      it.insertOrThrow(Tables.HOMEWORKS, null, homework.toContentValues(semesterId))
    }

    //endregion


    //region Изменение

    fun updateSemester(semester: Semester) = writableDatabase.use {
      val updatedRows = it.update(Tables.SEMESTERS, semester.contentValues, "${Tables.Semesters.ID} = ?",
          arrayOf(semester.id.toString()))
      if (updatedRows == 0) throw IllegalArgumentException("Семестра $semester нет в БД")

    }

    fun updateLesson(semesterId: Long, lesson: Lesson) = writableDatabase.use {

      fun toBindArgs(lessonRepeat: LessonRepeat.ByWeekday) =
          arrayOf(lesson.id.toString(), lessonRepeat.weekday.toString(), joiner.join(lessonRepeat.weeks))

      fun toBindArgs(lessonRepeat: LessonRepeat.ByDates) =
          arrayOf(lesson.id.toString(), joiner.join(lessonRepeat.dates))

      it.beginTransaction()

      try {
        val updatedRows = it.update(Tables.LESSONS, lesson.toContentValues(semesterId),
            "${Tables.Lessons.SEMESTER_ID} = ? AND ${Tables.Lessons.ID} = ?",
            arrayOf(semesterId.toString(), lesson.id.toString()))
        if (updatedRows == 0) throw IllegalArgumentException("Пары $lesson нет в БД")

        when (lesson.lessonRepeat) {
          is LessonRepeat.ByWeekday -> it.execSQL(Queries.replaceByWeekday, toBindArgs(lesson.lessonRepeat))
          is LessonRepeat.ByDates -> it.execSQL(Queries.replaceByDates, toBindArgs(lesson.lessonRepeat))
          else -> throw IllegalArgumentException("Неизвестный тип повторений: ${lesson.lessonRepeat}")
        }

        it.setTransactionSuccessful()
      } finally {
        it.endTransaction()
      }
    }

    fun updateLessons(semesterId: Long, oldSubjectName: String, newSubjectName: String) = writableDatabase.use {
      val cv = ContentValues()
      cv.put(Tables.Lessons.SUBJECT_NAME, newSubjectName)

      it.beginTransaction()

      try {
        it.update(Tables.LESSONS, cv, "${Tables.Lessons.SEMESTER_ID} = ? AND ${Tables.Lessons.SUBJECT_NAME} = ?",
            arrayOf(semesterId.toString(), oldSubjectName))

        cv.clear()
        cv.put(Tables.Homeworks.SUBJECT_NAME, newSubjectName)

        it.update(Tables.HOMEWORKS, cv, "${Tables.Homeworks.SEMESTER_ID} = ? AND ${Tables.Homeworks.SUBJECT_NAME} = ?",
            arrayOf(semesterId.toString(), oldSubjectName))

        it.setTransactionSuccessful()
      } finally {
        it.endTransaction()
      }
    }

    fun updateHomework(semesterId: Long, homework: Homework) = writableDatabase.use {
      val updatedRows = it.update(Tables.HOMEWORKS, homework.toContentValues(semesterId),
          "${Tables.Homeworks.SEMESTER_ID} = ? AND ${Tables.Homeworks.ID} = ?",
          arrayOf(semesterId.toString(), homework.id.toString()))
      if (updatedRows == 0) throw IllegalArgumentException("Задания $homework нет в БД")
    }

    fun updateHomeworks(semesterId: Long, oldSubjectName: String, newSubjectName: String) = writableDatabase.use {
      val cv = ContentValues()
      cv.put(Tables.Homeworks.SUBJECT_NAME, newSubjectName)

      it.update(Tables.HOMEWORKS, cv, "${Tables.Homeworks.SEMESTER_ID} = ? AND ${Tables.Homeworks.SUBJECT_NAME} = ?",
          arrayOf(semesterId.toString(), oldSubjectName))
      //if (updatedRows == 0) throw IllegalArgumentException("Заданий для $oldSubjectName нет в БД")
    }

    //endregion


    //region Удаление

    fun deleteSemester(id: Long) = writableDatabase.use {
      it.delete(Tables.SEMESTERS, "${Tables.Semesters.ID} = ?", arrayOf(id.toString()))
    }

    fun deleteLesson(semesterId: Long, lessonId: Long) = writableDatabase.use {
      it.delete(Tables.LESSONS, "${Tables.Lessons.SEMESTER_ID} = ? AND ${Tables.Lessons.ID} = ?",
          arrayOf(semesterId.toString(), lessonId.toString()))
    }

    fun deleteHomework(semesterId: Long, lessonId: Long) = writableDatabase.use {
      it.delete(Tables.HOMEWORKS, "${Tables.Homeworks.SEMESTER_ID} = ? AND ${Tables.Homeworks.ID} = ?",
          arrayOf(semesterId.toString(), lessonId.toString()))
    }

    fun deleteHomeworks(semesterId: Long, subjectName: String) = writableDatabase.use {
      it.delete(Tables.HOMEWORKS, "${Tables.Homeworks.SEMESTER_ID} = ? AND ${Tables.Homeworks.SUBJECT_NAME} = ?",
          arrayOf(semesterId.toString(), subjectName))
    }

    //endregion


    //region Преобразования в ContentValues

    private val Semester.contentValues: ContentValues get() {
      val cv = ContentValues()

      cv.put(Tables.Semesters.NAME, name)
      cv.put(Tables.Semesters.FIRST_DAY, dateFormatter.print(firstDay))
      cv.put(Tables.Semesters.LAST_DAY, dateFormatter.print(lastDay))
      cv.put(Tables.Semesters.ID, id)

      return cv
    }

    private fun Lesson.toContentValues(semesterId: Long): ContentValues {
      val cv = ContentValues()

      cv.put(Tables.Lessons.ID, id)

      cv.put(Tables.Lessons.SUBJECT_NAME, subjectName)
      cv.put(Tables.Lessons.TYPE, if (type.isNotBlank()) type else null)

      cv.put(Tables.Lessons.TEACHERS,
          if (teachers.isNotEmpty()) joiner.join(teachers) else null)
      cv.put(Tables.Lessons.CLASSROOMS,
          if (classrooms.isNotEmpty()) joiner.join(classrooms) else null)

      cv.put(Tables.Lessons.START_TIME, timeFormatter.print(startTime))
      cv.put(Tables.Lessons.END_TIME, timeFormatter.print(endTime))

      cv.put(Tables.Lessons.REPEAT_TYPE, when (lessonRepeat) {
        is LessonRepeat.ByWeekday -> Tables.Lessons.REPEAT_TYPE_BY_WEEKDAY
        is LessonRepeat.ByDates -> Tables.Lessons.REPEAT_TYPE_BY_DATES
        else -> throw IllegalArgumentException("Неизвестный тип повторений: $lessonRepeat")
      })

      cv.put(Tables.Lessons.SEMESTER_ID, semesterId)

      return cv
    }

    private fun LessonRepeat.ByWeekday.toContentValues(lessonId: Long): ContentValues {
      val cv = ContentValues()

      cv.put(Tables.ByWeekday.LESSON_ID, lessonId)
      cv.put(Tables.ByWeekday.WEEKDAY, weekday)
      cv.put(Tables.ByWeekday.WEEKS, joiner.join(weeks))

      return cv
    }

    private fun LessonRepeat.ByDates.toContentValues(lessonId: Long): ContentValues {
      val cv = ContentValues()

      cv.put(Tables.ByDates.LESSON_ID, lessonId)
      cv.put(Tables.ByDates.DATES, joiner.join(dates.map { dateFormatter.print(it) }))

      return cv
    }

    private fun Homework.toContentValues(semesterId: Long): ContentValues {
      val cv = ContentValues()

      cv.put(Tables.Homeworks.ID, id)
      cv.put(Tables.Homeworks.SUBJECT_NAME, subjectName)
      cv.put(Tables.Homeworks.DESCRIPTION, description)
      cv.put(Tables.Homeworks.DEADLINE, dateFormatter.print(deadline))
      cv.put(Tables.Homeworks.SEMESTER_ID, semesterId)

      return cv
    }

    //endregion

    //endregion
  }
}
