package ru.sergeykamyshov.weekplanner;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class WeekPlannerApplication extends Application {

    private static WeekPlannerApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Realm.init(instance);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
