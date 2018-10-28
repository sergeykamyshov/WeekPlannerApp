package ru.sergeykamyshov.weekplanner.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ru.sergeykamyshov.weekplanner.model.Task;
import ru.sergeykamyshov.weekplanner.utils.Const;

public class SharedPreferencesUtils {

    // TODO: Переименовать prefs и не забыть про migration
    // Prefs которые периодически очищаются
    public static final String TEMP_PREFS_NAME = "ru.sergeykamyshov.weekplanner.utils.SharedPreferencesUtils";
    public static final String CARD_ID_PREF = "cardId";
    public static final String TASK_TITLE_PREF = "taskTitle";
    public static final String TASK_IS_DONE_PREF = "taskIsDone";
    public static final String TASK_POSITION_PREF = "taskPosition";

    // Prefs которые должны храниться
    public static final String KEEP_PREFS_NAME = "ru.sergeykamyshov.weekplanner.utils.KeepSharedPreferencesUtils";
    public static final String IMPORT_CARD_WEEK_END_DATE_PREF = "importEndWeekDate";
    public static final String IMPORT_CARD_ID_PREF = "importCardId";

    public static void saveTaskData(Context context, String cardId, Task task, int position) {
        SharedPreferences prefs = context.getSharedPreferences(TEMP_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CARD_ID_PREF, cardId);
        editor.putString(TASK_TITLE_PREF, task.getTitle());
        editor.putBoolean(TASK_IS_DONE_PREF, task.isDone());
        editor.putInt(TASK_POSITION_PREF, position);
        editor.apply();
    }

    public static boolean hasTaskData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(TEMP_PREFS_NAME, Context.MODE_PRIVATE);
        String cardId = prefs.getString(CARD_ID_PREF, null);
        return cardId != null;
    }

    public static Map<String, Object> getTaskData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(TEMP_PREFS_NAME, Context.MODE_PRIVATE);
        HashMap<String, Object> data = new HashMap<>();
        data.put(CARD_ID_PREF, prefs.getString(CARD_ID_PREF, Const.EMPTY));
        data.put(TASK_TITLE_PREF, prefs.getString(TASK_TITLE_PREF, Const.EMPTY));
        data.put(TASK_IS_DONE_PREF, prefs.getBoolean(TASK_IS_DONE_PREF, false));
        data.put(TASK_POSITION_PREF, prefs.getInt(TASK_POSITION_PREF, 0));
        return data;
    }

    public static void clearTaskData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(TEMP_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public static void saveWeekEndDate(Context context, Date date) {
        SharedPreferences prefs = context.getSharedPreferences(KEEP_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(IMPORT_CARD_WEEK_END_DATE_PREF, date.getTime());
        editor.apply();
    }

    public static Date getWeekEndDate(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(KEEP_PREFS_NAME, Context.MODE_PRIVATE);
        long weekEndDate = prefs.getLong(IMPORT_CARD_WEEK_END_DATE_PREF, new Date().getTime());
        return new Date(weekEndDate);
    }

    public static void saveCardIdForImportTask(Context context, String cardId) {
        SharedPreferences prefs = context.getSharedPreferences(KEEP_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(IMPORT_CARD_ID_PREF, cardId);
        editor.apply();
    }

    public static String getCardIdForImportTask(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(KEEP_PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(IMPORT_CARD_ID_PREF, Const.EMPTY);
    }
}
