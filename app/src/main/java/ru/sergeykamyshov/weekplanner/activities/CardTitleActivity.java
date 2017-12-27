package ru.sergeykamyshov.weekplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import io.realm.Realm;
import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.model.Card;

import static ru.sergeykamyshov.weekplanner.activities.CardActivity.EXTRA_CARD_ID;
import static ru.sergeykamyshov.weekplanner.activities.CardActivity.EXTRA_CARD_TITLE;

public class CardTitleActivity extends AppCompatActivity {

    private String mCardId;
    private EditText mTitleEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_title);

        // Настраиваем ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_set_card_title);
        }

        Intent intent = getIntent();
        mCardId = intent.getStringExtra(EXTRA_CARD_ID);
        String cardTitle = intent.getStringExtra(EXTRA_CARD_TITLE);
        mTitleEditText = findViewById(R.id.txt_card_title);
        mTitleEditText.setText(cardTitle);
        mTitleEditText.setSelection(mTitleEditText.getText().length());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_card_title, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save_card_title:
                final String cardTitle = mTitleEditText.getText().toString();
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Card card = realm.where(Card.class).equalTo("id", mCardId).findFirst();
                        if (card != null) {
                            card.setTitle(cardTitle);
                            realm.insertOrUpdate(card);
                        }
                    }
                });
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
