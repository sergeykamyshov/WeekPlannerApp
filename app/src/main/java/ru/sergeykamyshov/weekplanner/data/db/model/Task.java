package ru.sergeykamyshov.weekplanner.data.db.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Task extends RealmObject {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private boolean done;
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
