package ru.sergeykamyshov.weekplanner.data.db;

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
        } else if (oldVersion == 1) {
            schema.get("Card")
                    .addField("color", String.class);
            oldVersion++;
        }
    }
}
