package ru.erdenian.studentassistant.activity

import android.media.RingtoneManager
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_alarm.*
import ru.erdenian.studentassistant.R

class AlarmActivity : AppCompatActivity() {

    private val ringtone by lazy {
        RingtoneManager.getRingtone(
            this,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        content_alarm_turn_off.setOnClickListener {
            ringtone.stop()
            finish()
        }

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        ringtone.play()
    }

    override fun onUserLeaveHint() {
        ringtone.stop()
        finish()
        super.onUserLeaveHint()
    }

    override fun onBackPressed() = Unit
}
