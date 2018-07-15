package ru.sergeykamyshov.weekplanner.fragments;

import android.content.Intent;
import android.view.View;

import java.util.Date;

import ru.sergeykamyshov.weekplanner.activities.CardTitleActivity;
import ru.sergeykamyshov.weekplanner.utils.DateUtils;

import static ru.sergeykamyshov.weekplanner.activities.CardActivity.EXTRA_NEW_CARD_FLAG;
import static ru.sergeykamyshov.weekplanner.activities.CardActivity.EXTRA_NEXT_WEEK_FLAG;
import static ru.sergeykamyshov.weekplanner.activities.CardActivity.EXTRA_POSITION;
import static ru.sergeykamyshov.weekplanner.activities.CardActivity.EXTRA_WEEK_END_DATE;
import static ru.sergeykamyshov.weekplanner.activities.CardActivity.EXTRA_WEEK_START_DATE;

public class NextWeekFragment extends AbstractWeekFragment {

    public static NextWeekFragment newInstance() {
        return new NextWeekFragment();
    }

    @Override
    Date getWeekStartDate(Date date) {
        return DateUtils.getNextWeekStartDate(date);
    }

    @Override
    Date getWeekEndDate(Date date) {
        return DateUtils.getNextWeekEndDate(date);
    }

    @Override
    View.OnClickListener getOnFabClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переходим к созданию новой карточки для следующей недели
                Intent intent = new Intent(getContext(), CardTitleActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra(EXTRA_NEW_CARD_FLAG, true);
                intent.putExtra(EXTRA_POSITION, mWeekRecyclerAdapter.getItemCount());
                intent.putExtra(EXTRA_NEXT_WEEK_FLAG, true);
                intent.putExtra(EXTRA_WEEK_START_DATE, mWeekStartDate);
                intent.putExtra(EXTRA_WEEK_END_DATE, mWeekEndDate);
                getContext().startActivity(intent);
            }
        };
    }
}
