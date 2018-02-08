package ru.sergeykamyshov.weekplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Map;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.adapters.CardRecyclerAdapter;
import ru.sergeykamyshov.weekplanner.model.Card;
import ru.sergeykamyshov.weekplanner.model.Task;
import ru.sergeykamyshov.weekplanner.utils.TaskItemTouchHelper;
import ru.sergeykamyshov.weekplanner.utils.TaskItemTouchHelperAdapter;
import ru.sergeykamyshov.weekplanner.utils.TaskSharedPreferencesUtils;
import ru.sergeykamyshov.weekplanner.views.EmptyRecyclerView;

import static ru.sergeykamyshov.weekplanner.activities.TaskActivity.EXTRA_TASK_ID;
import static ru.sergeykamyshov.weekplanner.activities.TaskActivity.EXTRA_TASK_POSITION;

public class CardActivity extends AppCompatActivity implements TaskItemTouchHelperAdapter {

    public static final String EXTRA_CARD_ID = "cardId";
    public static final String EXTRA_CARD_TITLE = "cardTitle";
    public static final String EXTRA_WEEK_START_DATE = "weekStartDate";
    public static final String EXTRA_WEEK_END_DATE = "weekEndDate";
    public static final String EXTRA_NEW_CARD_FLAG = "newCardFlag";
    public static final String EXTRA_POSITION = "position";
    public static final String EXTRA_ARCHIVE_FLAG = "archiveFlag";
    public static final String EXTRA_NEXT_WEEK_FLAG = "nextWeekFlag";

    private CardRecyclerAdapter mAdapter;
    private Realm mRealm;
    private Card mCard;
    private String mCardId;
    private EmptyRecyclerView mRecyclerView;
    private TaskSharedPreferencesUtils mTaskSharedPreferencesUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        Intent intent = getIntent();
        mCardId = intent.getStringExtra(EXTRA_CARD_ID);

        // Создаем и настраиваем RecyclerView
        mRecyclerView = findViewById(R.id.recycler_tasks);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setEmptyView(findViewById(R.id.layout_empty_task_list));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mRealm = Realm.getDefaultInstance();
        mCard = mRealm.where(Card.class).equalTo("id", mCardId).findFirst();
        // Создаем и настраиваем адаптер
        mAdapter = new CardRecyclerAdapter(this, mCard.getTasks(), new OnTaskItemClickListener() {
            // Реализация обработчика нажатия задачи в списке
            @Override
            public void onClick(Task task, int position) {
                Intent intent = new Intent(CardActivity.this, TaskActivity.class);
                intent.putExtra(EXTRA_CARD_ID, mCardId);
                intent.putExtra(EXTRA_TASK_ID, task.getId());
                intent.putExtra(EXTRA_TASK_POSITION, position);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        // Добавляем возможность перемещать задачи в списке
        ItemTouchHelper.Callback itemTouchCallback = new TaskItemTouchHelper(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mTaskSharedPreferencesUtils = new TaskSharedPreferencesUtils(this);

        // Настраиваем ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mCard != null ? mCard.getTitle() : "");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Обновляем заголовок карточки
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mCard.getTitle());
        }
        mAdapter.notifyDataSetChanged();

        if (mTaskSharedPreferencesUtils.hasData()) {
            Snackbar snackbar = Snackbar.make(mRecyclerView, getString(R.string.snackbar_task_deleted), Snackbar.LENGTH_LONG);
            // Востанавливаем задачу если пользователь нажал "Отменить"
            snackbar.setAction(getString(R.string.snackbar_task_undo), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Map<String, Object> data = mTaskSharedPreferencesUtils.getData();

                    // Создаем новую задачу с ранее сохранеными данными
                    mRealm.beginTransaction();
                    Task task = mRealm.createObject(Task.class, UUID.randomUUID().toString());
                    task.setTitle((String) data.get(TaskSharedPreferencesUtils.TASK_TITLE_PREF));
                    task.setDone((Boolean) data.get(TaskSharedPreferencesUtils.TASK_IS_DONE_PREF));
                    int position = (int) data.get(TaskSharedPreferencesUtils.TASK_POSITION_PREF);
                    mRealm.commitTransaction();

                    mAdapter.insertItemToPosition(task, position);
                    // Костыль. Не разобрался почему при добавлении первой задачи RecyclerView не обновляется через notifyItemInserted
                    if (mCard.getTasks().size() == 1) {
                        mAdapter.notifyDataSetChanged();
                    }
                    mTaskSharedPreferencesUtils.clearData();
                }
            });
            // Очищаем SharedPreferences если пользователь не востановил задачу
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    if (event != DISMISS_EVENT_ACTION) {
                        mTaskSharedPreferencesUtils.clearData();
                    }
                }
            });
            snackbar.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_card, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_delete_card:
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmList<Task> tasks = mCard.getTasks();
                        // Удаляем все задачи, которые были внутри карточки
                        for (int i = tasks.size() - 1; tasks.size() > 0; i--) {
                            Task task = tasks.get(i);
                            if (task != null) {
                                task.deleteFromRealm();
                            }
                        }
                        mCard.deleteFromRealm();
                    }
                });
                finish();
                return true;
            case R.id.action_set_card_title:
                Intent intent = new Intent(this, CardTitleActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra(EXTRA_CARD_ID, mCard.getId());
                intent.putExtra(EXTRA_CARD_TITLE, mCard.getTitle());
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addNewTaskAction(View view) {
        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra(TaskActivity.EXTRA_CARD_ID, mCardId);
        startActivity(intent);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        mAdapter.onItemMove(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(final int position) {
        final Task removedTask = mAdapter.onItemDismiss(position);

        Snackbar snackbar = Snackbar.make(mRecyclerView, getString(R.string.snackbar_task_deleted), Snackbar.LENGTH_LONG);
        // Даем возможность пользователю востановить удаленную задачу
        snackbar.setAction(getString(R.string.snackbar_task_undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.insertItemToPosition(removedTask, position);
            }
        });
        // Удаляем задачу из Realm если пользователь не востановил ее
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event != DISMISS_EVENT_ACTION) {
                    mRealm.beginTransaction();
                    removedTask.deleteFromRealm();
                    mRealm.commitTransaction();
                }
            }
        });
        snackbar.show();
    }
}
