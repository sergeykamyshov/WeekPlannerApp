package ru.sergeykamyshov.weekplanner;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.sergeykamyshov.weekplanner.database.RealmMigrationImpl;

public class WeekPlannerApplication extends Application {

    private static WeekPlannerApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Realm.init(instance);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .migration(new RealmMigrationImpl())
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
