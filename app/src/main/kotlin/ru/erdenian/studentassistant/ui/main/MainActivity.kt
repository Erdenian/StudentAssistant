package ru.erdenian.studentassistant.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navigationView.setOnNavigationItemSelectedListener { item ->
            // setupWithNavController добавляет анимацию, поэтому Toolbar начинает мерцать при переходе
            findNavController(R.id.nav_host_fragment).navigate(item.itemId)
            true
        }
    }
}
