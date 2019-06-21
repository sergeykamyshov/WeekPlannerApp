package ru.sergeykamyshov.weekplanner.ui.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import io.realm.Realm;
import ru.sergeykamyshov.weekplanner.R;

public class TaskActivity extends AppCompatActivity {

    public static final String EXTRA_CARD_ID = "cardId";
    public static final String EXTRA_TASK_ID = "taskId";
    public static final String EXTRA_TASK_POSITION = "taskPosition";

    private Realm mRealm;
    private TaskPresenter mPresenter;
    private EditText mTaskTitleEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        setActionBar();
        init();
    }

    private void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.title_task));
        }
    }

    private void init() {
        mRealm = Realm.getDefaultInstance();
        mTaskTitleEditText = findViewById(R.id.txt_task_title);

        Intent intent = getIntent();
        String cardId = intent.getStringExtra(EXTRA_CARD_ID);
        String taskId = intent.getStringExtra(EXTRA_TASK_ID);
        int position = intent.getIntExtra(EXTRA_TASK_POSITION, 0);

        mPresenter = new TaskPresenter(cardId, taskId, position);
        mPresenter.attachView(this);
        mPresenter.viewReady();
    }

    public void setTaskTitle() {
        String taskTitle = mPresenter.getTaskTitle();
        mTaskTitleEditText.setText(taskTitle);
        mTaskTitleEditText.setSelection(taskTitle.length());
        mTaskTitleEditText.requestFocus();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
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
            case R.id.action_delete_task:
                mPresenter.saveTaskDataToPrefs();
                mPresenter.deleteTask();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getTaskTitle() {
        return mTaskTitleEditText.getText().toString();
    }

    public void cancelTaskAction(View view) {
        onBackPressed();
    }

    public void saveTaskAction(View view) {
        mPresenter.saveTask();
        finish();
    }
}
