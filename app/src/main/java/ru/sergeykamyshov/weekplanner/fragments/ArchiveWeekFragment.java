package ru.sergeykamyshov.weekplanner.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import io.realm.RealmResults;
import ru.sergeykamyshov.weekplanner.R;
import ru.sergeykamyshov.weekplanner.activities.CardActivity;
import ru.sergeykamyshov.weekplanner.model.Card;
import ru.sergeykamyshov.weekplanner.utils.DateUtils;

import static ru.sergeykamyshov.weekplanner.activities.CardActivity.EXTRA_ARCHIVE_FLAG;
import static ru.sergeykamyshov.weekplanner.activities.CardActivity.EXTRA_NEW_CARD_FLAG;
import static ru.sergeykamyshov.weekplanner.activities.CardActivity.EXTRA_POSITION;
import static ru.sergeykamyshov.weekplanner.activities.CardActivity.EXTRA_WEEK_END_DATE;

public class ArchiveWeekFragment extends AbstractWeekFragment {

    public static ArchiveWeekFragment newInstance() {
        return new ArchiveWeekFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_archive, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_select_week:
                DatePickerFragment datePicker = new DatePickerFragment();
                datePicker.setCallBack(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        GregorianCalendar calendar = new GregorianCalendar(year, month, dayOfMonth);
                        mWeekStartDate = DateUtils.getWeekStartDate(calendar.getTime());
                        mWeekEndDate = DateUtils.getWeekEndDate(calendar.getTime());
                        RealmResults<Card> cards = mRealm.where(Card.class)
                                .between("creationDate", mWeekStartDate, mWeekEndDate)
                                .sort("position")
                                .findAll();
                        fillCardsPositions(cards);
                        List<Card> copyFromRealmCards = mRealm.copyFromRealm(cards);
                        mWeekRecyclerAdapter.setCards(copyFromRealmCards);
                    }
                });
                datePicker.show(getFragmentManager(), "datePicker");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    Date getWeekStartDate(Date date) {
        return DateUtils.getPreviousWeekStartDate(date);
    }

    @Override
    Date getWeekEndDate(Date date) {
        return DateUtils.getPreviousWeekEndDate(date);
    }

    @Override
    View.OnClickListener getOnFabClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переходим к созданию новой карточки на выбранной неделе архива
                Intent intent = new Intent(getContext(), CardActivity.class);
                intent.putExtra(EXTRA_NEW_CARD_FLAG, true);
                intent.putExtra(EXTRA_POSITION, mWeekRecyclerAdapter.getItemCount());
                intent.putExtra(EXTRA_ARCHIVE_FLAG, true);
                intent.putExtra(EXTRA_WEEK_END_DATE, mWeekEndDate);
                getContext().startActivity(intent);
            }
        };
    }
}
