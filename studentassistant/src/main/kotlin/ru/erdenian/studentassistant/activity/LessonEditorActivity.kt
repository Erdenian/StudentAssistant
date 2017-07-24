package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment
import com.google.common.base.Joiner
import com.google.common.collect.ImmutableSortedSet
import kotlinx.android.synthetic.main.content_lesson_editor.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startService
import org.jetbrains.anko.toast
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.*
import ru.erdenian.studentassistant.schedule.Lesson
import ru.erdenian.studentassistant.schedule.LessonRepeat
import ru.erdenian.studentassistant.schedule.ScheduleManager
import ru.erdenian.studentassistant.service.ScheduleService

class LessonEditorActivity : AppCompatActivity(),
    RadialTimePickerDialogFragment.OnTimeSetListener {

  companion object {

    private const val START_TIME = "start_time"
    private const val END_TIME = "end_time"
    private const val WEEKDAY = "weekday"
    private const val WEEKS = "weeks"

    private const val START_TIME_TAG = "first_day_tag"
    private const val END_TIME_TAG = "last_day_tag"

    private const val TIME_FORMAT = "HH:mm"

    private val joiner = Joiner.on(", ")
  }

  private val semesterId: Long by lazy {
    intent.getLongExtra(SEMESTER_ID, -1L).takeIf { it != -1L } ?: throw IllegalStateException("Не передан id семестра")
  }
  private val lesson: Lesson? by lazy { ScheduleManager.getLessonOrNull(semesterId, intent.getLongExtra(LESSON_ID, -1L)) }
  private val copy: Boolean by lazy { intent.getBooleanExtra(COPY, false) }
  private val weekday: Int by lazy {
    intent.getIntExtra(WEEKDAY, -1).takeIf { it != -1 } ?: (lesson!!.lessonRepeat as? LessonRepeat.ByWeekday)?.weekday ?: 1
  }

  private var startTime: LocalTime? = null
  private var endTime: LocalTime? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_lesson_editor)

    setSupportActionBar(toolbar)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    content_lesson_editor_start_time.setOnClickListener { showTimePicker(this, startTime, START_TIME_TAG) }
    content_lesson_editor_end_time.setOnClickListener { showTimePicker(this, endTime, END_TIME_TAG) }

    if (savedInstanceState == null) {
      lesson?.apply {
        content_lesson_editor_subject_name_edit_text.setText(subjectName)
        content_lesson_editor_lesson_type_edit_text.setText(type)
        content_lesson_editor_teachers_edit_text.setText(joiner.join(teachers))
        content_lesson_editor_classrooms_edit_text.setText(joiner.join(classrooms))

        this@LessonEditorActivity.startTime = startTime
        content_lesson_editor_start_time.text = startTime.toString(TIME_FORMAT)
        this@LessonEditorActivity.endTime = endTime
        content_lesson_editor_end_time.text = endTime.toString(TIME_FORMAT)

        when (lessonRepeat) {
          is LessonRepeat.ByWeekday -> {
            content_lesson_editor_weekdays.setPosition(lessonRepeat.weekday - 1, false)
            content_lesson_editor_weeks_selector.weeks = lessonRepeat.weeks.toBooleanArray()
          }
          is LessonRepeat.ByDates -> TODO()
        }.exhaustive
      } ?: run {
        supportActionBar!!.title = getString(R.string.title_activity_lesson_editor_new_lesson)
        content_lesson_editor_weekdays.setPosition(weekday - 1, false)
      }
    } else {
      val startTimeString = savedInstanceState.getString(START_TIME)
      if (startTimeString != "null") {
        startTime = LocalTime.parse(startTimeString)
        content_lesson_editor_start_time.text = startTime!!.toString(TIME_FORMAT)
      }

      val endTimeString = savedInstanceState.getString(END_TIME, null)
      if (endTimeString != "null") {
        endTime = LocalTime.parse(endTimeString)
        content_lesson_editor_end_time.text = endTime!!.toString(TIME_FORMAT)
      }

      content_lesson_editor_weekdays.setPosition(savedInstanceState.getInt(WEEKDAY), false)

      content_lesson_editor_weeks_selector.weeks = savedInstanceState.getBooleanArray(WEEKS)
    }

    content_lesson_editor_subject_name_edit_text.setAdapter(ArrayAdapter(this,
        android.R.layout.simple_dropdown_item_1line, ScheduleManager.getSubjects(semesterId).asList()))

    content_lesson_editor_lesson_type_edit_text.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,
        (ScheduleManager.getTypes(semesterId) + resources.getStringArray(R.array.lesson_types)).sorted()))

    content_lesson_editor_teachers_edit_text.setAdapter(ArrayAdapter(this,
        android.R.layout.simple_dropdown_item_1line, ScheduleManager.getTeachers(semesterId).asList()))
    content_lesson_editor_teachers_edit_text.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

    content_lesson_editor_classrooms_edit_text.setAdapter(ArrayAdapter(this,
        android.R.layout.simple_dropdown_item_1line, ScheduleManager.getClassrooms(semesterId).asList()))
    content_lesson_editor_classrooms_edit_text.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
  }

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putString(START_TIME, startTime.toString())
    outState.putString(END_TIME, endTime.toString())
    outState.putInt(WEEKDAY, content_lesson_editor_weekdays.position)
    outState.putBooleanArray(WEEKS, content_lesson_editor_weeks_selector.weeks)

    super.onSaveInstanceState(outState)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    content_lesson_editor_weeks_selector.weeks = savedInstanceState.getBooleanArray(WEEKS)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_lesson_editor, menu)
    menu.findItem(R.id.menu_lesson_editor_delete_lesson).isVisible = (lesson != null)
    menu.setColor(getCompatColor(R.color.action_bar_icons_color))
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> finish()
      R.id.menu_lesson_editor_save -> {
        val subjectName = if (content_lesson_editor_subject_name_edit_text.text.trim().isNotBlank()) {
          content_lesson_editor_subject_name_edit_text.text.toString().asSingleLine.trim()
        } else {
          toast(R.string.activity_lesson_editor_incorrect_subject_name_message)
          return super.onOptionsItemSelected(item)
        }

        val type = content_lesson_editor_lesson_type_edit_text.text.toString().asSingleLine.trim()

        val teachers = content_lesson_editor_teachers_edit_text.text.toString().
            asSingleLine.split(",").map(String::trim).filter(String::isNotBlank)

        val classrooms = content_lesson_editor_classrooms_edit_text.text.toString().
            asSingleLine.split(",").map(String::trim).filter(String::isNotBlank)

        if (startTime == null) {
          toast(R.string.activity_lesson_editor_incorrect_start_time_message)
          return super.onOptionsItemSelected(item)
        }

        if (endTime == null) {
          toast(R.string.activity_lesson_editor_incorrect_end_time_message)
          return super.onOptionsItemSelected(item)
        }

        if (!startTime!!.isBefore(endTime)) {
          toast(R.string.activity_lesson_editor_incorrect_time_message)
          return super.onOptionsItemSelected(item)
        }

        val weekday = content_lesson_editor_weekdays.position + 1

        val weeks = content_lesson_editor_weeks_selector.weeks

        if (!weeks.contains(true)) {
          toast(R.string.activity_lesson_editor_no_weeks_checked_message)
          return super.onOptionsItemSelected(item)
        }

        fun saveChanges() {
          if ((lesson == null) || copy) {
            ScheduleManager.addLesson(semesterId, Lesson(subjectName, type, ImmutableSortedSet.copyOf(teachers),
                ImmutableSortedSet.copyOf(classrooms), startTime!!, endTime!!,
                LessonRepeat.ByWeekday(weekday, weeks.toList())))
          } else {
            ScheduleManager.updateLesson(semesterId, lesson!!.copy(subjectName, type, ImmutableSortedSet.copyOf(teachers),
                ImmutableSortedSet.copyOf(classrooms), startTime!!, endTime!!,
                LessonRepeat.ByWeekday(weekday, weeks.toList())))
          }

          startService<ScheduleService>()
          finish()
        }

        if ((lesson != null) && (subjectName != lesson!!.subjectName) &&
            (ScheduleManager.getLessons(semesterId, lesson!!.subjectName).size > (if (copy) 0 else 1))) {

          alert(R.string.activity_lesson_editor_alert_rename_lessons_message,
              R.string.activity_lesson_editor_alert_rename_lessons_title) {
            positiveButton(R.string.activity_lesson_editor_alert_rename_lessons_yes) {
              saveChanges()
              ScheduleManager.updateLessons(semesterId, lesson!!.subjectName, subjectName)
            }
            negativeButton(R.string.activity_lesson_editor_alert_rename_lessons_no) { saveChanges() }
            //neutralButton(R.string.activity_lesson_editor_alert_rename_lessons_cancel)
          }.show()
        } else saveChanges()
      }
      R.id.menu_lesson_editor_delete_lesson -> {
        fun remove() {
          ScheduleManager.removeLesson(semesterId, lesson!!.id)

          startService<ScheduleService>()
          finish()
        }

        if (ScheduleManager.getHomeworks(semesterId, lesson!!.subjectName).isNotEmpty()
            && (ScheduleManager.getLessons(semesterId, lesson!!.subjectName).size == 1)) {

          alert(R.string.activity_lesson_editor_alert_delete_homeworks_message,
              R.string.activity_lesson_editor_alert_delete_homeworks_title) {
            positiveButton(R.string.activity_lesson_editor_alert_delete_homeworks_yes) { remove() }
            negativeButton(R.string.activity_lesson_editor_alert_delete_homeworks_cancel) {}
          }.show()
        } else {

          alert(R.string.activity_lesson_editor_alert_delete_message) {
            positiveButton(R.string.activity_lesson_editor_alert_delete_yes) { remove() }
            negativeButton(R.string.activity_lesson_editor_alert_delete_no) {}
          }.show()
        }
      }
      else -> throw IllegalArgumentException("Неизвестный id: ${item.itemId}")
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onTimeSet(dialog: RadialTimePickerDialogFragment, hourOfDay: Int, minute: Int) {
    val newTime = LocalTime(hourOfDay, minute)
    when (dialog.tag) {
      START_TIME_TAG -> {
        startTime = newTime
        content_lesson_editor_start_time.text = newTime.toString(TIME_FORMAT)
        endTime = startTime!! + ScheduleManager.getLessonLength(semesterId)
        content_lesson_editor_end_time.text = endTime?.toString(TIME_FORMAT)
      }
      END_TIME_TAG -> {
        endTime = newTime
        content_lesson_editor_end_time.text = newTime.toString(TIME_FORMAT)
      }
      else -> IllegalArgumentException("Неизвестный тег: ${dialog.tag}")
    }
  }
}
