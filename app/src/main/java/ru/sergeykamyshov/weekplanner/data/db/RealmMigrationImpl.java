package ru.sergeykamyshov.weekplanner.data.db;

import java.util.Locale;

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
        if (oldVersion == 1) {
            schema.get("Card")
                    .addField("color", String.class);
            oldVersion++;
        }
        if (oldVersion < newVersion) {
            throw new IllegalStateException(String.format(Locale.US, "Migration missing from v%d to v%d", oldVersion, newVersion));
        }
    }
}
