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
import ru.sergeykamyshov.weekplanner.data.prefs.SharedPreferencesUtils;
import ru.sergeykamyshov.weekplanner.utils.TaskItemTouchHelperAdapter;

public class CardPresenter implements Presenter, TaskItemTouchHelperAdapter {

    private CardActivity mView;
    private CardRecyclerAdapter mAdapter;
    private String mCardId;
    private Card mCard;

    public CardPresenter(String cardId) {
        mCardId = cardId;
    }

    @Override
    public void attachView(AppCompatActivity activity) {
        mView = (CardActivity) activity;
    }

    @Override
    public void viewReady() {

    }

    @Override
    public void detachView() {
        mAdapter = null;
        mView = null;
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
        Map<String, Object> data = SharedPreferencesUtils.getTaskData(mView);

        // Создаем новую задачу с ранее сохранеными данными
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Task task = realm.createObject(Task.class, UUID.randomUUID().toString());
        task.setTitle((String) data.get(SharedPreferencesUtils.TASK_TITLE_PREF));
        task.setDone((Boolean) data.get(SharedPreferencesUtils.TASK_IS_DONE_PREF));
        realm.commitTransaction();

        int position = (int) data.get(SharedPreferencesUtils.TASK_POSITION_PREF);
        mAdapter.insertItemToPosition(task, position);
        // Костыль. Не разобрался почему при добавлении первой задачи RecyclerView не обновляется через notifyItemInserted
        if (getCard().getTasks().size() == 1) {
            mAdapter.notifyDataSetChanged();
        }
        SharedPreferencesUtils.clearTaskData(mView);
    }

    public boolean hasUndoData() {
        return SharedPreferencesUtils.hasTaskData(mView);
    }

    public void clearPrefs() {
        if (mView != null && SharedPreferencesUtils.hasTaskData(mView)) {
            SharedPreferencesUtils.clearTaskData(mView);
        }
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        mAdapter.onItemMove(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(final int position) {
        mAdapter.onItemDismiss(position);
        mView.showUndoSnackbar();
    }

    @Override
    public void onItemChecked(int position) {
        mAdapter.onItemChecked(position);
    }

}
