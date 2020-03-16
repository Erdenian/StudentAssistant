package ru.erdenian.studentassistant.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.erdenian.studentassistant.ui.main.MainActivity
import ru.erdenian.studentassistant.utils.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity<MainActivity>()
        finish()
    }
}
