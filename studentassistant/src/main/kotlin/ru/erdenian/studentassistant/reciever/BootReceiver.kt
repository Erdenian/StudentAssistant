package ru.erdenian.studentassistant.reciever

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.jetbrains.anko.startService
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.service.ScheduleService

class BootReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent1 = Intent(context, ScheduleService::class.java)
    val pendingIntent = PendingIntent.getService(context, 0, intent1, 0)

    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
        LocalDate.now().plusDays(1).toLocalDateTime(LocalTime(0, 0, 1)).toDateTime().millis,
        AlarmManager.INTERVAL_DAY, pendingIntent)

    context.startService<ScheduleService>()
  }
}
