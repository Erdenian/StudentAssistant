package ru.erdenian.studentassistant.localdata

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.common.base.Joiner
import com.google.common.collect.ImmutableSortedSet
import org.joda.time.LocalDate
import org.joda.time.Period
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import ru.erdenian.studentassistant.extensions.exhaustive
import ru.erdenian.studentassistant.extensions.toImmutableSortedSet
import ru.erdenian.studentassistant.schedule.Homework
import ru.erdenian.studentassistant.schedule.Lesson
import ru.erdenian.studentassistant.schedule.LessonRepeat
import ru.erdenian.studentassistant.schedule.Semester
import java.lang.ref.WeakReference

/**
 * Синглтон для работы с сохраненным расписанием.
 *
 * Содержит методы для чтения и изменения расписания.
 *
 * @author Ilya Solovyev
 * @since 0.0.0
 */
object ScheduleManager {

  /**
   * Экземпляр соединения с базой данных.
   *
   * @author Ilya Solovyev
   * @since 0.0.0
   */
  private lateinit var dbHelper: ScheduleDBHelper

  /**
   * Инициализация объекта.
   *
   * @author Ilya Solovyev
   * @since 0.0.0
   */
  fun initialize(context: Context) {
    dbHelper = ScheduleDBHelper(context.applicationContext)
  }

  //region Кэш

  /**
   * Кэш семестров.
   *
   * Заполняется сам при первом обращении к нему. Держит в себе все семетры в расписании.
   *
   * @author Ilya Solovyev
   * @since 0.0.0
   */
  private val semestersCache: MutableMap<Long, Semester> by lazy {
    mutableMapOf<Long, Semester>().apply {
      dbHelper.getSemesters { put(it.id, it) }
    }
  }

  /**
   * Кэш пар.
   *
   * Содержит в себе список пар [выбранного семестра][selectedSemesterId], либо null, если нет ни одного семестра.
   * Если равен null, и есть выбранный семестр, то при очередном обращении заполняется сам.
   * Присваивать можно только null для очистки кэша.
   *
   * @author Ilya Solovyev
   * @since 0.0.0
   * @throws IllegalArgumentException если присваемое значение не null
   */
  private var lessonsCache: MutableMap<Long, Lesson>? = null
    get() {
      if ((field == null) && (semestersCache.isNotEmpty()))
        field = mutableMapOf<Long, Lesson>().apply {
          dbHelper.getLessons(selectedSemesterId ?: return null) { put(it.id, it) }
        }
      return field
    }
    set(value) {
      // Todo: разобаться
      //if (value != null) throw IllegalArgumentException("Допускается только сброс кэша присвоением ему null")
      field = value
    }

  /**
   * Кэш домашних заданий.
   *
   * Содержит в себе список домашних заданий [выбранного семестра][selectedSemesterId], либо null, если нет ни одного семестра.
   * Если равен null, и есть выбранный семестр, то при очередном обращении заполняется сам.
   * Присваивать можно только null для очистки кэша.
   *
   * @author Ilya Solovyev
   * @since 0.1.0
   * @throws IllegalArgumentException если присваемое значение не null
   */
  private var homeworksCache: MutableMap<Long, Homework>? = null
    get() {
      if ((field == null) && (semestersCache.isNotEmpty()))
        field = mutableMapOf<Long, Homework>().apply {
          dbHelper.getHomeworks(selectedSemesterId ?: return null) { put(it.id, it) }
        }
      return field
    }
    set(value) {
      // Todo: разобаться
      //if (value != null) throw IllegalArgumentException("Допускается только сброс кэша присвоением ему null")
      field = value
    }

  //endregion

  //region Обработчики

  /**
   * Интерфейс для оповещения об изменении расписания.
   *
   * @author Ilya Solovyev
   * @since 0.0.0
   */
  interface OnScheduleUpdateListener {

    /**
     * Вызывается после изменения расписания.
     *
     * @author Ilya Solovyev
     * @since 0.0.0
     */
    fun onScheduleUpdate()
  }

