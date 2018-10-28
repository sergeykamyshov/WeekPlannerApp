package ru.sergeykamyshov.weekplanner.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import ru.sergeykamyshov.weekplanner.R;

public class MainActivitySharedPreferencesUtils {

    // TODO: Переименовать prefs и не забыть про migration
    private static final String PREFS = "ru.sergeykamyshov.weekplanner.MainActivity";
    private static final String TITLE_KEY = "title";
    private Context mContext;

    public MainActivitySharedPreferencesUtils(Context context) {
        mContext = context;
    }

    public String getTitleFromPrefs() {
        SharedPreferences preferences = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return preferences.getString(TITLE_KEY, mContext.getString(R.string.app_name));
    }

    public void writeTitleToPrefs(String title) {
        SharedPreferences preferences = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("title", title);
        editor.apply();
    }
}
