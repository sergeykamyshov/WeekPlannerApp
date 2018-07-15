package ru.sergeykamyshov.weekplanner.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ru.sergeykamyshov.weekplanner.model.Task;

public class SharedPreferencesUtils {

    public static final String PREFS_NAME = "ru.sergeykamyshov.weekplanner.utils.SharedPreferencesUtils";
    public static final String CARD_ID_PREF = "cardId";
    public static final String TASK_TITLE_PREF = "taskTitle";
    public static final String TASK_IS_DONE_PREF = "taskIsDone";
    public static final String TASK_POSITION_PREF = "taskPosition";
    public static final String CARD_WEEK_END_DATE_PREF = "endWeekDate";

    public static void saveTaskData(Context context, String cardId, Task task, int position) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CARD_ID_PREF, cardId);
        editor.putString(TASK_TITLE_PREF, task.getTitle());
        editor.putBoolean(TASK_IS_DONE_PREF, task.isDone());
        editor.putInt(TASK_POSITION_PREF, position);
        editor.apply();
    }

    public static boolean hasTaskData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String cardId = prefs.getString(CARD_ID_PREF, null);
        return cardId != null;
    }

    public static Map<String, Object> getTaskData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        HashMap<String, Object> data = new HashMap<>();
        data.put(CARD_ID_PREF, prefs.getString(CARD_ID_PREF, ""));
        data.put(TASK_TITLE_PREF, prefs.getString(TASK_TITLE_PREF, ""));
        data.put(TASK_IS_DONE_PREF, prefs.getBoolean(TASK_IS_DONE_PREF, false));
        data.put(TASK_POSITION_PREF, prefs.getInt(TASK_POSITION_PREF, 0));
        return data;
    }

    public static void clearTaskData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public static void saveWeekEndDate(Context context, Date date) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(CARD_WEEK_END_DATE_PREF, date.getTime());
        editor.apply();
    }

    public static Date getWeekEndDate(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long weekEndDate = prefs.getLong(CARD_WEEK_END_DATE_PREF, new Date().getTime());
        return new Date(weekEndDate);
    }
}
