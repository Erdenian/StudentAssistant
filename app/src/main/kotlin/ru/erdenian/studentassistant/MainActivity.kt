package ru.erdenian.studentassistant

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
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
            val isDarkTheme = isSystemInDarkTheme()
            LaunchedEffect(Unit) {
                val mainComponent = MainComponentHolder.instance
                val analytics = mainComponent.analyticsApi.analytics

                analytics.setUserProperty("theme", if (isDarkTheme) "dark" else "light")
                analytics.setUserProperty(
                    name = "is_advanced_weeks_selector_enabled",
                    value = mainComponent.repositoryApi.settingsRepository.isAdvancedWeeksSelectorEnabled.toString(),
                )

                mainComponent.repositoryApi.selectedSemesterRepository.await()

                analytics.logEvent("app_opened")
                showSplashScreen = false
            }

            AppTheme(isDarkTheme = isDarkTheme) { StudentAssistantApp() }
        }
    }
}
