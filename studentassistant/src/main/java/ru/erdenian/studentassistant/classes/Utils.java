package ru.erdenian.studentassistant.classes;

import android.content.Context;
import android.util.DisplayMetrics;

import java.util.ArrayList;

/**
 * Created by Erdenian on 18.07.2016.
 */

public final class Utils {

    /**
     * Преобразует dp в px
     *
     * @param context контекст
     * @param dp      значение в dp
     * @return значение в px
     */
    public static int dpToPx(Context context, int dp) {
        return Math.round(dp * (context.getResources().getDisplayMetrics().xdpi /
                DisplayMetrics.DENSITY_DEFAULT));
    }

    /**
     * Преобразует px в dp
     *
     * @param context контекст
     * @param px      значение в px
     * @return значение в dp
     */
    public static int pxToDp(Context context, int px) {
        return Math.round(px / (context.getResources().getDisplayMetrics().xdpi /
                DisplayMetrics.DENSITY_DEFAULT));
    }

    /**
     * Поиск по списку
     *
     * @param list  список
     * @param query запрос
     * @param <T>   тип списка
     * @return список того же типа с найденными элементами
     */
    public static <T> ArrayList<T> search(ArrayList<T> list, String query) {
        // Todo: нормальный поиск
        if ((query == null) || (query.length() == 0))
            return list;

        ArrayList<T> result = new ArrayList<>();

        for (int i = 0; i < list.size(); i++)
            if (list.get(i).toString().toLowerCase().contains(query.toLowerCase()))
                result.add(list.get(i));

        return result;
    }
}
