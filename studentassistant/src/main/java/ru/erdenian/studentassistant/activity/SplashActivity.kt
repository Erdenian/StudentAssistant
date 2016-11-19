package ru.erdenian.studentassistant.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ru.erdenian.studentassistant.ulils.FileUtils

/**
 * Activity, открывающееся при запуске приложения.
 * Показывает картинку на весь экран, пока подгружается следующее Activity.
 *
 * @author Ilya Solovyev
 * @version 0.0.0
 * @since 0.0.0
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FileUtils.initialize(this)

        startActivity(Intent(this, ScheduleActivity::class.java))
        finish()
    }
}
