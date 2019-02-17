package ru.sergeykamyshov.weekplanner.ui.taskslist;

import android.support.v7.app.AppCompatActivity;

import io.realm.Realm;
import io.realm.RealmList;
import ru.sergeykamyshov.weekplanner.data.db.model.Card;
import ru.sergeykamyshov.weekplanner.data.db.model.Task;
import ru.sergeykamyshov.weekplanner.ui.base.Presenter;
import ru.sergeykamyshov.weekplanner.utils.CardUtils;

public class CardPresenter implements Presenter {

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

    public void fillColorLine() {
        String color = CardUtils.getCardColor(mView, mCardId);
        mView.setLineColor(color);
    }

}
