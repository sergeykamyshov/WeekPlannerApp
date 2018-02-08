package ru.sergeykamyshov.weekplanner.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import ru.sergeykamyshov.weekplanner.model.Task;

public class TaskSharedPreferencesUtils {

    public static final String PREFS_NAME = "ru.sergeykamyshov.weekplanner.utils.TaskSharedPreferencesUtils";
    public static final String CARD_ID_PREF = "cardId";
    public static final String TASK_TITLE_PREF = "taskTitle";
    public static final String TASK_IS_DONE_PREF = "taskIsDone";

    private AppCompatActivity mActivity;

    public TaskSharedPreferencesUtils(AppCompatActivity activity) {
        mActivity = activity;
    }

    public void saveData(String cardId, Task task) {
        SharedPreferences prefs = mActivity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CARD_ID_PREF, cardId);
        editor.putString(TASK_TITLE_PREF, task.getTitle());
        editor.putBoolean(TASK_IS_DONE_PREF, task.isDone());
        editor.apply();
    }

    public boolean hasData() {
        SharedPreferences prefs = mActivity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String cardId = prefs.getString(CARD_ID_PREF, null);
        return cardId != null;
    }

    public Map<String, Object> getData() {
        SharedPreferences prefs = mActivity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        HashMap<String, Object> data = new HashMap<>();
        data.put(CARD_ID_PREF, prefs.getString(CARD_ID_PREF, ""));
        data.put(TASK_TITLE_PREF, prefs.getString(TASK_TITLE_PREF, ""));
        data.put(TASK_IS_DONE_PREF, prefs.getBoolean(TASK_IS_DONE_PREF, false));
        return data;
    }

    public void clearData() {
        SharedPreferences prefs = mActivity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}
