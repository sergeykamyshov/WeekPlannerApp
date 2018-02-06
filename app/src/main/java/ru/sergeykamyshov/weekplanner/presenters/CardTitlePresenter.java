package ru.sergeykamyshov.weekplanner.presenters;

import android.support.v7.app.AppCompatActivity;

import java.util.UUID;

import io.realm.Realm;
import ru.sergeykamyshov.weekplanner.activities.CardTitleActivity;
import ru.sergeykamyshov.weekplanner.model.Card;

public class CardTitlePresenter implements Presenter {

    private Realm mRealm = Realm.getDefaultInstance();
    private CardTitleActivity mView;

    @Override
    public void attachView(AppCompatActivity activity) {
        mView = (CardTitleActivity) activity;
    }

    @Override
    public void viewReady() {
        mView.setCardTitleOnOpen();
    }

    @Override
    public void detachView() {
        mView = null;
    }

    public void saveCardTitle() {
        final String cardTitle = mView.getCardTitle();
        if (mView.isNewCardFlag()) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    String randomCardId = UUID.randomUUID().toString();
                    Card card = realm.createObject(Card.class, randomCardId);
                    card.setTitle(cardTitle);
                    card.setPosition(mView.getCardPosition());
                    if (mView.isArchive()) {
                        card.setCreationDate(mView.getWeekEndDate());
                    } else if (mView.isNextWeek()) {
                        card.setCreationDate(mView.getWeekStartDate());
                    }
                    realm.insertOrUpdate(card);
                    mView.setCardId(randomCardId);
                }
            });
        } else {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Card card = realm.where(Card.class).equalTo("id", mView.getCardId()).findFirst();
                    if (card != null) {
                        card.setTitle(cardTitle);
                        realm.insertOrUpdate(card);
                    }
                }
            });
        }
    }
}
