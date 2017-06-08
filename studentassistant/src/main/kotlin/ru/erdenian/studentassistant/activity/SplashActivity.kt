package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fatboyindustrial.gsonjodatime.Converters
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSortedSet
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startService
import ru.erdenian.studentassistant.netty.*
import ru.erdenian.studentassistant.schedule.Lesson
import ru.erdenian.studentassistant.schedule.LessonRepeat
import ru.erdenian.studentassistant.schedule.ScheduleManager
import ru.erdenian.studentassistant.schedule.Semester
import ru.erdenian.studentassistant.service.ScheduleService

class SplashActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    ScheduleManager.initialize(applicationContext)

    defaultSharedPreferences.apply {
      val login = getString("login", "null")
      val password = getString("password", "null")

      nettyQuery("$login;$password::getuser::") {
        val user = Gson().fromJson(it, User::class.java)

        nettyQuery("$login;$password::getschedule::${user.groupId}") {
          val args = it.split(';')
          val semesterJson = args[0]

          val gson = Converters.registerAll(GsonBuilder())
              .registerTypeAdapter(ImmutableSortedSet::class.java, ImmutableSortedSetDeserializer())
              .registerTypeAdapter(ImmutableList::class.java, ImmutableListDeserializer())
              .registerTypeAdapter(LessonRepeat::class.java, LessonRepeatDeserializer())
              .create()

          val semester = gson.fromJson(semesterJson, Semester::class.java)
          val semester1 = Semester(semester.name, semester.firstDay, semester.lastDay, semester.id)
          ScheduleManager.removeSemester(semester1.id)
          ScheduleManager.addSemester(semester1)

          for (i in 1..(args.size - 1)) {
            ScheduleManager.addLesson(semester.id, gson.fromJson(args[i], Lesson::class.java))
          }
        }
      }
    }

    startService<ScheduleService>()
    startActivity<ScheduleActivity>()
    finish()
  }
}
