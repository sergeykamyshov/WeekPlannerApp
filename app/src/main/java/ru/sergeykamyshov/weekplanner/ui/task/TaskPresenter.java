package ru.sergeykamyshov.weekplanner.ui.task;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.UUID;

import io.realm.Realm;
import ru.sergeykamyshov.weekplanner.data.db.model.Card;
import ru.sergeykamyshov.weekplanner.data.db.model.Task;
import ru.sergeykamyshov.weekplanner.data.prefs.SharedPreferencesUtils;
import ru.sergeykamyshov.weekplanner.ui.base.Presenter;

public class TaskPresenter implements Presenter {

    private TaskActivity mView;
    private Realm mRealm = Realm.getDefaultInstance();
    private String mCardId;

    private String mTaskId;
    private int mTaskPosition;
    private boolean addNewTaskToStart = false;

    public TaskPresenter(String cardId, String taskId, int taskPosition) {
        mCardId = cardId;
        mTaskId = taskId;
        mTaskPosition = taskPosition;
    }

    @Override
    public void attachView(AppCompatActivity activity) {
        mView = (TaskActivity) activity;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mView);
        addNewTaskToStart = prefs.getBoolean("pref_add_new_task_to_start", false);
    }

    @Override
    public void viewReady() {
        mView.setTaskTitle();
    }

    @Override
    public void detachView() {
        mView = null;
    }

    public String getTaskTitle() {
        if (TextUtils.isEmpty(mTaskId)) {
            return "";
        }
        Task task = mRealm.where(Task.class).equalTo("id", mTaskId).findFirst();
        return task != null ? task.getTitle() : "";

    }

    public void saveTask() {
        String taskTitle = mView.getTaskTitle();
        if (mTaskId != null) {
            // Задача уже была создана и ее трубуется только обновить
            updateTaskTitle(mTaskId, taskTitle);
        } else {
            // Создаем новую задачу с заполненными данными
            createTask(taskTitle);
        }
    }

    public void updateTaskTitle(final String taskId, final String taskTitle) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task task = mRealm.where(Task.class).equalTo("id", taskId).findFirst();
                if (task != null) {
                    task.setTitle(taskTitle);
                    mRealm.insertOrUpdate(task);
                }
            }
        });
    }

    public void createTask(final String taskTitle) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Card card = realm.where(Card.class).equalTo("id", mCardId).findFirst();
                if (card != null) {
                    Task task = realm.createObject(Task.class, UUID.randomUUID().toString());
                    task.setTitle(taskTitle);
                    if (addNewTaskToStart) {
                        card.addTask(0, task);
                    } else {
                        card.addTask(task);
                    }
                    realm.insertOrUpdate(card);
                }
            }
        });
    }

    public void deleteTask() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task task = mRealm.where(Task.class).equalTo("id", mTaskId).findFirst();
                if (task != null) {
                    task.deleteFromRealm();
                }
            }
        });
    }

    public void saveTaskDataToPrefs() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task task = mRealm.where(Task.class).equalTo("id", mTaskId).findFirst();
                if (task != null) {
                    SharedPreferencesUtils.saveTaskData(mView, mCardId, task, mTaskPosition);
                }
            }
        });
    }
}
