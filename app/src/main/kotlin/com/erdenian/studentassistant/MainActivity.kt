package com.erdenian.studentassistant

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.erdenian.studentassistant.style.AppTheme
import com.erdenian.studentassistant.utils.findMainComponent

internal class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var showSplashScreen by mutableStateOf(true)
        installSplashScreen().setKeepOnScreenCondition { showSplashScreen }

        WindowCompat.setDecorFitsSystemWindows(window, false) // To make insets work

        setContent {
            LaunchedEffect(Unit) {
                val selectedSemesterRepository = findMainComponent().selectedSemesterRepository()
                selectedSemesterRepository.await()
                showSplashScreen = false
            }

            AppTheme {
                // Temporarily disable this check to workaround a bug with insets
                // https://issuetracker.google.com/issues/228588441
                @Suppress("SimplifyBooleanWithConstants")
                if (!showSplashScreen || true) {
                    StudentAssistantApp()

                    val view = LocalView.current
                    LaunchedEffect(view) {
                        // Scrollable composables don't keep focused view in view properly if insets are changing with animation
                        // so we disable animations for now with this little hack.
                        // See also:
                        // androidx.compose.foundation.layout.WindowInsetsHolder.insetsListener
                        // androidx.compose.foundation.layout.WindowInsetsHolder.incrementConsumers
                        // https://android-review.googlesource.com/c/platform/frameworks/support/+/1965577/19/compose/foundation/foundation/src/commonMain/kotlin/androidx/compose/foundation/gestures/Scrollable.kt#566
                        // https://issuetracker.google.com/issues/220119990
                        // https://issuetracker.google.com/issues/217769672
                        ViewCompat.setWindowInsetsAnimationCallback(view, null)
                    }
                }
            }
        }
    }
}
