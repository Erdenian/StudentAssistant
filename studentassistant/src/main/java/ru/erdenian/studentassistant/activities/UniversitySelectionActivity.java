package ru.erdenian.studentassistant.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextSwitcher;
import android.widget.ViewSwitcher;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.File;
import java.util.ArrayList;

import ru.erdenian.studentassistant.R;
import ru.erdenian.studentassistant.adapters.UniversitySelectionListAdapter;
import ru.erdenian.studentassistant.classes.UniversitySelectionListItem;
import ru.erdenian.studentassistant.classes.Utils;
import ru.erdenian.studentassistant.constants.ServerConstants;
import ru.erdenian.studentassistant.constants.SharedPreferencesConstants;

/**
 * Created by Erdenian on 18.07.2016.
 */

public class UniversitySelectionActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener,
        MaterialSearchView.OnQueryTextListener,
        MaterialSearchView.SearchViewListener,
        View.OnClickListener,
        FutureCallback<JsonArray> {

    final String JSON = "json4",
            CURRENT_STATE = "current_state",
            SELECTED_ITEM = "selected_item",
            INDEX = "index",
            TOP = "top";

    int translationLengthToShowBackButton, screenWidth;

    LinearLayout llToolbarBackAndTitle;
    ImageButton ibBack;
    TextSwitcher tvTitle;
    MaterialSearchView materialSearchView;
    LinearLayout progressBar;
    ListView[] listViews = new ListView[2];

    InputMethodManager imm;

    String json;
    int currentState = 0;

    ArrayList<UniversitySelectionListItem> universities, currentList;
    ArrayList<Integer> selectedItems = new ArrayList<>();
    ArrayList<String> groupId = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_selection);

        translationLengthToShowBackButton = Utils.dpToPx(this,
                (int) (-getResources().getDimension(R.dimen.toolbar_margin_start_to_hide_button) /
                        getResources().getDisplayMetrics().density));
        screenWidth = getResources().getDisplayMetrics().widthPixels;

        setSupportActionBar((Toolbar) findViewById(R.id.tus_toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        llToolbarBackAndTitle = (LinearLayout) findViewById(R.id.tus_back_and_title);

        ibBack = (ImageButton) findViewById(R.id.tus_back);
        ibBack.setEnabled(false);
        ibBack.setOnClickListener(this);

        tvTitle = (TextSwitcher) findViewById(R.id.textview_toolbar_title);
        tvTitle.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        tvTitle.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        tvTitle.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.textview_toolbar_title, null);
            }
        });
        tvTitle.setCurrentText(getString(R.string.universities));

        progressBar = (LinearLayout) findViewById(R.id.pb_progress);

        listViews[0] = (ListView) findViewById(R.id.cus_list1);
        listViews[0].setEnabled(false);
        listViews[0].setOnItemClickListener(this);

        listViews[1] = (ListView) findViewById(R.id.cus_list2);
        listViews[1].setX(screenWidth);
        listViews[1].setEnabled(false);
        listViews[1].setOnItemClickListener(this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // Todo: разобраться с MaterialSearchView
        materialSearchView = (MaterialSearchView) findViewById(R.id.tus_search_view);
        materialSearchView.setHint(getString(R.string.search_hint));
        materialSearchView.setOnQueryTextListener(this);
        materialSearchView.setOnSearchViewListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putString(JSON, json);
        bundle.putInt(CURRENT_STATE, currentState);
        for (int i = 0; i < currentState; i++)
            bundle.putInt(SELECTED_ITEM + i, selectedItems.get(i));

        int index = listViews[0].getFirstVisiblePosition();
        View v = listViews[0].getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - listViews[0].getPaddingTop());
        bundle.putInt(INDEX, index);
        bundle.putInt(TOP, top);

        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        json = bundle.getString(JSON);
        universities = new Gson().fromJson(
                json,
                new TypeToken<ArrayList<UniversitySelectionListItem>>() {
                }.getType());
        currentList = universities;

        currentState = bundle.getInt(CURRENT_STATE);
        for (int i = 0; i < currentState; i++) {
            int selectedItem = bundle.getInt(SELECTED_ITEM + i);
            selectedItems.add(selectedItem);
            groupId.add("/" + currentList.get(selectedItem).getId());
            currentList = currentList.get(selectedItem).getInner();
        }

        tvTitle.setCurrentText(getTitleString());

        if (currentState > 0) {
            llToolbarBackAndTitle.setX(translationLengthToShowBackButton);
            ibBack.setEnabled(true);
        }

        listViews[0].setAdapter(new UniversitySelectionListAdapter(this, currentList));
        progressBar.setVisibility(View.GONE);
        listViews[0].setEnabled(true);

        listViews[0].setSelectionFromTop(bundle.getInt(INDEX), bundle.getInt(TOP));

        super.onRestoreInstanceState(bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (universities == null)
            Ion.with(this)
                    .load(ServerConstants.SERVER_URL +
                            ServerConstants.ROOT_FOLDER +
                            ServerConstants.UNIVERSITIES_FILE_NAME)
                    .asJsonArray()
                    .setCallback(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_university_selection, menu);

        MenuItem miSearch = menu.findItem(R.id.mus_search);
        materialSearchView.setMenuItem(miSearch);
        miSearch.setVisible(((currentList != null) && (currentList.size() > 1)));

        return true;
    }

    @Override
    public void onCompleted(Exception e, JsonArray result) {
        if (e != null) {
            // Todo: сообщение об ошибке
            return;
        }

        json = result.toString();

        universities = new Gson().fromJson(
                result,
                new TypeToken<ArrayList<UniversitySelectionListItem>>() {
                }.getType());
        currentList = universities;

        tvTitle.setCurrentText(getTitleString());

        listViews[0].setAdapter(new UniversitySelectionListAdapter(getApplicationContext(),
                currentList));
        progressBar.animate().alpha(0);
        listViews[0].setEnabled(true);
        listViews[0].setAlpha(0);
        listViews[0].animate().alpha(1);

        invalidateOptionsMenu();
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        listViews[0].setAdapter(new UniversitySelectionListAdapter(this,
                Utils.search(currentList, newText)));
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        View view = getCurrentFocus();
        if (view != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        return true;
    }

    @Override
    public void onSearchViewShown() {
    }

    @Override
    public void onSearchViewClosed() {
        listViews[0].setAdapter(new UniversitySelectionListAdapter(this, currentList));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currentState++;
        selectedItems.add(position);
        groupId.add(currentList.get(position).getId());
        currentList = currentList.get(position).getInner();

        if (currentList == null) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < currentState; i++)
                buffer.append("/" + groupId.get(i));

            File jsonFolder = new File(getFilesDir().getAbsolutePath() + "/json");
            File[] filesList = jsonFolder.listFiles();
            if (filesList != null)
                for (File f : filesList)
                    f.delete();
            jsonFolder.delete();

            SharedPreferences.Editor ed =
                    PreferenceManager.getDefaultSharedPreferences(this).edit();
            ed.putString(SharedPreferencesConstants.GROUP_ID, buffer.toString());
            ed.apply();

            Intent intent = new Intent(this, ScheduleActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        tvTitle.setText(getTitleString());
        if (currentState == 1) {
            llToolbarBackAndTitle.animate().translationX(translationLengthToShowBackButton);
            ibBack.setEnabled(true);
        }
        materialSearchView.closeSearch();

        listViews[0].animate().translationX(-screenWidth);
        listViews[0].setEnabled(false);

        listViews[1].setAdapter(new UniversitySelectionListAdapter(this, currentList));
        listViews[1].setX(screenWidth);
        listViews[1].animate().translationX(0);
        listViews[1].setEnabled(true);

        ListView tmp = listViews[0];
        listViews[0] = listViews[1];
        listViews[1] = tmp;

        invalidateOptionsMenu();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tus_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (materialSearchView.isSearchOpen())
            materialSearchView.closeSearch();
        else if (currentState == 0) {
            super.onBackPressed();
        } else {
            currentState--;
            selectedItems.remove(currentState);

            groupId.clear();
            currentList = universities;
            for (int i = 0; i < currentState; i++) {
                groupId.add("/" + currentList.get(selectedItems.get(i)).getId());
                currentList = currentList.get(selectedItems.get(i)).getInner();
            }

            tvTitle.setText(getTitleString());
            if (currentState == 0) {
                llToolbarBackAndTitle.animate().translationX(0);
                ibBack.setEnabled(false);
            }

            listViews[0].animate().translationX(screenWidth);
            listViews[0].setEnabled(false);

            listViews[1].setAdapter(new UniversitySelectionListAdapter(this, currentList));
            listViews[1].setX(-screenWidth);
            listViews[1].animate().translationX(0);
            listViews[1].setEnabled(true);

            ListView tmp = listViews[0];
            listViews[0] = listViews[1];
            listViews[1] = tmp;

            invalidateOptionsMenu();
        }
    }

    private String getTitleString() {
        int type = currentList.get(0).getType();
        switch (type) {
            case UniversitySelectionListItem.TYPE_UNIVERSITY:
                return getString(R.string.universities);
            case UniversitySelectionListItem.TYPE_FACULTY:
                return getString(R.string.faculties);
            case UniversitySelectionListItem.TYPE_GROUP:
                return getString(R.string.groups);
        }
        return null;
    }
}
