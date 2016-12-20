package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.startActivity
import ru.erdenian.studentassistant.schedule.ScheduleManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ScheduleManager.initialize(applicationContext)

        startActivity<ScheduleActivity>()
        finish()
    }
}
