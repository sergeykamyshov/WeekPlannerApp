package ru.sergeykamyshov.weekplanner.ui.base;

import android.support.v7.app.AppCompatActivity;

public interface Presenter {

    void attachView(AppCompatActivity activity);

    void viewReady();

    void detachView();
}
