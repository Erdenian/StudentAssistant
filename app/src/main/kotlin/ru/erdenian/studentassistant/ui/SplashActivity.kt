package ru.erdenian.studentassistant.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startService
import ru.erdenian.studentassistant.service.ScheduleService
import ru.erdenian.studentassistant.ui.schedule.ScheduleActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startService<ScheduleService>()
        startActivity<ScheduleActivity>()
        finish()
    }
}
