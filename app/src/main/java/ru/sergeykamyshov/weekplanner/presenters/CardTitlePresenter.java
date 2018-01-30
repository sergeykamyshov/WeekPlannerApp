package ru.sergeykamyshov.weekplanner.presenters;

import android.support.v7.app.AppCompatActivity;

import io.realm.Realm;
import ru.sergeykamyshov.weekplanner.activities.CardTitleActivity;
import ru.sergeykamyshov.weekplanner.model.Card;

public class CardTitlePresenter implements Presenter {

    Realm mRealm = Realm.getDefaultInstance();
    CardTitleActivity mView;
    private String mCardId;
    private String mCardTitle;

    public CardTitlePresenter(String cardId, String cardTitle) {
        mCardId = cardId;
        mCardTitle = cardTitle;
    }

    @Override
    public void attachView(AppCompatActivity activity) {
        mView = (CardTitleActivity) activity;
    }

    @Override
    public void viewReady() {
        mView.setCardTitleOnOpen(mCardTitle);
    }

    @Override
    public void detachView() {
        mView = null;
    }

    public void saveCardTitle() {
        final String cardTitle = mView.getCardTitle();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Card card = realm.where(Card.class).equalTo("id", mCardId).findFirst();
                if (card != null) {
                    card.setTitle(cardTitle);
                    realm.insertOrUpdate(card);
                }
            }
        });
    }
}
