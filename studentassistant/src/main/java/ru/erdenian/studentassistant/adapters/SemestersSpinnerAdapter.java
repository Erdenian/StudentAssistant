package ru.erdenian.studentassistant.adapters;

import android.content.Context;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.erdenian.studentassistant.R;
import ru.erdenian.studentassistant.classes.Semester;

/**
 * Created by Erdenian on 28.07.2016.
 */

public class SemestersSpinnerAdapter extends BaseAdapter {

    ThemedSpinnerAdapter.Helper dropDownHelper;
    LayoutInflater inflater;
    ArrayList<Semester> semesters;

    public SemestersSpinnerAdapter(Context context, ArrayList<Semester> semesters) {
        this.semesters = semesters;
        dropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.spinner_semesters, parent, false);
        TextView tvTitle = (TextView) view.findViewById(R.id.ss_title);
        if (semesters.get(position) != null) {
            tvTitle.setText(semesters.get(position).getName());
        } else {
            tvTitle.setText(R.string.today);
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.spinner_dropdown_item_semesters, parent, false);
        TextView tvTitle = (TextView) view.findViewById(R.id.sdis_title);
        if (semesters.get(position) != null) {
            tvTitle.setText(semesters.get(position).getName());
        } else {
            tvTitle.setText(R.string.today);
        }
        return view;
    }

    @Override
    public int getCount() {
        return semesters.size();
    }

    @Override
    public Object getItem(int position) {
        return semesters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
