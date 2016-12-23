package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startService
import ru.erdenian.studentassistant.schedule.ScheduleManager
import ru.erdenian.studentassistant.service.ScheduleService

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ScheduleManager.initialize(applicationContext)

        startService<ScheduleService>()
        startActivity<ScheduleActivity>()
        finish()
    }
}
