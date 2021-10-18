package ru.erdenian.studentassistant.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import ru.erdenian.studentassistant.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<BottomNavigationView>(R.id.navigation_view).apply {
            setOnItemSelectedListener { item ->
                // setupWithNavController добавляет анимацию, поэтому Toolbar начинает мерцать при переходе
                findNavController(R.id.nav_host_fragment).navigate(item.itemId)
                true
            }
            KeyboardVisibilityEvent.setEventListener(this@MainActivity) { isOpen ->
                visibility = if (isOpen) View.GONE else View.VISIBLE
            }
        }
    }
}
