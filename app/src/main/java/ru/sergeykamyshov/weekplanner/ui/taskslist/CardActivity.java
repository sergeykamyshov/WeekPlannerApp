package ru.sergeykamyshov.weekplanner.ui.taskslist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.data.db.model.Task;
import ru.sergeykamyshov.weekplanner.data.prefs.SharedPreferencesUtils;
import ru.sergeykamyshov.weekplanner.ui.card.CardTitleActivity;
import ru.sergeykamyshov.weekplanner.ui.custom.EmptyRecyclerView;
import ru.sergeykamyshov.weekplanner.ui.dialogs.DialogFactory;
import ru.sergeykamyshov.weekplanner.ui.dialogs.imports.weeks.WeekPicker;
import ru.sergeykamyshov.weekplanner.ui.task.TaskActivity;

import static ru.sergeykamyshov.weekplanner.ui.task.TaskActivity.EXTRA_TASK_ID;
import static ru.sergeykamyshov.weekplanner.ui.task.TaskActivity.EXTRA_TASK_POSITION;

public class CardActivity extends AppCompatActivity {

    public static final String EXTRA_CARD_ID = "cardId";
    public static final String EXTRA_CARD_TITLE = "cardTitle";
    public static final String EXTRA_WEEK_START_DATE = "weekStartDate";
    public static final String EXTRA_WEEK_END_DATE = "weekEndDate";
    public static final String EXTRA_NEW_CARD_FLAG = "newCardFlag";
    public static final String EXTRA_POSITION = "position";
    public static final String EXTRA_ARCHIVE_FLAG = "archiveFlag";
    public static final String EXTRA_NEXT_WEEK_FLAG = "nextWeekFlag";

    private CardPresenter mPresenter;
    private CardRecyclerAdapter mAdapter;
    private String mCardId;
    private EmptyRecyclerView mRecyclerView;
    private ItemTouchHelper mItemTouchHelper;
    private View mColorLine;
    private FrameLayout mDefaultToolbarLayout;
    private FrameLayout mEditToolbarLayout;

    private Toolbar mDefaultToolbar;
    private Toolbar mEditToolbar;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        mFab = findViewById(R.id.fab_add_task);

        mDefaultToolbarLayout = findViewById(R.id.toolbar_default);
        mEditToolbarLayout = findViewById(R.id.toolbar_edit);
        mEditToolbarLayout.setVisibility(View.GONE);

        mDefaultToolbar = findViewById(R.id.toolbar_tasks_list);
        mEditToolbar = findViewById(R.id.toolbar_tasks_list_edit);

        setSupportActionBar(mDefaultToolbar);

        mCardId = getIntent().getStringExtra(EXTRA_CARD_ID);
        mPresenter = new CardPresenter(mCardId);
        mPresenter.attachView(this);

        mColorLine = findViewById(R.id.v_color_line);

        // Создаем и настраиваем RecyclerView
        mRecyclerView = findViewById(R.id.recycler_tasks);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setEmptyView(findViewById(R.id.v_empty_task_list));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // Создаем и настраиваем адаптер
        List<Task> tasks = Realm.getDefaultInstance().copyFromRealm(mPresenter.getCard().getTasks());
        CardRecyclerAdapter adapter = new CardRecyclerAdapter(
                this,
                mCardId,
                tasks,
                new CardRecyclerAdapter.Callback() {
                    @Override
                    public void onClick(Task task, int position) {
                        Intent intent = new Intent(CardActivity.this, TaskActivity.class);
                        intent.putExtra(EXTRA_CARD_ID, mCardId);
                        intent.putExtra(EXTRA_TASK_ID, task.getId());
                        intent.putExtra(EXTRA_TASK_POSITION, position);
                        startActivity(intent);
                    }

                    @Override
                    public void onSelect() {
                        mEditToolbarLayout.setVisibility(View.VISIBLE);
                        mDefaultToolbarLayout.setVisibility(View.GONE);
                        setSupportActionBar(mEditToolbar);
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle("");
                        }

                        mFab.hide();
                    }

                    @Override
                    public void onResetSelect() {
                        mDefaultToolbarLayout.setVisibility(View.VISIBLE);
                        mEditToolbarLayout.setVisibility(View.GONE);
                        setSupportActionBar(mDefaultToolbar);

                        mFab.show();
                    }

                    @Override
                    public void taskChanged(Task task) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.insertOrUpdate(task);
                        realm.commitTransaction();
                    }

                    @Override
                    public void onTaskDismiss(Task task, int position) {
                        // Сохраняем данные перед обновлением
                        saveUndoData(task, position);

                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        RealmList<Task> realmList = mPresenter.getCard().getTasks();
                        // Удаляем из задачу из списка
                        Task removedTaskFromList = realmList.remove(position);
                        // Удаляем из задачу realm
                        if (removedTaskFromList.isValid()) {
                            removedTaskFromList.deleteFromRealm();
                        }
                        realm.commitTransaction();

                        showUndoSnackbar();
                    }

