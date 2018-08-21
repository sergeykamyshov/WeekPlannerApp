package ru.sergeykamyshov.weekplanner;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.sergeykamyshov.weekplanner.database.RealmMigrationImpl;

public class WeekPlannerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .migration(new RealmMigrationImpl())
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
