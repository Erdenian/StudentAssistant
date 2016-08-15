package ru.erdenian.studentassistant.classes;

import java.util.ArrayList;

/**
 * Created by Erdenian on 01.08.2016.
 * Todo: описание класса
 */

public class UniversitySelectionListItem {

    public static final int TYPE_UNIVERSITY = 0,
            TYPE_SCHOOL = 1,
            TYPE_FACULTY = 2,
            TYPE_YEAR = 3,
            TYPE_GROUP = 4,
            TYPE_CLASS = 5;

    int type;
    String title, subtitle, id;
    ArrayList<UniversitySelectionListItem> inner;

    public UniversitySelectionListItem(String name, String fullname, String id) {
        this.title = name;
        this.subtitle = fullname;
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getId() {
        return id;
    }

    public ArrayList<UniversitySelectionListItem> getInner() {
        return inner;
    }

    @Override
    public String toString() {
        return title;
    }
}
