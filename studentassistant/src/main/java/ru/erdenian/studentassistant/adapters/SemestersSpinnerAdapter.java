package ru.erdenian.studentassistant.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ru.erdenian.studentassistant.R;
import ru.erdenian.studentassistant.classes.Semester;
import ru.erdenian.studentassistant.helpers.SemestersHelper;

/**
 * Created by Erdenian on 28.07.2016.
 * Todo: описание класса
 */

public class SemestersSpinnerAdapter extends BaseAdapter {

    LayoutInflater inflater;

    public SemestersSpinnerAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Todo: повторное использование view
        @SuppressLint("ViewHolder") View view =
                inflater.inflate(R.layout.spinner_semesters, parent, false);

        Semester selectedSemester = SemestersHelper.getSemesters().get(position);
        TextView tvTitle = (TextView) view.findViewById(R.id.ss_title);
        if (selectedSemester != null) {
            tvTitle.setText(selectedSemester.getName());
        } else {
            tvTitle.setText(R.string.today);
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.spinner_dropdown_item_semesters, parent, false);

        Semester semester = SemestersHelper.getSemesters().get(position);
        TextView tvTitle = (TextView) view.findViewById(R.id.sdis_title);
        if (semester != null) {
            tvTitle.setText(semester.getName());
        } else {
            tvTitle.setText(R.string.today);
        }
        return view;
    }

    @Override
    public int getCount() {
        return SemestersHelper.getSemesters().size();
    }

    @Override
    public Object getItem(int position) {
        return SemestersHelper.getSemesters().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getDefaultPosition() {
        return SemestersHelper.getCurrentSemesterIndex();
    }
}
