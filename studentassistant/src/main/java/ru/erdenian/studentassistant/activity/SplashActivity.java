package ru.erdenian.studentassistant.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.erdenian.studentassistant.ulils.FileUtils;

/**
 * Activity, открывающееся при запуске приложения.
 * Показывает картинку на весь экран, пока подгружается следующее Activity.
 *
 * @author Ilya Solovyev
 * @version 0.0.0
 * @since 0.0.0
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FileUtils.initialize(this);

        /*Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();*/
    }
}