                    @Override
                    public void onDrag(RecyclerView.ViewHolder holder) {
                        mItemTouchHelper.startDrag(holder);
                    }
                }
        );
        mRecyclerView.setAdapter(adapter);
        mPresenter.setAdapter(adapter);
        mAdapter = adapter;

        // Добавляем возможность перемещать задачи в списке
        ItemTouchHelper.Callback itemTouchCallback = new TaskItemTouchHelper(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        // Настраиваем ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mPresenter.getCard().getTitle());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.fillColorLine();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Обновляем заголовок карточки
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mPresenter.getCard().getTitle());
        }
        mAdapter.setTasks(Realm.getDefaultInstance().copyFromRealm(mPresenter.getCard().getTasks()));
        showUndoSnackbar();
    }

    @Override
    protected void onDestroy() {
        clearPrefs();
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mEditToolbarLayout.getVisibility() == View.VISIBLE) {
            getMenuInflater().inflate(R.menu.menu_tasks_list_edit, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_tasks_list, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_delete_card:
                mPresenter.deleteCard();
                finish();
                return true;
            case R.id.action_import_task:
                // Сохраняем id карточки в которую будем добавлять импортируемую задачу
                SharedPreferencesUtils.saveCardIdForImportTask(this, mCardId);
                WeekPicker weekPicker = (WeekPicker) DialogFactory.getWeekPicker();
                weekPicker.setImportTask(true);
                weekPicker.show(getSupportFragmentManager(), null);
                return true;
            case R.id.action_set_card_title:
                Intent intent = new Intent(this, CardTitleActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra(EXTRA_CARD_ID, mPresenter.getCard().getId());
                intent.putExtra(EXTRA_CARD_TITLE, mPresenter.getCard().getTitle());
                startActivity(intent);
                return true;
            case R.id.action_check_all_tasks:
                Toast.makeText(this, "Check all tasks", Toast.LENGTH_SHORT).show();
                mAdapter.checkSelectedItems(true);
                return true;
            case R.id.action_delete_all_tasks:
                Toast.makeText(this, "Delete all tasks", Toast.LENGTH_SHORT).show();

                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                Set<Task> selectedItems = mAdapter.getSelectedItems();
                RealmList<Task> realmList = mPresenter.getCard().getTasks();

                // Отбираем задачи для удаления по выделенным задачам
                List<Task> taskForRemove = new ArrayList<>();
                for (Task selectedTask : selectedItems) {
                    for (Task task : realmList) {
                        if (selectedTask.getId().equals(task.getId())) {
                            taskForRemove.add(task);
                        }
                    }
                }
                // Удаляем из списка карточки
                realmList.removeAll(taskForRemove);
                // Удаляем из realm
                for (Task task : taskForRemove) {
                    task.deleteFromRealm();
                }
                realm.commitTransaction();

                mAdapter.deleteSelectedItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addNewTaskAction(View view) {
        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra(TaskActivity.EXTRA_CARD_ID, mCardId);
        startActivity(intent);
    }

    public void showUndoSnackbar() {
        if (hasUndoData()) {
            Snackbar snackbar = Snackbar.make(mRecyclerView, getString(R.string.snackbar_task_deleted), Snackbar.LENGTH_LONG);
            // Востанавливаем задачу если пользователь нажал "Отменить"
            snackbar.setAction(getString(R.string.snackbar_task_undo), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createTaskFromPrefs();
                }
            });
            // Очищаем SharedPreferences если пользователь не востановил задачу
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    if (event != DISMISS_EVENT_ACTION && event != DISMISS_EVENT_CONSECUTIVE) {
                        clearPrefs();
                    }
                }
            });
            snackbar.show();
        }
    }

    public void updateList() {
        mAdapter.setTasks(Realm.getDefaultInstance().copyFromRealm(mPresenter.getCard().getTasks()));
    }

    public void setLineColor(String color) {
        mColorLine.setBackgroundColor(Color.parseColor(color));
    }

    public void saveUndoData(Task task, int position) {
        SharedPreferencesUtils.saveTaskData(this, "", task, position);
    }

    public boolean hasUndoData() {
        return SharedPreferencesUtils.hasTaskData(this);
    }

    public void createTaskFromPrefs() {
        Map<String, Object> data = SharedPreferencesUtils.getTaskData(this);

        int position = (int) data.get(SharedPreferencesUtils.TASK_POSITION_PREF);

        Realm realm = Realm.getDefaultInstance();
        // Востанавливаем задачу по ранее сохраненым данным
        realm.beginTransaction();
        Task task = realm.createObject(Task.class, UUID.randomUUID().toString());
        task.setTitle((String) data.get(SharedPreferencesUtils.TASK_TITLE_PREF));
        task.setDone((Boolean) data.get(SharedPreferencesUtils.TASK_IS_DONE_PREF));
        mPresenter.getCard().getTasks().add(position, task);
        realm.commitTransaction();

        mAdapter.insertItemToPosition(realm.copyFromRealm(task), position);
        SharedPreferencesUtils.clearTaskData(this);
    }

    public void clearPrefs() {
        if (SharedPreferencesUtils.hasTaskData(this)) {
            SharedPreferencesUtils.clearTaskData(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (mAdapter.isSelectable()) {
            mAdapter.cancelSelect();
        } else {
            super.onBackPressed();
        }
    }
}
