package ru.sergeykamyshov.weekplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.presenters.CardTitlePresenter;

import static ru.sergeykamyshov.weekplanner.activities.CardActivity.EXTRA_CARD_ID;
import static ru.sergeykamyshov.weekplanner.activities.CardActivity.EXTRA_CARD_TITLE;

public class CardTitleActivity extends AppCompatActivity {

    private CardTitlePresenter mPresenter;
    private EditText mTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_title);

        setActionBar();
        init();
    }

    private void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_set_card_title);
        }
    }

    private void init() {
        mTitle = findViewById(R.id.txt_card_title);

        Intent intent = getIntent();
        String cardId = intent.getStringExtra(EXTRA_CARD_ID);
        String cardTitle = intent.getStringExtra(EXTRA_CARD_TITLE);

        mPresenter = new CardTitlePresenter(cardId, cardTitle);
        mPresenter.attachView(this);
        mPresenter.viewReady();
    }

    public void setCardTitleOnOpen(String cardTitle) {
        mTitle.setText(cardTitle);
        mTitle.setSelection(mTitle.getText().length());
    }

    public String getCardTitle() {
        return mTitle.getText().toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
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
                mPresenter.saveCardTitle();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
