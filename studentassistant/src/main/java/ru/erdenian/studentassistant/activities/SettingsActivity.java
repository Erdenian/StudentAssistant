package ru.erdenian.studentassistant.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;

import ru.erdenian.studentassistant.R;

/**
 * Created by Erdenian on 29.07.2016.
 * Todo: описание класса
 */

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new MainPreferenceFragment()).commit();

        AppCompatDelegate delegate = AppCompatDelegate.create(this, null);

        if (delegate.getSupportActionBar() != null)
            delegate.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /*@Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MainPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            setHasOptionsMenu(true);
        }
    }

    /*@Override
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || MainPreferenceFragment.class.getName().equals(fragmentName);
    }*/
}
