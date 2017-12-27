package ru.sergeykamyshov.weekplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.UUID;

import io.realm.Realm;
import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.model.Card;
import ru.sergeykamyshov.weekplanner.model.Task;

public class TaskActivity extends AppCompatActivity {

    public static final String EXTRA_CARD_ID = "cardId";
    public static final String EXTRA_TASK_ID = "taskId";

    private Realm mRealm;
    private String mCardId;
    private String mTaskId;
    private EditText mTaskTitleEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        // Настраиваем ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.title_task));
        }

        mRealm = Realm.getDefaultInstance();

        mTaskTitleEditText = findViewById(R.id.txt_task_title);

        Intent intent = getIntent();
        mCardId = intent.getStringExtra(EXTRA_CARD_ID);
        mTaskId = intent.getStringExtra(EXTRA_TASK_ID);

        if (mTaskId != null) {
            // Загружаем данные из задачи
            Task task = mRealm.where(Task.class).equalTo("id", mTaskId).findFirst();
            String taskTitle = task != null ? task.getTitle() : null;
            mTaskTitleEditText.setText(taskTitle);
            mTaskTitleEditText.setSelection(mTaskTitleEditText.getText().length());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save_task:
                final String taskTitle = mTaskTitleEditText.getText().toString();
                if (mTaskId != null) {
                    // Задача уже была создана и ее трубуется только обновить
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Task task = mRealm.where(Task.class).equalTo("id", mTaskId).findFirst();
                            if (task != null) {
                                task.setTitle(taskTitle);
                                mRealm.insertOrUpdate(task);
                            }
                        }
                    });
                } else {
                    // Создаем новую задачу с заполненными данными
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Card card = realm.where(Card.class).equalTo("id", mCardId).findFirst();
                            if (card != null) {
                                Task task = realm.createObject(Task.class, UUID.randomUUID().toString());
                                task.setTitle(taskTitle);
                                card.addTask(task);
                                realm.insertOrUpdate(card);
                            }
                        }
                    });
                }
                finish();
                return true;
            case R.id.action_delete_task:
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Task task = mRealm.where(Task.class).equalTo("id", mTaskId).findFirst();
                        if (task != null) {
                            task.deleteFromRealm();
                        }
                    }
                });
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
