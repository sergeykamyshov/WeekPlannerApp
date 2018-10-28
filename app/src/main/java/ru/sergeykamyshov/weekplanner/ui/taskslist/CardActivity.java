package ru.sergeykamyshov.weekplanner.ui.taskslist;

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

import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.ui.dialogs.DialogFactory;
import ru.sergeykamyshov.weekplanner.ui.dialogs.imports.weeks.WeekPicker;
import ru.sergeykamyshov.weekplanner.data.db.model.Task;
import ru.sergeykamyshov.weekplanner.data.prefs.SharedPreferencesUtils;
import ru.sergeykamyshov.weekplanner.ui.card.CardTitleActivity;
import ru.sergeykamyshov.weekplanner.ui.task.TaskActivity;
import ru.sergeykamyshov.weekplanner.ui.custom.EmptyRecyclerView;

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
    private DataView mAdapter;
    private String mCardId;
    private EmptyRecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        mCardId = getIntent().getStringExtra(EXTRA_CARD_ID);
        mPresenter = new CardPresenter(mCardId);
        mPresenter.attachView(this);

        // Создаем и настраиваем RecyclerView
        mRecyclerView = findViewById(R.id.recycler_tasks);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setEmptyView(findViewById(R.id.layout_empty_task_list));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // Создаем и настраиваем адаптер
        CardRecyclerAdapter adapter = new CardRecyclerAdapter(this, mPresenter.getCard().getTasks(), new OnTaskItemClickListener() {
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
        mRecyclerView.setAdapter(adapter);
        mPresenter.setAdapter(adapter);
        // Сохраняем ссылку на адаптер с интерфейсом DataView - для ограничения доступа
        mAdapter = adapter;

        // Добавляем возможность перемещать задачи в списке
        ItemTouchHelper.Callback itemTouchCallback = new TaskItemTouchHelper(mPresenter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        // Настраиваем ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mPresenter.getCard().getTitle());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Обновляем заголовок карточки
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mPresenter.getCard().getTitle());
        }
        mAdapter.refresh();
        showUndoSnackbar();
    }

    @Override
    protected void onDestroy() {
        mPresenter.clearPrefs();
        mPresenter.detachView();
        super.onDestroy();
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
        }
        return super.onOptionsItemSelected(item);
    }

    public void addNewTaskAction(View view) {
        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra(TaskActivity.EXTRA_CARD_ID, mCardId);
        startActivity(intent);
    }

    public void showUndoSnackbar() {
        if (mPresenter.hasUndoData()) {
            Snackbar snackbar = Snackbar.make(mRecyclerView, getString(R.string.snackbar_task_deleted), Snackbar.LENGTH_LONG);
            // Востанавливаем задачу если пользователь нажал "Отменить"
            snackbar.setAction(getString(R.string.snackbar_task_undo), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.createTaskFromPrefs();
                }
            });
            // Очищаем SharedPreferences если пользователь не востановил задачу
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    if (event != DISMISS_EVENT_ACTION && event != DISMISS_EVENT_CONSECUTIVE) {
                        mPresenter.clearPrefs();
                    }
                }
            });
            snackbar.show();
        }
    }

    public void updateList() {
        mAdapter.refresh();
    }

}
