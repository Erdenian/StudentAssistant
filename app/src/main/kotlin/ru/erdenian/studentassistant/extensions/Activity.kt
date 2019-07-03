package ru.erdenian.studentassistant.extensions

import android.app.Activity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.ui.help.HelpActivity
import ru.erdenian.studentassistant.ui.homeworks.HomeworksActivity
import ru.erdenian.studentassistant.ui.schedule.ScheduleActivity

/**
 * Инициализирует Navigation Drawer.
 *
 * @param toolbar тулбар
 * @since 0.0.0
 * @author Ilya Solovyev
 */
fun Activity.initializeDrawerAndNavigationView(toolbar: Toolbar, drawerLayout: DrawerLayout) {
    val navigationView = findViewById<NavigationView>(R.id.navigation_view)

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

    /*ScheduleRepository(applicationContext).hasLessons().let {
        navigationView.menu.findItem(R.id.nav_homeworks).isEnabled = it
        navigationView.menu.findItem(R.id.nav_alarm).isEnabled = it
    }*/

    navigationView.setCheckedItem(
        when (this) {
            is ScheduleActivity -> R.id.nav_schedule
            is HomeworksActivity -> R.id.nav_homeworks
            else -> throw IllegalStateException("Неизвестное Activity: ${this.javaClass.name}")
        }
    )

    navigationView.setNavigationItemSelectedListener { menuItem ->
        drawerLayout.closeDrawer(GravityCompat.START)
        when (menuItem.itemId) {
            R.id.nav_schedule -> {
                if (this !is ScheduleActivity) {
                    startActivity<ScheduleActivity>()
                    finish()
                }
                true
            }
            R.id.nav_homeworks -> {
                if (this !is HomeworksActivity) {
                    startActivity<HomeworksActivity>()
                    finish()
                }
                true
            }
            R.id.nav_alarm -> {
                toast("AlarmEditorActivity")
                true
            }
            R.id.nav_settings -> {
                toast(R.string.nav_settings)
                true
            }
            R.id.nav_help -> {
                startActivity<HelpActivity>()
                true
            }
            else -> false
        }
    }
}
