package com.erdenian.studentassistant

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.erdenian.studentassistant.mediator.mediators.RepositoryMediator
import com.erdenian.studentassistant.style.AppTheme

internal class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var showSplashScreen by mutableStateOf(true)
        installSplashScreen().setKeepOnScreenCondition { showSplashScreen }

        WindowCompat.setDecorFitsSystemWindows(window, false) // To make insets work

        setContent {
            LaunchedEffect(Unit) {
                RepositoryMediator.api.selectedSemesterRepository.await()
                showSplashScreen = false
            }

            AppTheme { StudentAssistantApp() }
        }
    }
}
