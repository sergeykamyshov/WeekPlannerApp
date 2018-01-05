package ru.sergeykamyshov.weekplanner.database;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class RealmMigrationImpl implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if (oldVersion == 0) {
            schema.get("Card")
                    .addField("position", int.class);
            oldVersion++;
        }
    }
}