  private var onScheduleUpdateListeners = mutableListOf<WeakReference<OnScheduleUpdateListener>>()

  fun addOnScheduleUpdateListener(listener: OnScheduleUpdateListener) {
    onScheduleUpdateListeners.add(WeakReference(listener))
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

  /**
   * Id выбранного семестра.
   *
   * Содержит в семе id выбранного семестра, либо null, если список семестров пуст.
   * Если равен null, и есть хотя бы один семестр, сам выбирает семестр и сохраняет в себе его id.
   *
   * @author Ilya Solovyev
   * @since 0.0.0
   */
  var selectedSemesterId: Long? = null
    get() {
      if ((field == null) && (semesters.isNotEmpty())) {
        val today = LocalDate.now()
        field = semesters.find { (it.firstDay <= today) && (today <= it.lastDay) }?.id ?: semesters.lastOrNull()?.id
      }
      return field
    }
    set(value) {
      if (field != value) {
        field = value?.let { getSemester(it).id }
        lessonsCache = null
        homeworksCache = null
      }
    }

  /**
   * Индекс выбранного семестра.
   *
   * Позиция семестра в отсортированном списке всех семестров.
   *
   * @author Ilya Solovyev
   * @since 0.0.0
   * @throws IllegalStateException если [selectedSemesterId] равен null или в нем записан несуществующий id
   */
  val selectedSemesterIndex: Int
    get() {
      val selectedSemesterId = selectedSemesterId ?: throw IllegalStateException("Не выбран семестр")
      return semesters.indexOfFirst { it.id == selectedSemesterId }.takeIf { it >= 0 } ?:
          throw IllegalStateException("Неверное значение выбранного семестра")
    }

  /**
   * Выбранный семестр.
   *
   * @author Ilya Solovyev
   * @since 0.0.0
   * @throws IllegalStateException если [selectedSemesterId] равен null
   */
  val selectedSemester get() = getSemester(selectedSemesterId ?: throw IllegalStateException("Семестр не выбран"))

  //endregion

  //region Получение семестров

  /**
   * Список всех семестров в расписании.
   *
   * @author Ilya Solovyev
   * @since 0.0.0
   */
  val semesters get() = semestersCache.values.toImmutableSortedSet()

  /**
   * Позволяет получить семестр по его id.
   *
   * @author Ilya Solovyev
   * @since 0.0.0
   * @param id id семестра
   * @return семестр с заданным id
   * @throws IllegalArgumentException если семестра с таким id нет
   */
  fun getSemester(id: Long) = getSemesterOrNull(id) ?: throw IllegalArgumentException("Нет семестра с таким id")

  /**
   * Позволяет получить семестр по его id.
   *
   * @author Ilya Solovyev
   * @since 0.0.0
   * @param id id семестра
   * @return семестр с заданным id, либо null, если такого семестра нет
   */
  fun getSemesterOrNull(id: Long) = semestersCache[id]

  /**
   * Список имен всех семестров.
   *
   * @author Ilya Solovyev
   * @since 0.0.0
   * @see Semester.name
   */
  val semestersNames get() = semestersCache.map { (_, semester) -> semester.name }

  /**
   * Список всех предметов заданного семестра.
   *
   * @author Ilya Solovyev
   * @since 0.1.0
   * @param semesterId id семестра
   * @return список всех предметов в этом семестре
   */
  fun getSubjects(semesterId: Long) = getLessons(semesterId).map { it.subjectName }.toImmutableSortedSet()

  /**
   * Список всех типов пар в заданном семестре.
   *
   * @author Ilya Solovyev
   * @since 0.2.5
   * @param semesterId id семестра
   * @return список из всех типов пар в этом семестре
   */
  fun getTypes(semesterId: Long) = getLessons(semesterId).map { it.type }.toImmutableSortedSet()

  /**
   * Список всех преподавателей заданного семестра.
   *
   * @author Ilya Solovyev
   * @since 0.2.5
   * @param semesterId id семестра
   * @return список всех преподавателей, ведущих пары в этом семестре
   */
  fun getTeachers(semesterId: Long) =
      sortedSetOf<String>().apply { getLessons(semesterId).forEach { addAll(it.teachers) } }.toImmutableSortedSet()

  /**
   * Список всех аудиторий заданного семестра.
   *
   * @author Ilya Solovyev
   * @since 0.2.5
   * @param semesterId id семестра
   * @return список всех аудиторий, в которых проходят пары в этом семестре
   */
  fun getClassrooms(semesterId: Long) =
      sortedSetOf<String>().apply { getLessons(semesterId).forEach { addAll(it.classrooms) } }.toImmutableSortedSet()

  /**
   * Самая частая длительность пары в заданном семестре, либо длительность по умолчанию (1 час 30 минут).
   *
   * @author Ilya Solovyev
   * @since 0.2.6
   * @param semesterId id семестра
   * @return самую частую продолжительность пары в этом семестре, либо продолжительность по умолчанию, если он пуст)
   */
  fun getLessonLength(semesterId: Long) =
      getLessons(semesterId).groupBy { Period(it.startTime, it.endTime) }.maxBy { (_, lessons) -> lessons.size }?.key ?:
          Period(1, 30, 0, 0)

  /**
   * Имеется ли в расписании хотя бы одна пара.
   *
   * Если хотя бы один семестр содержит хотя бы одну пару, вернет true, false в противном случае.
   *
   * @author Ilya Solovyev
   * @since 0.2.0
   */
  val hasLessons: Boolean get() {
    semesters.forEach { if (getLessons(it.id).isNotEmpty()) return true }
    return false
  }

  //endregion

  //region Получение пар

  /**
   * Позволяет получить пару по id.
   *
   * @author Ilya Solovyev
   * @since 0.0.0
   * @param semesterId id семестра, содержащего пару
   * @param lessonId id пары
   * @return пару из заданного семестра с заданным id
   * @throws IllegalArgumentException если пара не найдена
   */
  fun getLesson(semesterId: Long, lessonId: Long) =
      getLessonOrNull(semesterId, lessonId) ?: throw IllegalArgumentException("Пара не найдена")

  /**
   * Позволяет получить пару по id.
   *
   * @author Ilya Solovyev
   * @since 0.2.6
   * @param semesterId id семестра, содержащего пару
   * @param lessonId id пары
   * @return пару из заданного семестра с заданным id, либо null, если пара не найдена
   */
  fun getLessonOrNull(semesterId: Long, lessonId: Long) =
      if (semesterId == selectedSemesterId) lessonsCache!![lessonId]
      else dbHelper.getLesson(semesterId, lessonId)

  /**
   * Позволяет получить список пар из определенного семестра, удовлетворяющих условию отбора.
   *
   * @author Ilya Solovyev
   * @since 0.2.0
   * @param semesterId id семестра
   * @param predicate условие отбора (по умолчанию { true })
   * @return коллекцию пар из семестра с id [semesterId], удовлетворяющих [predicate]
   */
  fun getLessons(semesterId: Long, predicate: (Lesson) -> Boolean = { true }) =
      (if (semesterId == selectedSemesterId) lessonsCache!!.filter { predicate(it.value) }.map { it.value }
      else sortedSetOf<Lesson>().apply { dbHelper.getLessons(semesterId) { if (predicate(it)) add(it) } })
          .toImmutableSortedSet()

  /**
   * Позволяет получить список пар из определенного семетра, которые будут в определенный день.
   *
   * @author Ilya Solovyev
   * @since 0.0.0
   * @param semesterId id семестра
   * @param day день
   * @return список пар из семестра с id [semesterId], которые будут в [этот день][day]
   */
  fun getLessons(semesterId: Long, day: LocalDate): ImmutableSortedSet<Lesson> {
    val weekNumber = getSemester(semesterId).getWeekNumber(day)
    return getLessons(semesterId) { it.lessonRepeat.repeatsOnDay(day, weekNumber) }
  }

  /**
   * Позволяет получить список пар из определенного семетра, которые повторяются в определенный день недели.
   *
   * @author Ilya Solovyev
   * @since 0.0.0
   * @param semesterId id семестра
   * @param weekday день недели (понедельник - 1, воскресенье - 7)
   * @return список пар из семестра с id [semesterId], которые повторяются в [этот день недели][weekday]
   */
  fun getLessons(semesterId: Long, weekday: Int) =
      getLessons(semesterId) { (it.lessonRepeat as? LessonRepeat.ByWeekday)?.repeatsOnWeekday(weekday) ?: false }

  /**
   * Позволяет получить список пар определенного предмета из определенного семетра.
   *
   * @author Ilya Solovyev
   * @since 0.2.0
   * @param semesterId id семестра
   * @param subjectName название предмета
   * @return список пар предмета [subjectName] из семестра с id [semesterId]
   */
  fun getLessons(semesterId: Long, subjectName: String) =
      getLessons(semesterId) { it.subjectName == subjectName }

  //endregion

  //region Получение домашних заданий

  /**
   * Возвращает домашнее задание по его id и id семестра.
   *
   * @author Ilya Solovyev
   * @since 0.1.0
   * @param semesterId id семестра, содержащего домашнее задание
   * @param homeworkId id домашнего задания
   * @return домашнее задание из заданного семестра с заданным id
   * @throws IllegalArgumentException если задание не найдено
   */
  fun getHomework(semesterId: Long, homeworkId: Long) =
      getHomeworkOrNull(semesterId, homeworkId) ?: throw IllegalArgumentException("Задание не найдено")

  /**
   * Возвращает домашнее задание по его id и id семестра.
   *
   * @author Ilya Solovyev
   * @since 0.2.6
   * @param semesterId id семестра, содержащего домашнее задание
   * @param homeworkId id домашнего задания
   * @return домашнее задание из заданного семестра с заданным id, либо null, если задание не найдено
   */
  fun getHomeworkOrNull(semesterId: Long, homeworkId: Long) =
      if (semesterId == selectedSemesterId) homeworksCache!![homeworkId]
      else dbHelper.getHomework(semesterId, homeworkId)

  /**
   * Позволяет получить список домашних заданий из определенного семестра, удовлетворяющих условию отбора.
   *
   * @author Ilya Solovyev
   * @since 0.2.0
   * @param semesterId id семестра
   * @param predicate условие отбора (по умолчанию { true })
   * @return коллекцию заданий из семестра с id [semesterId], удовлетворяющих [predicate]
   */
  fun getHomeworks(semesterId: Long, predicate: (Homework) -> Boolean = { true }): ImmutableSortedSet<Homework> =
      (if (semesterId == selectedSemesterId) homeworksCache!!.filter { predicate(it.value) }.map { it.value }
      else sortedSetOf<Homework>().apply { dbHelper.getHomeworks(semesterId) { if (predicate(it)) add(it) } })
          .toImmutableSortedSet()

  /**
   * Позволяет получить список домашних заданий по определенному предмету из определенного семетра.
   *
   * @author Ilya Solovyev
   * @since 0.2.0
   * @param semesterId id семестра
   * @param subjectName название предмета
   * @return список заданий по предмету [subjectName] из семестра с id [semesterId]
   */
  fun getHomeworks(semesterId: Long, subjectName: String): ImmutableSortedSet<Homework> =
      (if (semesterId == selectedSemesterId) homeworksCache!!.filter { it.value.subjectName == subjectName }.map { it.value }
      else sortedSetOf<Homework>().apply { dbHelper.getHomeworks(semesterId, subjectName) { add(it) } })
          .toImmutableSortedSet()

  /**
   * Позволяет получить список еще не сданных домашних заданий.
   *
   * @author Ilya Solovyev
   * @since 0.1.0
   * @param semesterId id семестра
   * @return список еще не сданных заданий из семестра с id [semesterId]
   */
  fun getActualHomeworks(semesterId: Long): ImmutableSortedSet<Homework> {
    val today = LocalDate.now()
    return getHomeworks(semesterId) { it.deadline >= today }
  }

  /**
   * Позволяет получить список еще не сданных домашних заданий по определенному предмету.
   *
   * @author Ilya Solovyev
   * @since 0.2.6
   * @param semesterId id семестра
   * @param subjectName название предмета
   * @return список еще не сданных заданий по предмету [subjectName] из семестра с id [semesterId]
   */
  fun getActualHomeworks(semesterId: Long, subjectName: String): ImmutableSortedSet<Homework> {
    val today = LocalDate.now()
    return getHomeworks(semesterId) { (it.subjectName == subjectName) && (it.deadline >= today) }
  }

  /**
   * Позволяет получить список сданных домашних заданий.
   *
   * @author Ilya Solovyev
   * @since 0.1.0
   * @param semesterId id семестра
   * @return список сданных заданий из семестра с id [semesterId]
   */
  fun getPastHomeworks(semesterId: Long): ImmutableSortedSet<Homework> {
    val today = LocalDate.now()
    return getHomeworks(semesterId) { it.deadline < today }
  }

  //endregion

  //region Редактирование семестра

  fun addSemester(semester: Semester, notifyListeners: Boolean = true) {
    //Todo: код, создающий патчи

    dbHelper.insertSemester(semester)
    semestersCache.put(semester.id, semester)

    selectedSemesterId = semester.id

    if (notifyListeners) runScheduleUpdateListeners()
  }

  fun updateSemester(semester: Semester, notifyListeners: Boolean = true) {
    //Todo: код, создающий патчи

    dbHelper.updateSemester(semester)
    semestersCache.put(semester.id, semester)

    if (notifyListeners) runScheduleUpdateListeners()
  }

  fun removeSemester(id: Long, notifyListeners: Boolean = true) {
    //Todo: код, создающий патчи

    dbHelper.deleteSemester(id)
    semestersCache.remove(id)

    if (selectedSemesterId == id) selectedSemesterId = null

    if (notifyListeners) runScheduleUpdateListeners()
  }

  //endregion

  //region Редактирование пар

  fun addLesson(semesterId: Long, lesson: Lesson, notifyListeners: Boolean = true) {
    //Todo: код, создающий патчи

    dbHelper.insertLesson(semesterId, lesson)
    if (semesterId == selectedSemesterId) lessonsCache!!.put(lesson.id, lesson)

    if (notifyListeners) runScheduleUpdateListeners()
  }

  fun updateLesson(semesterId: Long, lesson: Lesson, notifyListeners: Boolean = true) {
    //Todo: код, создающий патчи

    val oldSubjectName = getLesson(semesterId, lesson.id).subjectName

    dbHelper.updateLesson(semesterId, lesson)
    if (semesterId == selectedSemesterId) lessonsCache!!.put(lesson.id, lesson)

    if (getLessons(semesterId, oldSubjectName).isEmpty())
      updateHomeworks(semesterId, oldSubjectName, lesson.subjectName, notifyListeners = false)

    if (notifyListeners) runScheduleUpdateListeners()
  }

  fun updateLessons(semesterId: Long, oldSubjectName: String, newSubjectName: String, notifyListeners: Boolean = true) {
    //Todo: код, создающий патчи

    dbHelper.updateLessons(semesterId, oldSubjectName, newSubjectName)

    if (semesterId == selectedSemesterId) {
      lessonsCache!!.apply {
        forEach { (id, lesson) ->
          if (lesson.subjectName == oldSubjectName) put(id, lesson.copy(subjectName = newSubjectName))
        }
      }
      homeworksCache!!.apply {
        forEach { (id, homework) ->
          if (homework.subjectName == oldSubjectName) put(id, homework.copy(subjectName = newSubjectName))
        }
      }
    }

    if (notifyListeners) runScheduleUpdateListeners()
  }

  fun removeLesson(semesterId: Long, lessonId: Long, notifyListeners: Boolean = true) {
    //Todo: код, создающий патчи

    val subjectName = getLesson(semesterId, lessonId).subjectName

    dbHelper.deleteLesson(semesterId, lessonId)
    if (semesterId == selectedSemesterId) lessonsCache!!.remove(lessonId)

    if (getLessons(semesterId, subjectName).isEmpty())
      removeHomeworks(semesterId, subjectName, notifyListeners = false)

    if (notifyListeners) runScheduleUpdateListeners()
  }

  //endregion

  //region Редактирование заданий

  fun addHomework(semesterId: Long, homework: Homework, notifyListeners: Boolean = true) {
    //Todo: код, создающий патчи

    dbHelper.insertHomework(semesterId, homework)
    if (semesterId == selectedSemesterId) homeworksCache!!.put(homework.id, homework)

    if (notifyListeners) runScheduleUpdateListeners()
  }

  fun updateHomework(semesterId: Long, homework: Homework, notifyListeners: Boolean = true) {
    //Todo: код, создающий патчи

    dbHelper.updateHomework(semesterId, homework)
    if (semesterId == selectedSemesterId) homeworksCache!!.put(homework.id, homework)

    if (notifyListeners) runScheduleUpdateListeners()
  }

  fun updateHomeworks(semesterId: Long, oldSubjectName: String, newSubjectName: String, notifyListeners: Boolean = true) {
    //Todo: код, создающий патчи

    if (getLessons(semesterId) { it.subjectName == newSubjectName }.isEmpty())
      throw IllegalArgumentException("Нет пар предмета $newSubjectName")

    dbHelper.updateHomeworks(semesterId, oldSubjectName, newSubjectName)

    if (semesterId == selectedSemesterId) homeworksCache!!.apply {
      forEach { (id, homework) ->
        if (homework.subjectName == oldSubjectName) homeworksCache!!.put(id, homework.copy(subjectName = newSubjectName))
      }
    }

    if (notifyListeners) runScheduleUpdateListeners()
  }

  fun removeHomework(semesterId: Long, homeworkId: Long, notifyListeners: Boolean = true) {
    //Todo: код, создающий патчи

    dbHelper.deleteHomework(semesterId, homeworkId)
    if (semesterId == selectedSemesterId) homeworksCache!!.remove(homeworkId)

    if (notifyListeners) runScheduleUpdateListeners()
  }

  fun removeHomeworks(semesterId: Long, subjectName: String, notifyListeners: Boolean = true) {
    //Todo: код, создающий патчи

    dbHelper.deleteHomeworks(semesterId, subjectName)

    if (semesterId == selectedSemesterId)
      homeworksCache = homeworksCache!!.filter { it.value.subjectName != subjectName }.toMutableMap()

    if (notifyListeners) runScheduleUpdateListeners()
  }

  //endregion

  class ScheduleDBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
      private const val DB_NAME = "schedule.db"
      private const val DB_VERSION = 1

      private const val DATE_PATTERN = "yyyy.MM.dd"
      private const val TIME_PATTERN = "HH:mm"
      private const val ARRAY_ITEMS_SEPARATOR = ","

      private val dateFormatter: DateTimeFormatter = DateTimeFormat.forPattern(DATE_PATTERN)
      private val timeFormatter: DateTimeFormatter = DateTimeFormat.forPattern(TIME_PATTERN)
      private val joiner: Joiner = Joiner.on(ARRAY_ITEMS_SEPARATOR)
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

      const val CREATE_TABLE_SEMESTERS = """
        CREATE TABLE ${Tables.SEMESTERS}(
        ${Tables.Semesters.ID}        INTEGER PRIMARY KEY,
        ${Tables.Semesters.NAME}      TEXT NOT NULL,
        ${Tables.Semesters.FIRST_DAY} TEXT NOT NULL,
        ${Tables.Semesters.LAST_DAY}  TEXT NOT NULL);
      """

      const val CREATE_TABLE_LESSONS = """
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

      const val CREATE_TABLE_BY_WEEKDAY = """
        CREATE TABLE ${Tables.BY_WEEKDAY}(
          ${Tables.ByWeekday.LESSON_ID} INTEGER PRIMARY KEY
                                        REFERENCES ${Tables.LESSONS}(${Tables.Lessons.ID})
                                        ON DELETE CASCADE,
          ${Tables.ByWeekday.WEEKDAY}   INTEGER NOT NULL,
          ${Tables.ByWeekday.WEEKS}     TEXT NOT NULL);
      """

      const val CREATE_TABLE_BY_DATES = """
        CREATE TABLE ${Tables.BY_DATES}(
          ${Tables.ByDates.LESSON_ID} INTEGER PRIMARY KEY
                                      REFERENCES ${Tables.LESSONS}(${Tables.Lessons.ID})
                                      ON DELETE CASCADE,
          ${Tables.ByDates.DATES}     TEXT NOT NULL);
      """

      const val CREATE_TABLE_HOMEWORKS = """
        CREATE TABLE ${Tables.HOMEWORKS}(
          ${Tables.Homeworks.ID}            INTEGER PRIMARY KEY,
          ${Tables.Homeworks.SUBJECT_NAME}  TEXT NOT NULL,
          ${Tables.Homeworks.DESCRIPTION}   TEXT NOT NULL,
          ${Tables.Homeworks.DEADLINE}      TEXT NOT NULL,
          ${Tables.Homeworks.SEMESTER_ID}   INTEGER NOT NULL
                                            REFERENCES ${Tables.SEMESTERS}(${Tables.Semesters.ID})
                                            ON DELETE CASCADE);
      """

      const val GET_LESSONS = """
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

      const val GET_LESSON = """
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

      const val REPLACE_BY_WEEKDAY = """
        REPLACE INTO ${Tables.BY_WEEKDAY}(${Tables.ByWeekday.LESSON_ID}, ${Tables.ByWeekday.WEEKDAY},
                                          ${Tables.ByWeekday.WEEKS}) VALUES(?, ?, ?);
      """

      const val REPLACE_BY_DATES = """
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
        db.execSQL(Queries.CREATE_TABLE_SEMESTERS)
        db.execSQL(Queries.CREATE_TABLE_LESSONS)
        db.execSQL(Queries.CREATE_TABLE_BY_WEEKDAY)
        db.execSQL(Queries.CREATE_TABLE_BY_DATES)
        db.execSQL(Queries.CREATE_TABLE_HOMEWORKS)

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
      it.rawQuery(Queries.GET_LESSONS, arrayOf(semesterId.toString())).use {
        if (it.moveToFirst()) {
          val indexes = it.lessonColumnsIndexes
          do block(it.createLesson(indexes))
          while (it.moveToNext())
        }
      }
    }

    fun getLesson(semesterId: Long, lessonId: Long) = readableDatabase.use {
      it.rawQuery(Queries.GET_LESSON, arrayOf(semesterId.toString(), lessonId.toString())).use {
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

        val lessonRepeat = lesson.lessonRepeat
        when (lessonRepeat) {
          is LessonRepeat.ByWeekday -> it.insertOrThrow(Tables.BY_WEEKDAY, null, lessonRepeat.toContentValues(lesson.id))
          is LessonRepeat.ByDates -> it.insertOrThrow(Tables.BY_DATES, null, lessonRepeat.toContentValues(lesson.id))
        }.exhaustive

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

        val lessonRepeat = lesson.lessonRepeat
        when (lessonRepeat) {
          is LessonRepeat.ByWeekday -> it.execSQL(Queries.REPLACE_BY_WEEKDAY, toBindArgs(lessonRepeat))
          is LessonRepeat.ByDates -> it.execSQL(Queries.REPLACE_BY_DATES, toBindArgs(lessonRepeat))
        }.exhaustive

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
