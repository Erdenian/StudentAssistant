package ru.erdenian.studentassistant.service

import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.activity.AlarmActivity
import ru.erdenian.studentassistant.schedule.ScheduleManager

class ScheduleService : IntentService("ScheduleService") {

    override fun onHandleIntent(intent: Intent?) {
        if (!ScheduleManager.hasLessons) return

        var alarmTime: LocalTime? = null

        ScheduleManager.semesters.forEach {
            ScheduleManager.getLessons(it.id, LocalDate.now()).forEach {
                if ((alarmTime == null) || (it.startTime.isBefore(alarmTime!!)))
                    alarmTime = it.startTime
            }
        }

        if (LocalTime.now().isBefore(alarmTime!!)) {
            val intent1 = Intent(this, AlarmActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0)

            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= 19)
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime!!.minusHours(1).toDateTimeToday().millis, pendingIntent)
            else alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime!!.minusHours(1).toDateTimeToday().millis, pendingIntent)
        }
    }
}
