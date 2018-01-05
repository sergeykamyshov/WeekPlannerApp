package ru.sergeykamyshov.weekplanner.model;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Card extends RealmObject {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private String title;
    private Date creationDate = new Date();
    private int position;
    private RealmList<Task> tasks = new RealmList<>();

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public RealmList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(RealmList<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }
}
