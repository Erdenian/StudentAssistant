package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import com.fatboyindustrial.gsonjodatime.Converters
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSortedSet
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.content_login.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.toast
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.netty.*
import ru.erdenian.studentassistant.schedule.Lesson
import ru.erdenian.studentassistant.schedule.LessonRepeat
import ru.erdenian.studentassistant.schedule.ScheduleManager
import ru.erdenian.studentassistant.schedule.Semester


class LoginActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    setSupportActionBar(toolbar)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    content_login_login_edit_text.addTextChangedListener(object : TextWatcher {

      override fun afterTextChanged(s: Editable?) {
        if (!s.isNullOrBlank())
          nettyQuery(";::checklogin::${s!!.trim()}") {
            runOnUiThread {
              if (it.toBoolean()) {
                content_login_login.isErrorEnabled = true
                content_login_login.error = "Логин занят"
              } else {
                content_login_login.isErrorEnabled = false
              }
            }
          }
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    })

    content_login_sign_in.setOnClickListener {
      val login = content_login_login_edit_text.text.trim().toString()
      val password = content_login_password_edit_text.text.toString()

      nettyQuery(";::checkuser::$login;$password") {
        if (it.toBoolean()) {
          defaultSharedPreferences.edit().apply {
            putString("login", login)
            putString("password", password)
          }.commit()

          nettyQuery("$login;$password::getuser::") {
            val user = Gson().fromJson(it, User::class.java)

            nettyQuery("$login;$password::getschedule::${user.groupId}") {
              val args = it.split(';')
              val semesterJson = args[0]

              Log.d("tag", "1")
              val gson = Converters.registerAll(GsonBuilder())
                  .registerTypeAdapter(ImmutableSortedSet::class.java, ImmutableSortedSetDeserializer())
                  .registerTypeAdapter(ImmutableList::class.java, ImmutableListDeserializer())
                  .registerTypeAdapter(LessonRepeat::class.java, LessonRepeatDeserializer())
                  .create()

              Log.d("tag", "2")
              val semester = gson.fromJson(semesterJson, Semester::class.java)
              val semester1 = Semester(semester.name, semester.firstDay, semester.lastDay, semester.id)
              Log.d("tag", "3")
              ScheduleManager.removeSemester(semester1.id)
              Log.d("tag", "4")
              ScheduleManager.addSemester(semester1)
              Log.d("tag", "5")

              for (i in 1..(args.size - 1)) {
                ScheduleManager.addLesson(semester.id, gson.fromJson(args[i], Lesson::class.java))
              }
            }

            nettyQuery("$login;$password::getuniversity::${user.universityId}") {
              defaultSharedPreferences.edit().apply {
                putString("university", it)
              }.apply()
            }

            nettyQuery("$login;$password::getfaculty::${user.facultyId}") {
              defaultSharedPreferences.edit().apply {
                putString("faculty", it)
              }.apply()
            }

            nettyQuery("$login;$password::getgroup::${user.groupId}") {
              defaultSharedPreferences.edit().apply {
                putString("group", it)
              }.apply()
            }
          }
          finish()
        } else {
          toast("Неверный логин или пароль")
        }
      }
    }

    content_login_sign_up.setOnClickListener {
      if (content_login_login_edit_text.text.isNullOrBlank()) {
        toast("Введите логин")
      } else if (content_login_password_edit_text.text.isNullOrBlank()) {
        toast("Введите пароль")
      } else {
        nettyQuery(";::registration::${content_login_login_edit_text.text};${content_login_password_edit_text.text}") {
          defaultSharedPreferences.edit().apply {
            putString("login", content_login_login_edit_text.text.trim().toString())
            putString("password", content_login_password_edit_text.text.toString())
          }.commit()
        }
        finish()
      }
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> finish()
      else -> throw IllegalArgumentException("Неизвестный id: ${item.itemId}")
    }
    return super.onOptionsItemSelected(item)
  }
}
