package ru.erdenian.studentassistant

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import ru.erdenian.studentassistant.di.MainComponentHolder
import ru.erdenian.studentassistant.style.AppTheme

internal class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var showSplashScreen by mutableStateOf(true)
        installSplashScreen().setKeepOnScreenCondition { showSplashScreen }

        setContent {
            LaunchedEffect(Unit) {
                val mainComponent = MainComponentHolder.instance
                mainComponent.repositoryApi.selectedSemesterRepository.await()
                mainComponent.analyticsApi.analytics.logEvent("app_open")
                showSplashScreen = false
            }

            AppTheme { StudentAssistantApp() }
        }
    }
}
