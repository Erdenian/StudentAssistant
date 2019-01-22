package ru.erdenian.studentassistant.service

import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.preference.PreferenceManager
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.activity.AlarmActivity
import ru.erdenian.studentassistant.localdata.ScheduleManager

class ScheduleService : IntentService("ScheduleService") {

  override fun onHandleIntent(intent: Intent?) {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent1 = Intent(this, AlarmActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0)

    if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("on", false)) {
      alarmManager.cancel(pendingIntent)
      return
    }

    if (!ScheduleManager.hasLessons) {
      alarmManager.cancel(pendingIntent)
      return
    }

    var alarmTime: LocalTime? = null

    ScheduleManager.semesters.forEach {
      ScheduleManager.getLessons(it.id, LocalDate.now()).forEach {
        if ((alarmTime == null) || (it.startTime.isBefore(alarmTime!!)))
          alarmTime = it.startTime
      }
    }

    if (alarmTime == null) {
      alarmManager.cancel(pendingIntent)
      return
    }

    val time = PreferenceManager.getDefaultSharedPreferences(this).getString("time", "01:00:00.000")

    alarmTime = alarmTime!!.minusHours(LocalTime(time).hourOfDay).minusMinutes(LocalTime(time).minuteOfHour)

    if (LocalTime.now().isBefore(alarmTime!!)) {
      if (Build.VERSION.SDK_INT >= 19)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
            alarmTime!!.toDateTimeToday().millis, pendingIntent)
      else alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime!!.toDateTimeToday().millis, pendingIntent)
    }
  }
}
