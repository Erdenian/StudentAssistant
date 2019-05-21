package ru.erdenian.studentassistant.extensions

import android.app.Activity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import kotlinx.android.synthetic.main.navigation_view.navigation_view
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.activity.AlarmEditorActivity
import ru.erdenian.studentassistant.activity.HelpActivity
import ru.erdenian.studentassistant.activity.HomeworksActivity
import ru.erdenian.studentassistant.localdata.ScheduleManager
import ru.erdenian.studentassistant.ui.schedule.ScheduleActivity

/**
 * Инициализирует Navigation Drawer.
 *
 * @param toolbar тулбар
 * @since 0.0.0
 * @author Ilya Solovyev
 */
fun Activity.initializeDrawerAndNavigationView(toolbar: Toolbar) {
    val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

    ActionBarDrawerToggle(
        this,
        drawerLayout,
        toolbar,
        R.string.navigation_drawer_open,
        R.string.navigation_drawer_close
    ).apply {
        drawerLayout.addDrawerListener(this)
        syncState()
    }

    ScheduleManager.hasLessons.let {
        navigation_view.menu.findItem(R.id.nav_homeworks).isEnabled = it
        navigation_view.menu.findItem(R.id.nav_alarm).isEnabled = it
    }

    navigation_view.setCheckedItem(
        when (this) {
            is ScheduleActivity -> R.id.nav_schedule
            is HomeworksActivity -> R.id.nav_homeworks
            is AlarmEditorActivity -> R.id.nav_alarm
            else -> throw IllegalStateException("Неизвестное Activity: ${this.javaClass.name}")
        }
    )

    navigation_view.setNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.nav_schedule -> if (this !is ScheduleActivity) {
                startActivity<ScheduleActivity>()
                finish()
            }
            R.id.nav_homeworks -> if (this !is HomeworksActivity) {
                startActivity<HomeworksActivity>()
                finish()
            }
            R.id.nav_alarm -> if (this !is AlarmEditorActivity) {
                startActivity<AlarmEditorActivity>()
                finish()
            }
            R.id.nav_settings -> toast(R.string.nav_settings)
            R.id.nav_help -> startActivity<HelpActivity>()
            else -> throw IllegalArgumentException("Неизвестный id: ${it.itemId}")
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        true
    }
}
