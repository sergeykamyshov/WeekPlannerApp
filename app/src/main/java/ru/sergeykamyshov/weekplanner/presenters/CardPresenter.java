package ru.sergeykamyshov.weekplanner.presenters;

import android.support.v7.app.AppCompatActivity;

import java.util.Map;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import ru.sergeykamyshov.weekplanner.activities.CardActivity;
import ru.sergeykamyshov.weekplanner.adapters.CardRecyclerAdapter;
import ru.sergeykamyshov.weekplanner.model.Card;
import ru.sergeykamyshov.weekplanner.model.Task;
import ru.sergeykamyshov.weekplanner.utils.TaskItemTouchHelperAdapter;
import ru.sergeykamyshov.weekplanner.utils.TaskSharedPreferencesUtils;

public class CardPresenter implements Presenter, TaskItemTouchHelperAdapter {

    private CardActivity mActivity;
    private CardRecyclerAdapter mAdapter;
    private String mCardId;
    private Card mCard;
    private TaskSharedPreferencesUtils mPrefs;

    public CardPresenter(String cardId) {
        mCardId = cardId;
    }

    @Override
    public void attachView(AppCompatActivity activity) {
        mActivity = (CardActivity) activity;
        mPrefs = new TaskSharedPreferencesUtils(mActivity);
    }

    @Override
    public void viewReady() {

    }

    @Override
    public void detachView() {
        mPrefs = null;
        mAdapter = null;
        mActivity = null;
    }

    public void setAdapter(CardRecyclerAdapter adapter) {
        mAdapter = adapter;
    }

    public Card getCard() {
        if (mCard == null) {
            Realm realm = Realm.getDefaultInstance();
            mCard = realm.where(Card.class).equalTo("id", mCardId).findFirst();
        }
        return mCard;
    }

    public void deleteCard() {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmList<Task> tasks = getCard().getTasks();
                // Удаляем все задачи, которые были внутри карточки
                for (int i = tasks.size() - 1; tasks.size() > 0; i--) {
                    Task task = tasks.get(i);
                    if (task != null) {
                        task.deleteFromRealm();
                    }
                }
                getCard().deleteFromRealm();
            }
        });
    }

    public void createTaskFromPrefs() {
        Map<String, Object> data = mPrefs.getData();

        // Создаем новую задачу с ранее сохранеными данными
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Task task = realm.createObject(Task.class, UUID.randomUUID().toString());
        task.setTitle((String) data.get(TaskSharedPreferencesUtils.TASK_TITLE_PREF));
        task.setDone((Boolean) data.get(TaskSharedPreferencesUtils.TASK_IS_DONE_PREF));
        realm.commitTransaction();

        int position = (int) data.get(TaskSharedPreferencesUtils.TASK_POSITION_PREF);
        mAdapter.insertItemToPosition(task, position);
        // Костыль. Не разобрался почему при добавлении первой задачи RecyclerView не обновляется через notifyItemInserted
        if (getCard().getTasks().size() == 1) {
            mAdapter.notifyDataSetChanged();
        }
        mPrefs.clearData();
    }

    public boolean hasUndoData() {
        return mPrefs.hasData();
    }

    public void clearPrefs() {
        mPrefs.clearData();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        mAdapter.onItemMove(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(final int position) {
        mAdapter.onItemDismiss(position);
        mActivity.showUndoSnackbar();
    }

    @Override
    public void onItemChecked(int position) {
        mAdapter.onItemChecked(position);
    }

}
