package ru.erdenian.studentassistant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.erdenian.studentassistant.R;
import ru.erdenian.studentassistant.classes.UniversitySelectionListItem;

/**
 * Created by Erdenian on 17.07.2016.
 */

public class UniversitySelectionListAdapter extends BaseAdapter {

    LayoutInflater lInflater;

    ArrayList<UniversitySelectionListItem> list;

    public UniversitySelectionListAdapter(Context context, ArrayList<UniversitySelectionListItem> list) {
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list != null)
            return list.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null)
            view = lInflater.inflate(R.layout.list_item_university_selection, parent, false);

        UniversitySelectionListItem listItem = list.get(position);

        ((TextView) view.findViewById(R.id.lius_title)).setText(listItem.getTitle());

        TextView tvSubtitle = (TextView) view.findViewById(R.id.lius_subtitle);
        if (listItem.getSubtitle() != null)
            tvSubtitle.setText(listItem.getSubtitle());
        else
            tvSubtitle.setHeight(0);

        return view;
    }

    ;
}