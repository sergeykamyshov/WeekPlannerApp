package ru.sergeykamyshov.weekplanner.ui.card;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.ui.taskslist.CardActivity;

import static ru.sergeykamyshov.weekplanner.ui.taskslist.CardActivity.EXTRA_ARCHIVE_FLAG;
import static ru.sergeykamyshov.weekplanner.ui.taskslist.CardActivity.EXTRA_CARD_ID;
import static ru.sergeykamyshov.weekplanner.ui.taskslist.CardActivity.EXTRA_CARD_TITLE;
import static ru.sergeykamyshov.weekplanner.ui.taskslist.CardActivity.EXTRA_NEW_CARD_FLAG;
import static ru.sergeykamyshov.weekplanner.ui.taskslist.CardActivity.EXTRA_NEXT_WEEK_FLAG;
import static ru.sergeykamyshov.weekplanner.ui.taskslist.CardActivity.EXTRA_POSITION;
import static ru.sergeykamyshov.weekplanner.ui.taskslist.CardActivity.EXTRA_WEEK_END_DATE;
import static ru.sergeykamyshov.weekplanner.ui.taskslist.CardActivity.EXTRA_WEEK_START_DATE;

public class CardTitleActivity extends AppCompatActivity {

    private CardTitlePresenter mPresenter;
    private EditText mTitle;
    private View mColorLine;

    // Intent Extras
    private boolean mNewCardFlag;
    private int mCardPosition;
    private String mCardId;
    private String mCardTitle;
    private boolean mIsArchive;
    private Date mWeekEndDate;
    private boolean mIsNextWeek;
    private Date mWeekStartDate;
    private RecyclerView mRecyclerView;

    private String mChosenColor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_title);

        init();
        setActionBar();
        mPresenter.fillColorLine();

        mRecyclerView = findViewById(R.id.recycler_colors);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<String> colors = Arrays.asList(getResources().getStringArray(R.array.card_color_entries));
        ColorRecyclerAdapter adapter = new ColorRecyclerAdapter(this, colors, new OnColorClickListener() {
            @Override
            public void onClick(String color) {
                mColorLine.setBackgroundColor(Color.parseColor(color));
                mChosenColor = color;
            }
        });
        mRecyclerView.setAdapter(adapter);
    }

    private void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_card);
        }
    }

    private void init() {
        mTitle = findViewById(R.id.txt_card_title);
        mColorLine = findViewById(R.id.v_color_line);

        getIntentExtras();

        mPresenter = new CardTitlePresenter();
        mPresenter.attachView(this);
        mPresenter.viewReady();
    }

    private void getIntentExtras() {
        Intent intent = getIntent();
        mNewCardFlag = intent.getBooleanExtra(EXTRA_NEW_CARD_FLAG, false);
        mCardPosition = intent.getIntExtra(EXTRA_POSITION, 0);
        mCardId = intent.getStringExtra(EXTRA_CARD_ID);
        mCardTitle = intent.getStringExtra(EXTRA_CARD_TITLE);
        mIsArchive = intent.getBooleanExtra(EXTRA_ARCHIVE_FLAG, false);
        mWeekEndDate = (Date) intent.getSerializableExtra(EXTRA_WEEK_END_DATE);
        mIsNextWeek = intent.getBooleanExtra(EXTRA_NEXT_WEEK_FLAG, false);
        mWeekStartDate = (Date) intent.getSerializableExtra(EXTRA_WEEK_START_DATE);
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setCardTitleOnOpen() {
        mTitle.setText(mCardTitle);
        mTitle.setSelection(mTitle.getText().length());
    }

    public void cancelCardTitleAction(View view) {
        onBackPressed();
    }

    public void saveCardTitleAction(View view) {
        mPresenter.saveCardTitle();
        mPresenter.saveCardColor(mChosenColor);

        Intent intent = new Intent(this, CardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(EXTRA_CARD_ID, mCardId);
        startActivity(intent);
    }

    public boolean isNewCardFlag() {
        return mNewCardFlag;
    }

    public void setLineColor(String color) {
        mChosenColor = color;
        mColorLine.setBackgroundColor(Color.parseColor(color));
    }

    public String getCardTitle() {
        return mTitle.getText().toString();
    }

    public void setCardId(String cardId) {
        mCardId = cardId;
    }

    public String getCardId() {
        return mCardId;
    }

    public int getCardPosition() {
        return mCardPosition;
    }

    public boolean isArchive() {
        return mIsArchive;
    }

    public Date getWeekEndDate() {
        return mWeekEndDate;
    }

    public boolean isNextWeek() {
        return mIsNextWeek;
    }

    public Date getWeekStartDate() {
        return mWeekStartDate;
    }
}
