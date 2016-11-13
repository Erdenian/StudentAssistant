package ru.erdenian.studentassistant.ulils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;

import org.joda.time.LocalDate;

import java.util.Calendar;

import ru.erdenian.studentassistant.R;
import ru.erdenian.studentassistant.activity.ScheduleActivity;

/**
 * Todo: описание класса.
 *
 * @author Ilya Solovyev
 * @version 0.0.0
 * @since 0.0.0
 */
public class UiUtils {

    /**
     * Инициализирует Navigation Bar.
     *
     * @param currentActivity Activity, в котором выполняется инициализация
     * @param drawerId        id DrawerLayout'а
     * @param toolbar         тулбар
     * @param resources       ресурсы
     * @return инициализированный DrawerLayout
     * @since 0.0.0
     */
    public static DrawerLayout initializeDrawerAndNavigationView(final Activity currentActivity,
                                                                 int drawerId, Toolbar toolbar,
                                                                 final Resources resources) {
        View view = currentActivity.getWindow().getDecorView();

        final DrawerLayout drawerLayout = (DrawerLayout) view.findViewById(drawerId);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                currentActivity, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) view.findViewById(R.id.navigation_view);
        navigationView.setCheckedItem(R.id.nav_schedule);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_schedule:
                        Toast.makeText(currentActivity, resources.getText(R.string.nav_schedule), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_homework:
                        Toast.makeText(currentActivity, resources.getText(R.string.nav_homework), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_alarm:
                        Toast.makeText(currentActivity, resources.getText(R.string.nav_alarm), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_settings:
                        Toast.makeText(currentActivity, resources.getText(R.string.nav_settings), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_help:
                        Toast.makeText(currentActivity, resources.getText(R.string.nav_help), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Log.wtf(this.getClass().getName(), "Неизвестный id: " + item.getItemId());
                        break;
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        if (currentActivity instanceof ScheduleActivity) {
            navigationView.setCheckedItem(R.id.nav_schedule);
        }

        return drawerLayout;
    }

    /**
     * Окрашивает иконки в тулбаре в нужный цвет.
     *
     * @param context контекст
     * @param menu    меню, в котором нужно окрасить иконки
     * @since 0.0.0
     */
    public static void colorMenu(Context context, Menu menu) {
        int color = ContextCompat.getColor(context, R.color.action_bar_icons_color);

        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    public static void showDatePicker(LocalDate firstDay, LocalDate lastDay, LocalDate preselected, FragmentManager fragmentManager,
                                      CalendarDatePickerDialogFragment.OnDateSetListener onDateSetListener) {
        showDatePicker(firstDay, lastDay, preselected, fragmentManager, onDateSetListener, "date_picker_tag");
    }

    public static void showDatePicker(LocalDate firstDay, LocalDate lastDay, LocalDate preselected, FragmentManager fragmentManager,
                                      CalendarDatePickerDialogFragment.OnDateSetListener onDateSetListener, String tag) {
        Calendar first = Calendar.getInstance();
        first.setTime(firstDay.toDate());
        Calendar last = Calendar.getInstance();
        last.setTime(lastDay.toDate());

        MonthAdapter.CalendarDay startDate = new MonthAdapter.CalendarDay(first);
        MonthAdapter.CalendarDay endDate = new MonthAdapter.CalendarDay(last);

        new CalendarDatePickerDialogFragment()
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setDateRange(startDate, endDate)
                .setThemeCustom(R.style.DatePicker)
                .setOnDateSetListener(onDateSetListener)
                .show(fragmentManager, "date_picker");
    }
}
