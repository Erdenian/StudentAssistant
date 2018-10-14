package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_alarm_editor.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startService
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.initializeDrawerAndNavigationView
import ru.erdenian.studentassistant.extensions.showTimePicker
import ru.erdenian.studentassistant.service.ScheduleService

class AlarmEditorActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_alarm_editor)

    setSupportActionBar(toolbar)
    initializeDrawerAndNavigationView(toolbar)

    val sp = PreferenceManager.getDefaultSharedPreferences(this)
    val timeString = sp.getString("time", null) ?: run {
      val s = "01:00:00.000"
      sp.edit().putString("time", s).apply()
      s
    }

    if (timeString.isNotBlank()) content_alarm_editor_time.text = LocalTime(timeString).toString("HH:mm")

    content_alarm_editor_on_off.isChecked = sp.getBoolean("on", false)

    content_alarm_editor_time.setOnClickListener { _ ->
      showTimePicker(LocalTime(1, 0)) { newTime ->
        sp.edit().putString("time", newTime.toString()).apply()
        content_alarm_editor_time.text = newTime.toString("HH:mm")
        startService<ScheduleService>()
      }
    }

    content_alarm_editor_on_off.setOnCheckedChangeListener { _, isChecked ->
      sp.edit().putBoolean("on", isChecked).apply()
      startService<ScheduleService>()
    }
  }
}
