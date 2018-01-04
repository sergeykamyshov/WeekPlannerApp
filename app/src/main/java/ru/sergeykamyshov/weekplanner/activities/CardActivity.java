package ru.sergeykamyshov.weekplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.adapters.CardRecyclerAdapter;
import ru.sergeykamyshov.weekplanner.model.Card;
import ru.sergeykamyshov.weekplanner.model.Task;
import ru.sergeykamyshov.weekplanner.utils.TaskItemTouchHelper;
import ru.sergeykamyshov.weekplanner.views.EmptyRecyclerView;

import static ru.sergeykamyshov.weekplanner.activities.TaskActivity.EXTRA_TASK_ID;

public class CardActivity extends AppCompatActivity {

    public static final String EXTRA_CARD_ID = "cardId";
    public static final String EXTRA_CARD_TITLE = "cardTitle";
    public static final String EXTRA_WEEK_START_DATE = "weekStartDate";
    public static final String EXTRA_WEEK_END_DATE = "weekEndDate";
    public static final String EXTRA_NEW_CARD_FLAG = "newCardFlag";
    public static final String EXTRA_ARCHIVE_FLAG = "archiveFlag";
    public static final String EXTRA_NEXT_WEEK_FLAG = "nextWeekFlag";

    private CardRecyclerAdapter mAdapter;
    private Realm mRealm;
    private Card mCard;
    private String mCardId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        // Получаем данные из вызываемого интента
        Intent intent = getIntent();
        boolean isNewCard = intent.getBooleanExtra(EXTRA_NEW_CARD_FLAG, false);
        final boolean isArchive = intent.getBooleanExtra(EXTRA_ARCHIVE_FLAG, false);
        final Date weekEndDate = (Date) intent.getSerializableExtra(EXTRA_WEEK_END_DATE);
        final boolean isNextWeek = intent.getBooleanExtra(EXTRA_NEXT_WEEK_FLAG, false);
        final Date weekStartDate = (Date) intent.getSerializableExtra(EXTRA_WEEK_START_DATE);

        mRealm = Realm.getDefaultInstance();
        if (isNewCard) {
            // Создаем новую карточку
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    mCardId = UUID.randomUUID().toString();
                    Card card = realm.createObject(Card.class, mCardId);
                    if (isArchive) {
                        card.setCreationDate(weekEndDate);
                    } else if (isNextWeek) {
                        card.setCreationDate(weekStartDate);
                    }
                    realm.insertOrUpdate(card);
                }
            });
        } else {
            mCardId = intent.getStringExtra(EXTRA_CARD_ID);
        }
        mCard = mRealm.where(Card.class).equalTo("id", mCardId).findFirst();

        // Настраиваем ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mCard != null ? mCard.getTitle() : "");
        }

        // Устанавлеиваем адаптр
        EmptyRecyclerView recyclerTasks = findViewById(R.id.recycler_tasks);
        recyclerTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerTasks.setEmptyView(findViewById(R.id.layout_empty_task_list));
        mAdapter = new CardRecyclerAdapter(this, mCard.getTasks(), new OnTaskItemClickListener() {
            // Реализация обработчика нажатия задачи в списке
            @Override
            public void onClick(Task task) {
                Intent intent = new Intent(CardActivity.this, TaskActivity.class);
                intent.putExtra(EXTRA_CARD_ID, mCardId);
                intent.putExtra(EXTRA_TASK_ID, task.getId());
                startActivity(intent);
            }
        });
        recyclerTasks.setAdapter(mAdapter);
        recyclerTasks.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // Добавляем возможность перемещать задачи в списке
        ItemTouchHelper.Callback itemTouchHelperCallback = new TaskItemTouchHelper(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerTasks);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Удаляем пустую карточку
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (mCard.isValid() && (mCard.getTitle() == null || mCard.getTitle().isEmpty()) && mCard.getTasks().isEmpty()) {
                    mCard.deleteFromRealm();
                }
            }
        });
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
}
